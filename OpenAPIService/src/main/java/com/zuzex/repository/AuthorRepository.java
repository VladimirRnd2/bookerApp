package com.zuzex.repository;

import com.zuzex.model.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author,Long> {

    Author findByName(String name);

}
