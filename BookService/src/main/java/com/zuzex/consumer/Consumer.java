package com.zuzex.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zuzex.mapper.BookMapper;
import com.zuzex.model.dto.AuthorRequest;
import com.zuzex.model.dto.BookRequest;
import com.zuzex.model.dto.BookResponse;
import com.zuzex.model.entity.Author;
import com.zuzex.model.entity.Book;
import com.zuzex.service.BookReactiveService;
import com.zuzex.service.BookService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.UUID.randomUUID;


@Component
public class Consumer {

    private final BookReactiveService bookService;
    private final BookMapper bookMapper;
    private final ObjectMapper objectMapper;
    private final KafkaSender<String,String> kafkaSender;

    @Autowired
    public Consumer(BookReactiveService bookService, BookMapper bookMapper, ObjectMapper objectMapper, KafkaSender<String, String> kafkaSender) {
        this.bookService = bookService;
        this.bookMapper = bookMapper;
        this.objectMapper = objectMapper;
        this.kafkaSender = kafkaSender;
    }

    @KafkaListener(topics = "bookRq", groupId = "google", containerFactory = "kafkaListenerContainerFactory")
    public void consume(ConsumerRecord<String, String> record) throws JsonProcessingException {
        Mono<Book> book;
        Flux<Book> bookList;
        Flux<Author> authorList;
        String result = null;
        Header header = record.headers().lastHeader("u.id");
        switch (record.key()) {
            case "google/user/book/" -> {
                    kafkaSender.send(bookService.getAllBooks().buffer()
                            .map(i -> {
                                try {
                                    String s = objectMapper.writeValueAsString(i.stream().map(bookMapper::bookToResponseBook).collect(Collectors.toList()));
                                    ProducerRecord<String,String> producerRecord = new ProducerRecord<String, String>("bookRs", record.key(),s);
                                    producerRecord.headers().add(header);
                                    return SenderRecord.create(producerRecord,UUID.randomUUID().toString());
                                } catch (JsonProcessingException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }))
                            .doOnError(Throwable::printStackTrace)
                            .subscribe();

            }
//            case "google/user/book/{id}" -> {
//                book = bookService.getBookById(Long.parseLong(record.value()));
//                try {
//                    result = objectMapper.writeValueAsString(bookMapper.bookToResponseBook(book));
//                } catch (JsonProcessingException e) {
//                    e.printStackTrace();
//                }
//            }
//            case "google/user/book/search" -> {
//                BookRequest rq = null;
//                try {
//                    rq = objectMapper.readValue(record.value(), BookRequest.class);
//                    BookResponse rs = bookService.getBookResponse(rq);
//                    book = bookService.createNewBook(rs, new String(record.headers().lastHeader("user.name").value(), StandardCharsets.UTF_8));
//                    result = objectMapper.writeValueAsString(bookMapper.bookToResponseBook(book));
//                } catch (JsonProcessingException e) {
//                    e.printStackTrace();
//                }
//            }
//            case "google/user/book/authorsearch" -> {
//                AuthorRequest authorRequest = null;
//                try {
//                    authorRequest = objectMapper.readValue(record.value(), AuthorRequest.class);
//                    bookList = bookService.createNewBooksByAuthor(bookService.getAllBookResponseByAuthor(authorRequest),
//                            new String(record.headers().lastHeader("user.name").value(), StandardCharsets.UTF_8));
//                    result = objectMapper.writeValueAsString(bookList.stream().map(bookMapper::bookToResponseBook).collect(Collectors.toList()));
//                } catch (JsonProcessingException e) {
//                    e.printStackTrace();
//                }
//
//            }
//            case "google/user/book/read" -> {
//                bookList = bookService.getAllReadBooks();
//                try {
//                    result = objectMapper.writeValueAsString(bookList.stream().map(bookMapper::bookToResponseBook).collect(Collectors.toList()));
//                } catch (JsonProcessingException e) {
//                    e.printStackTrace();
//                }
//            }
//            case "google/user/book/noread" -> {
//                bookList = bookService.getAllNoReadBooks();
//                try {
//                    result = objectMapper.writeValueAsString(bookList.stream().map(bookMapper::bookToResponseBook).collect(Collectors.toList()));
//                } catch (JsonProcessingException e) {
//                    e.printStackTrace();
//                }
//            }
//            case "google/user/book/{id}/isread" -> {
//                try {
//                    result = objectMapper.writeValueAsString(bookService.addToReadBooks(bookService.getBookById(Long.parseLong(record.value()))));
//                } catch (JsonProcessingException e) {
//                    e.printStackTrace();
//                }
//            }
//            case "google/user/book/{id}/isnoread" -> {
//                try {
//                    result = objectMapper.writeValueAsString(bookService.deleteFromReadBooks(bookService.getBookById(Long.parseLong(record.value()))));
//                } catch (JsonProcessingException e) {
//                    e.printStackTrace();
//                }
//            }
//            case "google/user/book/{id}/authors" -> {
//                authorList = bookService.getBookById(Long.parseLong(record.value())).getAuthors();
//                try {
//                    result = objectMapper.writeValueAsString(authorList.stream().map(bookMapper::authorToResponseAuthor).collect(Collectors.toList()));
//                } catch (JsonProcessingException e) {
//                    e.printStackTrace();
//                }
//            }
//            case "google/user/book/{id}/delete" -> {
//                bookService.deleteBookById(Long.parseLong(record.value()));
//                result = "Книга удалена";
//            }
//            case "google/user/book/counts" -> {
//                Map<String, Long> countsMap = bookService.getAllCounts();
//                try {
//                    result = objectMapper.writeValueAsString(countsMap);
//                } catch (JsonProcessingException e) {
//                    e.printStackTrace();
//                }
//            }
        }
    }
}
