package com.amigos.authenautho.demo.repositories;

import com.amigos.authenautho.demo.entities.Book;
import com.amigos.authenautho.demo.entities.DetailsImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetaisImageRepository extends JpaRepository<DetailsImage, Long> {
    List<DetailsImage> findAllByBook(Book book);
}
