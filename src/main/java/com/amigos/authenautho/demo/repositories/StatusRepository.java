package com.amigos.authenautho.demo.repositories;

import com.amigos.authenautho.demo.entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatusRepository extends JpaRepository<Status, Integer> {
    Optional<Status> findByTtTrangThai(String tt);
}
