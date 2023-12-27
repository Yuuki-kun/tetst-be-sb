package com.amigos.authenautho.demo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "details_img")
public class DetailsImage {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "img_url")
    private String img;

    @ManyToOne
    @JoinColumn(name = "SACH_ID")
    private Book book;
}
