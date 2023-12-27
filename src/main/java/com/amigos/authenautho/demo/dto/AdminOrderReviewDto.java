package com.amigos.authenautho.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderReviewDto {
    private Long id;
    private String cus;
    private Date orderDate;
    private float totalAmount;
    private String currentStatus;
}
