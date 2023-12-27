package com.amigos.authenautho.demo.repositories;

import com.amigos.authenautho.demo.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepsitory extends JpaRepository<Cart, Long> {
}
