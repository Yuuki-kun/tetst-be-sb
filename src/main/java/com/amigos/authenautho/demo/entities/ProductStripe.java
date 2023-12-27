package com.amigos.authenautho.demo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "SANPHAM_STRIPE")
public class ProductStripe {
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "price_id")
    private String priceId;
    @Column(name = "prod_id")

    private String prodId;
    @Column(name = "currency")

    private String currency;
    @Column(name = "product_name")

    private String product_name;
}
