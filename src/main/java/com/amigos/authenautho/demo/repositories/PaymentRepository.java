package com.amigos.authenautho.demo.repositories;

import com.amigos.authenautho.demo.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
}
