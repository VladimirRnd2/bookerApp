package com.zuzex.service;

import com.zuzex.model.dto.AuthorRequest;
import com.zuzex.model.dto.BookRequest;
import com.zuzex.model.dto.BookResponse;
import com.zuzex.model.entity.Author;
import com.zuzex.model.entity.Book;

import java.util.List;
import java.util.Map;

public interface BookService {

    Book getBookById(Long id);

    Book getBookByTitle(String title);

    List<Book> getAllBooks();

    List<Book> getAllReadBooks();

    List<Book> getAllNoReadBooks();

    boolean addToReadBooks(Book book);

    boolean deleteFromReadBooks(Book book);

    List<Book> getBooksByDate(String date);

    BookResponse getBookResponse(BookRequest bookRequest);

    Book createNewBook(BookResponse bookResponse, String login);

    List<Book> getBooksByAuthorId(Long id);

    Author getAuthorByName(String name);

    void deleteBookById(Long id);

    List<Author> getAllAuthors();

    Author getAuthorById(Long id);

    List<BookResponse> getAllBookResponseByAuthor(AuthorRequest authorRequest);

    List<Book> createNewBooksByAuthor(List<BookResponse> bookResponseList,String login);

    Map<String, Long> getAllCounts();
}
