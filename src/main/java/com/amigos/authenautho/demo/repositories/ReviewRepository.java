package com.amigos.authenautho.demo.repositories;

import com.amigos.authenautho.demo.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
