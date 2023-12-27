package com.amigos.authenautho.demo.repositories;

import com.amigos.authenautho.demo.entities.PaymentAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentAddressRepository extends JpaRepository<PaymentAddress, Integer > {
}
