package com.amigos.authenautho.demo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "GIO_HANG")
public class Cart {
    @Id
    @GeneratedValue
    @Column(name = "GH_ID")
    private long id;

}
