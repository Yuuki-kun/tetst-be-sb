package com.amigos.authenautho.demo.repositories;

import com.amigos.authenautho.demo.entities.Customer;
import com.amigos.authenautho.demo.entities.Order;
import com.amigos.authenautho.demo.entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByKhachHangId(Long khId);
    List<Order> findAllByTrangThai(Status trangThai);
}
