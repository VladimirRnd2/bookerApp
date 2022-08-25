package com.zuzex.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zuzex.model.dto.*;
import com.zuzex.model.entity.Author;
import com.zuzex.model.entity.Book;
import com.zuzex.model.entity.Status;
import com.zuzex.model.entity.User;
import com.zuzex.repository.reactive.AuthorReactiveRepository;
import com.zuzex.repository.reactive.BookReactiveRepository;
import com.zuzex.repository.reactive.UserReactiveRepository;
import com.zuzex.service.BookReactiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Transactional
public class BookReactiveServiceImpl implements BookReactiveService {

    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes?q=";

    private final AuthorReactiveRepository authorReactiveRepository;
    private final BookReactiveRepository bookReactiveRepository;
    private final UserReactiveRepository userReactiveRepository;
    private final RestTemplate restTemplate;
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public BookReactiveServiceImpl(AuthorReactiveRepository authorReactiveRepository,
                                   BookReactiveRepository bookReactiveRepository,
                                   UserReactiveRepository userReactiveRepository, RestTemplate restTemplate) {
        this.authorReactiveRepository = authorReactiveRepository;
        this.bookReactiveRepository = bookReactiveRepository;
        this.userReactiveRepository = userReactiveRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public Mono<Book> getBookById(Long id) {
        return bookReactiveRepository.findById(id);
    }

    @Override
    public Mono<Book> getBookByTitle(String title) {
        return bookReactiveRepository.findByTitle(title);
    }

    @Override
    public Flux<Book> getAllBooks() {
        return bookReactiveRepository.findAll();
    }

    @Override
    public Flux<Book> getAllReadBooks() {
        return bookReactiveRepository.findByIsReadEquals(true);
    }

    @Override
    public Flux<Book> getAllNoReadBooks() {
        return bookReactiveRepository.findByIsReadEquals(false);
    }

    @Override
    public Mono<Boolean> addToReadBooks(Book book) {
        if(!book.getIsRead()) {
            book.setIsRead(true);
            bookReactiveRepository.save(book);
            return Mono.just(true);
        }
        return Mono.just(false);
    }

    @Override
    public Mono<Boolean> deleteFromReadBooks(Book book) {
        if(book.getIsRead()) {
            book.setIsRead(false);
            bookReactiveRepository.save(book);
            return Mono.just(true);
        }
        return Mono.just(false);
    }

    @Override
    public Flux<Book> getBooksByDate(String date) {
        return bookReactiveRepository.findByDate(date);
    }

    @Override
    public BookResponse getBookResponse(BookRequest bookRequest) {
        String responseEntity = restTemplate.getForObject(BASE_URL.concat(bookRequest.getTitle()), String.class);
        BookResponse bookResponse = new BookResponse();
        List<AuthorResponse> authorResponseList = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(responseEntity);
            JsonNode items = root.path("items");
            for (JsonNode item : items) {
                JsonNode volumeInfo = item.path("volumeInfo");
                bookResponse.setTitle(volumeInfo.path("title").asText());
                bookResponse.setDate(volumeInfo.path("publishedDate").asText());
                JsonNode authorsName = volumeInfo.get("authors");
                for (JsonNode name : authorsName) {
                    String n = name.asText();
                    authorResponseList.add(new AuthorResponse(n));
                }
                bookResponse.setAuthors(authorResponseList);
                break;
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return bookResponse;
    }

    @Override
    public Mono<Book> createNewBook(BookResponse bookResponse, String login) {
        Mono<Book> optionalBook = bookReactiveRepository.findByTitle(bookResponse.getTitle());
        Mono<User> optionalUser = userReactiveRepository.findByLogin(login);
        Book book;
        User user;
        if(optionalUser.blockOptional().isEmpty()) {
            user = new User();
            user.setLogin(login);
            user.setBooks(new ArrayList<>());
            userReactiveRepository.save(user);
        }
        else {
            user = optionalUser.blockOptional().get();
        }
        if (optionalBook.blockOptional().isEmpty()) {
            book = new Book();
            book.setTitle(bookResponse.getTitle());
            book.setDate(bookResponse.getDate());
            book.setAuthors(getAllAuthorsFromAuthorResponse(bookResponse).collectList().block());
            book.setIsRead(false);
            book.setUsers(new ArrayList<>());
            book.setCreated(new Date());
            book.setUpdated(new Date());
            book.setStatus(Status.ACTIVE);
            book = bookReactiveRepository.save(book).block();
            user.getBooks().add(book);
            LogDto logDto = LogDto.builder()
                    .name(Objects.requireNonNull(book).getTitle())
                    .currentDate(book.getCreated())
                    .service("google")
                    .method("create")
                    .entityType("book")
                    .build();
            userReactiveRepository.save(user);
            System.out.println(logDto);
            restTemplate.postForEntity("http://127.0.0.1:8086/logs/create",logDto, void.class);
        }
        else {
            book = optionalBook.blockOptional().get();
            user.getBooks().add(book);
            userReactiveRepository.save(user);
        }
        return Mono.just(book);
    }

    @Override
    public List<Book> getBooksByAuthorId(Long id) {
        return Objects.requireNonNull(authorReactiveRepository.findById(id).block()).getBooks();
    }

    @Override
    public Mono<Author> getAuthorByName(String name) {
        return authorReactiveRepository.findByName(name);
    }

    @Override
    public Mono<Void> deleteBookById(Long id) {
        return bookReactiveRepository.deleteById(id);
    }

    @Override
    public Flux<Author> getAllAuthors() {
        return authorReactiveRepository.findAll();
    }

    @Override
    public Mono<Author> getAuthorById(Long id) {
        return authorReactiveRepository.findById(id);
    }

    @Override
    public List<BookResponse> getAllBookResponseByAuthor(AuthorRequest authorRequest) {
        String responseEntity = restTemplate.getForObject(BASE_URL.concat("+inauthor:").concat(authorRequest.getAuthorName()), String.class);
        BookResponse bookResponse = null;
        List<AuthorResponse> authorResponseList = null;
        List<BookResponse> bookResponseList = new ArrayList<>();

        try {
            JsonNode root = objectMapper.readTree(responseEntity);
            JsonNode items = root.path("items");
            for (JsonNode item : items) {
                bookResponse = new BookResponse();
                authorResponseList = new ArrayList<>();
                JsonNode volumeInfo = item.path("volumeInfo");
                bookResponse.setTitle(volumeInfo.path("title").asText());
                bookResponse.setDate(volumeInfo.path("publishedDate").asText());
                JsonNode authorsName = volumeInfo.get("authors");
                if (authorsName != null) {
                    for (JsonNode name : authorsName) {
                        String n = name.asText();
                        authorResponseList.add(new AuthorResponse(n));
                    }
                }
                bookResponse.setAuthors(authorResponseList);
                bookResponseList.add(bookResponse);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
//        Gson gson = new GsonBuilder()
//                .setPrettyPrinting()
//                .registerTypeAdapter(ListBookResponse.class, new ListBookResponseDeserializer())
//                .create();
//        ListBookResponse listBookResponse = gson.fromJson(responseEntity, ListBookResponse.class);
//        List<BookResponse> bookResponseList = listBookResponse.getBookResponseList();
        return bookResponseList;
    }

    @Override
    public Flux<Book> createNewBooksByAuthor(List<BookResponse> bookResponseList, String login) {
        AtomicInteger count = new AtomicInteger(0);
        List<Book> resultList = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(3);

        for (int i = 0; i < 3; i++) {
            executorService.submit(() -> {
                while (count.get() < bookResponseList.size()) {
                    BookResponse bookResponse = bookResponseList.get(count.getAndIncrement());
                    System.out.println(Thread.currentThread().getName());
                    resultList.add(createNewBook(bookResponse, login).block());
                }
                countDownLatch.countDown();
            });
        }
        while (countDownLatch.getCount() != 0) {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return Flux.fromIterable(resultList);
    }

    @Override
    public Map<String, Long> getAllCounts() {
        Map<String, Long> map = new HashMap<>();
        map.put("book", bookReactiveRepository.count().block());
        map.put("author", authorReactiveRepository.count().block());
        return map;
    }

    private Flux<Author> getAllAuthorsFromAuthorResponse(BookResponse bookResponse) {
        List<Author> authors = new ArrayList<>();
        Author author;
        if (bookResponse.getAuthors() != null) {
            for (AuthorResponse response : bookResponse.getAuthors()) {
                author = authorReactiveRepository.findByName(response.getName()).block();
                if (author == null) {
                    author = new Author();
                    author.setName(response.getName());
                    author.setCreated(new Date());
                    author.setUpdated(new Date());
                    author.setStatus(Status.ACTIVE);

                    authors.add(author);
                    authorReactiveRepository.save(author);
                    LogDto logDto1 = LogDto.builder()
                            .name(author.getName())
                            .currentDate(author.getCreated())
                            .service("google")
                            .method("create")
                            .entityType("author")
                            .build();
                    System.out.println(logDto1);

                    restTemplate.postForEntity("http://127.0.0.1:8086/logs/create",logDto1, void.class);
                } else {
                    authors.add(author);
                }
            }
        }
        return Flux.fromIterable(authors);
    }
}
