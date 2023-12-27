package com.amigos.authenautho.demo.repositories;

import com.amigos.authenautho.demo.entities.Publishing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PublishingRepository extends JpaRepository<Publishing, Long> {
    Publishing findBynxb(String nxb_name);
}
