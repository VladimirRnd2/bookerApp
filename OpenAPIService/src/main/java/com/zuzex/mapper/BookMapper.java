package com.zuzex.mapper;

import com.zuzex.model.dto.ResponseAuthor;
import com.zuzex.model.dto.ResponseBook;
import com.zuzex.model.dto.ResponseUser;
import com.zuzex.model.entity.Author;
import com.zuzex.model.entity.Book;
import com.zuzex.model.entity.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BookMapper {

    public ResponseBook bookToResponseBook(Book book) {
        ResponseBook responseBook = new ResponseBook();
        responseBook.setId(book.getId());
        responseBook.setTitle(book.getTitle());
        responseBook.setIsRead(book.getIsRead());
        responseBook.setDate(book.getDate());
        List<Author> authors = book.getAuthors();
        List<ResponseAuthor> responseAuthors = new ArrayList<>();
        for (Author author : authors) {
            responseAuthors.add(authorToResponseAuthor(author));
        }
        responseBook.setAuthors(responseAuthors);
        List<User> users = book.getUsers();
        List<ResponseUser> responseUsers = new ArrayList<>();
        for(User user : users) {
            responseUsers.add(userToResponseUser(user));
        }
        responseBook.setUsers(responseUsers);
        return responseBook;
    }

    public ResponseAuthor authorToResponseAuthor(Author author) {
        ResponseAuthor responseAuthor = new ResponseAuthor();
        responseAuthor.setId(author.getId());
        responseAuthor.setName(author.getName());
        return responseAuthor;
    }

    public ResponseUser userToResponseUser(User user) {
        ResponseUser responseUser = new ResponseUser();
        responseUser.setId(user.getId());
        responseUser.setLogin(user.getLogin());
        return responseUser;
    }
}

