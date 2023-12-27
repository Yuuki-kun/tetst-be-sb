package com.amigos.authenautho.demo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="KHACH_HANG")
public class Customer {
    @Id
    @GeneratedValue
    @Column(name = "KH_ID")
    private int id;

    @Column(name = "KH_HOTEN")
    private String fullName;

    @Column(name = "KH_SDT",  unique = true)
    private String phoneNumber;

    @Column(name = "KH_DIACHI")
    private String address;

    @OneToOne
    @JoinColumn(name = "GH_ID")
    private Cart gioHang;

    @OneToMany(mappedBy = "customer")
    private List<Review> reviews;

    @OneToMany(mappedBy = "customer")
    private List<Comment> comments;
}
