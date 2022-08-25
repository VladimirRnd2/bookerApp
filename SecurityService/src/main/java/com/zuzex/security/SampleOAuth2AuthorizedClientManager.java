package com.zuzex.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SampleOAuth2AuthorizedClientManager implements OAuth2AuthorizedClientManager {

    private static final OAuth2AuthorizedClientProvider DEFAULT_AUTHORIZED_CLIENT_PROVIDER = OAuth2AuthorizedClientProviderBuilder.builder()
            .authorizationCode()
            .refreshToken()
            .clientCredentials()
            .password()
            .build();

    private final ClientRegistrationRepository clientRegistrationRepository;

    private final OAuth2AuthorizedClientRepository authorizedClientRepository;

    private OAuth2AuthorizedClientProvider authorizedClientProvider;

    private Function<OAuth2AuthorizeRequest, Map<String, Object>> contextAttributesMapper;

    private OAuth2AuthorizationSuccessHandler authorizationSuccessHandler;

    private OAuth2AuthorizationFailureHandler authorizationFailureHandler;

    public SampleOAuth2AuthorizedClientManager(ClientRegistrationRepository clientRegistrationRepository,
                                                OAuth2AuthorizedClientRepository authorizedClientRepository) {
        Assert.notNull(clientRegistrationRepository, "clientRegistrationRepository cannot be null");
        Assert.notNull(authorizedClientRepository, "authorizedClientRepository cannot be null");
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.authorizedClientRepository = authorizedClientRepository;
        this.authorizedClientProvider = DEFAULT_AUTHORIZED_CLIENT_PROVIDER;
        this.contextAttributesMapper = new DefaultOAuth2AuthorizedClientManager.DefaultContextAttributesMapper();
        this.authorizationSuccessHandler = (authorizedClient, principal, attributes) -> authorizedClientRepository
                .saveAuthorizedClient(authorizedClient, principal,
                        (HttpServletRequest) attributes.get(HttpServletRequest.class.getName()),
                        (HttpServletResponse) attributes.get(HttpServletResponse.class.getName()));
        this.authorizationFailureHandler = new RemoveAuthorizedClientOAuth2AuthorizationFailureHandler(
                (clientRegistrationId, principal, attributes) -> authorizedClientRepository.removeAuthorizedClient(
                        clientRegistrationId, principal,
                        (HttpServletRequest) attributes.get(HttpServletRequest.class.getName()),
                        (HttpServletResponse) attributes.get(HttpServletResponse.class.getName())));
    }


    @Override
    public OAuth2AuthorizedClient authorize(OAuth2AuthorizeRequest authorizeRequest) {
        Assert.notNull(authorizeRequest, "authorizeRequest cannot be null");
        String clientRegistrationId = authorizeRequest.getClientRegistrationId();
        OAuth2AuthorizedClient authorizedClient = authorizeRequest.getAuthorizedClient();
        Authentication principal = authorizeRequest.getPrincipal();
        HttpServletRequest servletRequest = getHttpServletRequestOrDefault(authorizeRequest.getAttributes());
        Assert.notNull(servletRequest, "servletRequest cannot be null");
        HttpServletResponse servletResponse = getHttpServletResponseOrDefault(authorizeRequest.getAttributes());
        Assert.notNull(servletResponse, "servletResponse cannot be null");
        OAuth2AuthorizationContext.Builder contextBuilder;
        if (authorizedClient != null) {
            contextBuilder = OAuth2AuthorizationContext.withAuthorizedClient(authorizedClient);
        }
        else {
            authorizedClient = this.authorizedClientRepository.loadAuthorizedClient(clientRegistrationId, principal,
                    servletRequest);
            if (authorizedClient != null) {
                contextBuilder = OAuth2AuthorizationContext.withAuthorizedClient(authorizedClient);
            }
            else {
                ClientRegistration clientRegistration = this.clientRegistrationRepository
                        .findByRegistrationId(clientRegistrationId);
                Assert.notNull(clientRegistration,
                        "Could not find ClientRegistration with id '" + clientRegistrationId + "'");
                contextBuilder = OAuth2AuthorizationContext.withClientRegistration(clientRegistration);
            }
        }
        // @formatter:off
        OAuth2AuthorizationContext authorizationContext = contextBuilder.principal(principal)
                .attributes((attributes) -> {
                    Map<String, Object> contextAttributes = this.contextAttributesMapper.apply(authorizeRequest);
                    if (!CollectionUtils.isEmpty(contextAttributes)) {
                        attributes.putAll(contextAttributes);
                    }
                })
                .build();
        // @formatter:on
        try {
            authorizedClient = this.authorizedClientProvider.authorize(authorizationContext);
        }
        catch (OAuth2AuthorizationException ex) {
            this.authorizationFailureHandler.onAuthorizationFailure(ex, principal,
                    createAttributes(servletRequest, servletResponse));
            throw ex;
        }
        if (authorizedClient != null) {
            this.authorizationSuccessHandler.onAuthorizationSuccess(authorizedClient, principal,
                    createAttributes(servletRequest, servletResponse));
        }
        else {
            // In the case of re-authorization, the returned `authorizedClient` may be
            // null if re-authorization is not supported.
            // For these cases, return the provided
            // `authorizationContext.authorizedClient`.
            if (authorizationContext.getAuthorizedClient() != null) {
                return authorizationContext.getAuthorizedClient();
            }
        }
        return authorizedClient;
    }

    private static Map<String, Object> createAttributes(HttpServletRequest servletRequest,
                                                        HttpServletResponse servletResponse) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(HttpServletRequest.class.getName(), servletRequest);
        attributes.put(HttpServletResponse.class.getName(), servletResponse);
        return attributes;
    }

    private static HttpServletRequest getHttpServletRequestOrDefault(Map<String, Object> attributes) {
        HttpServletRequest servletRequest = (HttpServletRequest) attributes.get(HttpServletRequest.class.getName());
        if (servletRequest == null) {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes instanceof ServletRequestAttributes) {
                servletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
            }
        }
        return servletRequest;
    }

    private static HttpServletResponse getHttpServletResponseOrDefault(Map<String, Object> attributes) {
        HttpServletResponse servletResponse = (HttpServletResponse) attributes.get(HttpServletResponse.class.getName());
        if (servletResponse == null) {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes instanceof ServletRequestAttributes) {
                servletResponse = ((ServletRequestAttributes) requestAttributes).getResponse();
            }
        }
        return servletResponse;
    }

    public void setAuthorizedClientProvider(OAuth2AuthorizedClientProvider authorizedClientProvider) {
        Assert.notNull(authorizedClientProvider, "authorizedClientProvider cannot be null");
        this.authorizedClientProvider = authorizedClientProvider;
    }

    public void setContextAttributesMapper(
            Function<OAuth2AuthorizeRequest, Map<String, Object>> contextAttributesMapper) {
        Assert.notNull(contextAttributesMapper, "contextAttributesMapper cannot be null");
        this.contextAttributesMapper = contextAttributesMapper;
    }

    public void setAuthorizationSuccessHandler(OAuth2AuthorizationSuccessHandler authorizationSuccessHandler) {
        Assert.notNull(authorizationSuccessHandler, "authorizationSuccessHandler cannot be null");
        this.authorizationSuccessHandler = authorizationSuccessHandler;
    }

    public void setAuthorizationFailureHandler(OAuth2AuthorizationFailureHandler authorizationFailureHandler) {
        Assert.notNull(authorizationFailureHandler, "authorizationFailureHandler cannot be null");
        this.authorizationFailureHandler = authorizationFailureHandler;
    }

    public static class DefaultContextAttributesMapper
            implements Function<OAuth2AuthorizeRequest, Map<String, Object>> {

        @Override
        public Map<String, Object> apply(OAuth2AuthorizeRequest authorizeRequest) {
            Map<String, Object> contextAttributes = Collections.emptyMap();
            HttpServletRequest servletRequest = getHttpServletRequestOrDefault(authorizeRequest.getAttributes());
            String scope = servletRequest.getParameter(OAuth2ParameterNames.SCOPE);
            if (StringUtils.hasText(scope)) {
                contextAttributes = new HashMap<>();
                contextAttributes.put(OAuth2AuthorizationContext.REQUEST_SCOPE_ATTRIBUTE_NAME,
                        StringUtils.delimitedListToStringArray(scope, " "));
            }
            return contextAttributes;
        }

    }
}
