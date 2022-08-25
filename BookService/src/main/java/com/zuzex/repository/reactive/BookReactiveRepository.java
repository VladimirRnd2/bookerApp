package com.zuzex.repository.reactive;

import com.zuzex.model.entity.Book;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookReactiveRepository extends ReactiveCrudRepository<Book, Long> {

    Mono<Book> findByTitle(String title);

    Flux<Book> findByIsReadEquals(boolean isRead);

    Flux<Book> findByDate(String date);
}
