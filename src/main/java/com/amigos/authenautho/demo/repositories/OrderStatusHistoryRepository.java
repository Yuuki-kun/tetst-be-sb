package com.amigos.authenautho.demo.repositories;

import com.amigos.authenautho.demo.entities.Order;
import com.amigos.authenautho.demo.entities.OrderStatusHistory;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long> {
    List<OrderStatusHistory> findAllByDonHang(Order order);
    OrderStatusHistory findByDonHang(Order order);
}
