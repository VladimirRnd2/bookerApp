package com.zuzex.repository.reactive;

import com.zuzex.model.entity.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Repository
public interface UserReactiveRepository extends ReactiveCrudRepository<User,Long> {

    Mono<User> findByLogin(String login);
}
