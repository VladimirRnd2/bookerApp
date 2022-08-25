package com.zuzex.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zuzex.model.dto.Author;
import com.zuzex.model.dto.AuthorRequest;
import com.zuzex.model.dto.Book;
import com.zuzex.model.dto.BookRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Objects;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

@RestController
@RequestMapping("{service}/user/book")
public class BookController {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WebClient webClient;

    @Autowired
    public BookController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping(value = "/")
    public List<Book> getAllGoogleBooks(
            @RegisteredOAuth2AuthorizedClient("articles-client-oidc") OAuth2AuthorizedClient authorizedClient,
            @PathVariable(name = "service") String service
    ) {
        System.out.println("Access Token :" + Objects.requireNonNull(authorizedClient.getAccessToken()).getTokenValue());
        System.out.println("Access Token Type : " + authorizedClient.getAccessToken().getTokenType().getValue());
        System.out.println("Refresh Token :" + Objects.requireNonNull(authorizedClient.getRefreshToken()).getTokenValue());



        return this.webClient
                .get()
                .uri("http://127.0.0.1:8084/user/book/".concat(service).concat("/"))
                .attributes(oauth2AuthorizedClient(authorizedClient))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Book>>() {})
                .block();


    }

    @PostMapping("/search")
    public Book createNewGoogleBook(@RegisteredOAuth2AuthorizedClient("articles-client-oidc")  OAuth2AuthorizedClient authorizedClient,
                                    @RequestBody @Valid BookRequest bookRequest,
                                    @PathVariable(name = "service") String service) {
        return this.webClient
                .post()
                .uri("http://127.0.0.1:8084/user/book/".concat(service).concat("/search"))
                .attributes(oauth2AuthorizedClient(authorizedClient))
                .header("principal", authorizedClient.getAccessToken().getTokenValue())
                .body(Mono.just(bookRequest), BookRequest.class)
                .retrieve()
                .bodyToMono(Book.class)
                .block();
    }

    @PostMapping(value = "/authorsearch")
    public List<Book> createGoogleBooksByAuthor(@RegisteredOAuth2AuthorizedClient("articles-client-oidc")  OAuth2AuthorizedClient authorizedClient,
                                                @RequestBody @Valid AuthorRequest authorRequest,
                                                @PathVariable(name = "service") String service) {

        return this.webClient
                .post()
                .uri("http://127.0.0.1:8084/user/book/".concat(service).concat("/authorsearch"))
                .attributes(oauth2AuthorizedClient(authorizedClient))
                .body(Mono.just(authorRequest), AuthorRequest.class)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Book>>() {})
                .block();
    }
    @GetMapping(value = "/read")
    public List<Book> getAllReadGoogleBooks(@RegisteredOAuth2AuthorizedClient("articles-client-oidc")  OAuth2AuthorizedClient authorizedClient,
                                            @PathVariable(name = "service") String service) {

        return this.webClient
                .get()
                .uri("http://127.0.0.1:8084/user/book/".concat(service).concat("/read"))
                .attributes(oauth2AuthorizedClient(authorizedClient))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Book>>() {})
                .block();
    }

    @GetMapping(value = "/noread")
    public List<Book> getAllNoReadGoogleBooks(@RegisteredOAuth2AuthorizedClient("articles-client-oidc")  OAuth2AuthorizedClient authorizedClient,
                                              @PathVariable(name = "service") String service) {

        return this.webClient
                .get()
                .uri("http://127.0.0.1:8084/user/book/".concat(service).concat("/noread"))
                .attributes(oauth2AuthorizedClient(authorizedClient))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Book>>() {})
                .block();
    }

    @GetMapping(value = "/{id}")
    public Book getGoogleBookById(@PathVariable(name = "id") @Min(0) Long id,
                                  @PathVariable(name = "service") String service,
                                  @RegisteredOAuth2AuthorizedClient("articles-client-oidc")  OAuth2AuthorizedClient authorizedClient) {
        return this.webClient
                .get()
                .uri("http://127.0.0.1:8084/user/book/".concat(service).concat("/").concat(id.toString()))
                .attributes(oauth2AuthorizedClient(authorizedClient))
                .retrieve()
                .bodyToMono(Book.class)
                .block();
    }

    @GetMapping(value = "/{id}/isread")
    public Boolean addToReadGoogleBooks(@PathVariable(name = "id") @Min(0) Long id,
                                        @PathVariable(name = "service") String service,
                                        @RegisteredOAuth2AuthorizedClient("articles-client-oidc")  OAuth2AuthorizedClient authorizedClient) {
        return this.webClient.
                get()
                .uri("http://127.0.0.1:8084/user/book/" + service + "/" + id + "/isread")
                .attributes(oauth2AuthorizedClient(authorizedClient))
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
    }

    @GetMapping(value = "/{id}/isnoread")
    public Boolean deleteFromReadGoogleBooks(@PathVariable(name = "id") @Min(0) Long id,
                                             @PathVariable(name = "service") String service,
                                             @RegisteredOAuth2AuthorizedClient("articles-client-oidc")  OAuth2AuthorizedClient authorizedClient) {
        return this.webClient.
                get()
                .uri("http://127.0.0.1:8084/user/book/" + service + "/" + id + "/isnoread")
                .attributes(oauth2AuthorizedClient(authorizedClient))
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
    }

    @GetMapping(value = "/{id}/authors")
    public List<Author> getAllAuthorsByGoogleBookId(@PathVariable(name = "id") @Min(0) Long id,
                                                    @PathVariable(name = "service") String service,
                                                    @RegisteredOAuth2AuthorizedClient("articles-client-oidc")  OAuth2AuthorizedClient authorizedClient){

        return this.webClient
                .get()
                .uri("http://127.0.0.1:8084/user/book/".concat(service).concat("/").concat(id.toString()).concat("/authors"))
                .attributes(oauth2AuthorizedClient(authorizedClient))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Author>>() {})
                .block();
    }

    @DeleteMapping(value = "/{id}")
    public String deleteGoogleBook(@PathVariable(name = "id") @Min(0) Long id,
                                   @PathVariable(name = "service") String service,
                                   @RegisteredOAuth2AuthorizedClient("articles-client-oidc")  OAuth2AuthorizedClient authorizedClient) {
        return this.webClient
                .delete()
                .uri("http://127.0.0.1:8084/user/book/".concat(service).concat("/").concat(id.toString()))
                .attributes(oauth2AuthorizedClient(authorizedClient))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
