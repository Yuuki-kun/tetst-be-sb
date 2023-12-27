package com.amigos.authenautho.demo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue
    private Long r_id;

    private float rating;

    private String reviewText;

    private Date reviewDate;

    @ManyToOne
    @JoinColumn(name = "S_ID")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "KH_ID")
    private Customer customer;

    @OneToMany(mappedBy = "review")
    private List<Comment> comments;
}
