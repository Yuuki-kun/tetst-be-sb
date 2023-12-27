package com.amigos.authenautho.demo.repositories;

import com.amigos.authenautho.demo.entities.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookCategoryRepository extends JpaRepository <BookCategory, Long> {
    BookCategory findByTl(String tl_ten);
}
