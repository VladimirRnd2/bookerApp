package com.zuzex.service;

import com.zuzex.model.dto.AuthorRequest;
import com.zuzex.model.dto.BookRequest;
import com.zuzex.model.dto.BookResponse;
import com.zuzex.model.entity.Author;
import com.zuzex.model.entity.Book;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface BookReactiveService {

    Mono<Book> getBookById(Long id);

    Mono<Book> getBookByTitle(String title);

    Flux<Book> getAllBooks();

    Flux<Book> getAllReadBooks();

    Flux<Book> getAllNoReadBooks();

    Mono<Boolean> addToReadBooks(Book book);

    Mono<Boolean> deleteFromReadBooks(Book book);

    Flux<Book> getBooksByDate(String date);

    BookResponse getBookResponse(BookRequest bookRequest);

    Mono<Book> createNewBook(BookResponse bookResponse, String login);

    List<Book> getBooksByAuthorId(Long id);

    Mono<Author> getAuthorByName(String name);

    Mono<Void> deleteBookById(Long id);

    Flux<Author> getAllAuthors();

    Mono<Author> getAuthorById(Long id);

    List<BookResponse> getAllBookResponseByAuthor(AuthorRequest authorRequest);

    Flux<Book> createNewBooksByAuthor(List<BookResponse> bookResponseList,String login);

    Map<String,Long> getAllCounts();
}
