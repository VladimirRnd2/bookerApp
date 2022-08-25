package com.zuzex.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zuzex.model.dto.Author;
import com.zuzex.model.dto.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Client;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookControllerTest {

    private final String FIRST_BOOK_JSON = "{\"id\":1,\"title\":\"Abba\",\"date\":\"12.12.2020\",\"isRead\":false,\"authors\":[{\"id\":1,\"name\":\"Duma\"}]}";
    private final String SECOND_BOOK_JSON = "{\"id\":2,\"title\":\"Pudge\",\"date\":\"21.21.2012\",\"isRead\":true,\"authors\":null}";
    private final String LIST_OF_BOOK = "[{\"id\":1,\"title\":\"Abba\",\"date\":\"12.12.2020\",\"isRead\":false,\"authors\":[{\"id\":1,\"name\":\"Duma\"}]},{\"id\":2,\"title\":\"Pudge\",\"date\":\"21.21.2012\",\"isRead\":true,\"authors\":null}]";

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

//    private static WireMockRule wireMockRule = new WireMockRule(8089);
//
//    @RegisterExtension
//    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
//            .options(wireMockConfig().dynamicPort())
//            .build();
//
//    @DynamicPropertySource
//    static void configureProperties(DynamicPropertyRegistry registry) {
//        registry.add("http://127.0.0.1:8084", wireMockServer::baseUrl);
//    }

    Book firstBook;
    Book secondBook;
    Author author;

    @BeforeEach
    void setup() {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

//        bookClient = MockMvcWebTestClient.bindToApplicationContext(this.context)
//                .apply(springSecurity())
//                .configureClient()
//                .filter(basicAuthentication("vova", "vova"))
//                .build();

        author = new Author();
        author.setId(1L);
        author.setName("Duma");

        firstBook = new Book();
        firstBook.setId(1L);
        firstBook.setTitle("Abba");
        firstBook.setDate("12.12.2020");
        firstBook.setIsRead(false);
        firstBook.setAuthors(List.of(author));
        firstBook.setUsers(null);

        secondBook = new Book();
        secondBook.setId(2L);
        secondBook.setTitle("Pudge");
        secondBook.setDate("21.21.2012");
        secondBook.setIsRead(true);
        secondBook.setAuthors(null);
        secondBook.setUsers(null);
    }

    @Test
    void shouldCreateMockMvc() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String fBook = objectMapper.writeValueAsString(firstBook);
            String sBook = objectMapper.writeValueAsString(secondBook);
            String listBooks = objectMapper.writeValueAsString(List.of(firstBook,secondBook));
            System.out.println(fBook);
            System.out.println(sBook);
            System.out.println(listBooks);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        assertNotNull(mockMvc);
    }

//    @Test
//    void createNewBook() throws Exception {
//
//
//
//        bookClient
//                .mutateWith(mockOAuth2Client("feign-client-oidc"))
//                .mutateWith(mockUser("vova").roles("USER"))
//                .post()
//                .uri("/{service}/user/book/search", "google")
//                .contentType(MediaType.APPLICATION_JSON)
//                .body("{\"title\": \"Abba\"}", Book.class)
//                .syncBody("{\"title\": \"Abba\"}")
//                .exchange()
//                .expectStatus().is3xxRedirection();
//                .expectBody(Book.class);
//        when(bookClient.createNewBook(any(String.class), any(BookRequest.class),any(String.class))).thenReturn(firstBook);
//        this.mockMvc.perform(post("/{service}/user/book/search", "google")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .content("{\"title\": \"Abba\"}")
//                        .with(oauth2Client("feign-client-oidc"))
//                        .with(user("vova").roles("USER")))
//                .andDo(print())
//                .andExpect(status().is2xxSuccessful())
//                .andExpect(jsonPath("$.title").value("Abba"))
//                .andExpect(jsonPath("$.date").value("12.12.2020"));
//        verify(bookClient).createNewBook(any(String.class), any(BookRequest.class),any(String.class));
//    }
//
    @Test
    void getAllBooks() throws Exception {
//        wireMockServer.stubFor(WireMock.get("/kek").willReturn(aResponse().withHeader("Content-type",MediaType.APPLICATION_JSON_VALUE)));
//        when(bookClient.getAllBooks(any(String.class),any(String.class))).thenReturn(List.of(firstBook,secondBook));

        this.mockMvc.perform(get("/{service}/user/book/","google").characterEncoding(StandardCharsets.UTF_8)
                        .with(oauth2Client("feign-client-oidc"))
                        .with(user("vova").roles("USER")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].title").value("Abba"))
                .andExpect(jsonPath("$[1].title").value("Pudge"))
                .andExpect(jsonPath("$[0].date").value("12.12.2020"))
                .andExpect(jsonPath("$[1].date").value("21.21.2012"));
    }
//
//    @Test
//    void getAllReadBooks() throws Exception {
//        when(bookClient.getAllReadBooks(any(String.class),any(String.class))).thenReturn(List.of(secondBook));
//
//        this.mockMvc.perform(get("/{service}/user/book/read","google")
//                        .with(oauth2Client("feign-client-oidc"))
//                        .with(user("vova").roles("USER")))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.size()").value(1))
//                .andExpect(jsonPath("$[0].date").value("21.21.2012"))
//                .andExpect(jsonPath("$[0].isRead").value(true))
//                .andExpect(jsonPath("$[0].title").value("Pudge"));
//    }
//
//    @Test
//    void getAllNoReadBooks() throws Exception {
//        when(bookClient.getAllNoReadBooks(any(String.class),any(String.class))).thenReturn(List.of(firstBook));
//
//        this.mockMvc.perform(get("/{service}/user/book/noread","google")
//                        .with(oauth2Client("feign-client-oidc"))
//                        .with(user("vova").roles("USER")))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.size()").value(1))
//                .andExpect(jsonPath("$[0].date").value("12.12.2020"))
//                .andExpect(jsonPath("$[0].isRead").value(false))
//                .andExpect(jsonPath("$[0].title").value("Abba"));
//    }
//
//    @Test
//    void getBookById() throws Exception {
//        when(bookClient.getGoogleBookById(any(Long.class),any(String.class),any(String.class))).thenReturn(firstBook);
//
//        this.mockMvc.perform(get("/{service}/user/book/"+1L ,"google")
//                        .with(oauth2Client("feign-client-oidc"))
//                        .with(user("vova").roles("USER")))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.date").value("12.12.2020"))
//                .andExpect(jsonPath("$.isRead").value(false))
//                .andExpect(jsonPath("$.title").value("Abba"));
//    }
//
//    @Test
//    void addToReadBooks() throws Exception {
//        when(bookClient.getGoogleBookById(any(Long.class),any(String.class),any(String.class))).thenReturn(firstBook);
//
//        this.mockMvc.perform(get("/{service}/user/book/" + 1L + "/isread" ,"google")
//                        .with(oauth2Client("feign-client-oidc"))
//                        .with(user("vova").roles("USER")))
//                .andExpect(status().isOk());
//
//
//    }
//
//    @Test
//    void deleteFromReadBooks() throws Exception {
//        when(bookClient.getGoogleBookById(any(Long.class),any(String.class),any(String.class))).thenReturn(firstBook);
//
//        this.mockMvc.perform(get("/{service}/user/book/" + 2L + "/isnoread" ,"google")
//                        .with(oauth2Client("feign-client-oidc"))
//                        .with(user("vova").roles("USER")))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void getAllAuthorsByBookId() throws Exception {
//        when(bookClient.getAllAuthorsByBookId(any(Long.class),any(String.class),any(String.class))).thenReturn(List.of(author));
//
//        this.mockMvc.perform(get("/{service}/user/book/1/authors","google")
//                        .with(oauth2Client("feign-client-oidc"))
//                        .with(user("vova").roles("USER")))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.size()").value(1))
//                .andExpect(jsonPath("$[0].name").value("Duma"));
//    }
//
//    @Test
//    void deleteBookById() throws Exception {
//
//        this.mockMvc.perform(delete("/{service}/user/book/"+1L ,"google")
//                        .with(oauth2Client("feign-client-oidc"))
//                        .with(user("vova").roles("USER")))
//                .andExpect(status().isOk());
//    }

}