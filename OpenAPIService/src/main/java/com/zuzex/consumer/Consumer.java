package com.zuzex.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zuzex.mapper.BookMapper;
import com.zuzex.model.dto.AuthorRequest;
import com.zuzex.model.dto.BookRequest;
import com.zuzex.model.dto.BookResponse;
import com.zuzex.model.entity.Author;
import com.zuzex.model.entity.Book;
import com.zuzex.service.BookService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class Consumer {

    private final BookService bookService;
    private final BookMapper bookMapper;
    private final ObjectMapper objectMapper;

    @Autowired
    public Consumer(BookService bookService, BookMapper bookMapper, ObjectMapper objectMapper) {
        this.bookService = bookService;
        this.bookMapper = bookMapper;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "bookRq", groupId = "open", containerFactory = "kafkaListenerContainerFactory")
    @SendTo()
    @Transactional
    public String consume(ConsumerRecord<String, String> record) {
        String result = null;
        Book book;
        List<Book> bookList;
        List<Author> authorList;
        switch (record.key()) {
            case "open/user/book/" :
                bookList = bookService.getAllBooks();
                try {
                    result = objectMapper.writeValueAsString(bookList.stream().map(bookMapper::bookToResponseBook).collect(Collectors.toList()));
                    break;
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            case "open/user/book/{id}" :
                book = bookService.getBookById(Long.parseLong(record.value()));
                try {
                    result = objectMapper.writeValueAsString(bookMapper.bookToResponseBook(book));
                    break;
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            case "open/user/book/search" :
                BookRequest rq = null;
                try {
                    rq = objectMapper.readValue(record.value(), BookRequest.class);
                    BookResponse rs = bookService.getBookResponse(rq);
                    book = bookService.createNewBook(rs, new String(record.headers().lastHeader("user.name").value(), StandardCharsets.UTF_8));
                    result = objectMapper.writeValueAsString(bookMapper.bookToResponseBook(book));
                    break;
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            case "open/user/book/authorsearch" :
                AuthorRequest authorRequest = null;
                try {
                    authorRequest = objectMapper.readValue(record.value(), AuthorRequest.class);
                    bookList = bookService.createNewBooksByAuthor(bookService.getAllBookResponseByAuthor(authorRequest),
                            new String(record.headers().lastHeader("user.name").value(), StandardCharsets.UTF_8));
                    result = objectMapper.writeValueAsString(bookList.stream().map(bookMapper::bookToResponseBook).collect(Collectors.toList()));
                    break;
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

            case "open/user/book/read" :
                bookList = bookService.getAllReadBooks();
                try {
                    result = objectMapper.writeValueAsString(bookList.stream().map(bookMapper::bookToResponseBook).collect(Collectors.toList()));
                    break;
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            case "open/user/book/noread" :
                bookList = bookService.getAllNoReadBooks();
                try {
                    result = objectMapper.writeValueAsString(bookList.stream().map(bookMapper::bookToResponseBook).collect(Collectors.toList()));
                    break;
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            case "open/user/book/{id}/isread" :
                try {
                    result = objectMapper.writeValueAsString(bookService.addToReadBooks(bookService.getBookById(Long.parseLong(record.value()))));
                    break;
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            case "open/user/book/{id}/isnoread" :
                try {
                    result = objectMapper.writeValueAsString(bookService.deleteFromReadBooks(bookService.getBookById(Long.parseLong(record.value()))));
                    break;
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            case "open/user/book/{id}/authors" :
                authorList = bookService.getBookById(Long.parseLong(record.value())).getAuthors();
                try {
                    result = objectMapper.writeValueAsString(authorList.stream().map(bookMapper::authorToResponseAuthor).collect(Collectors.toList()));
                    break;
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

            case "open/user/book/{id}/delete" :
                bookService.deleteBookById(Long.parseLong(record.value()));
                result = "Книга удалена";
                break;

            case "open/user/book/counts" :
                Map<String, Long> countsMap = bookService.getAllCounts();
                try {
                    result = objectMapper.writeValueAsString(countsMap);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        return result;
    }

}
