package com.amigos.authenautho.demo.repositories;

import com.amigos.authenautho.demo.entities.Book;
import com.amigos.authenautho.demo.entities.Publishing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByTen(String name);
    List<Book> findAllByPublishing(Publishing publishing);

}
