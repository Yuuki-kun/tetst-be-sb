package com.amigos.authenautho.demo.entities;

import com.amigos.authenautho.demo.entities.Order;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "PAYMENT")
public class Payment {
    @Id
    @GeneratedValue
    @Column(name = "P_ID")
    private int pId;


    @Column(name = "P_METHOD")
    private String pMethod;

    @Column(name = "P_DATE")
    private Date pDate;

    @Column(name = "P_AMOUNT")
    private float pAmount;
}
