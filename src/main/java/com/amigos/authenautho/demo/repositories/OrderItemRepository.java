package com.amigos.authenautho.demo.repositories;

import com.amigos.authenautho.demo.entities.Order;
import com.amigos.authenautho.demo.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    List<OrderItem> findAllByDonHang(Order order);

}
