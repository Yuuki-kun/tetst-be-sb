package com.amigos.authenautho.demo.repositories;

import com.amigos.authenautho.demo.entities.ProductStripe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductStripeRepository extends JpaRepository<ProductStripe, Integer> {

}
