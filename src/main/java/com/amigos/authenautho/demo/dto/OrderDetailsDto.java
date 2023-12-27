package com.amigos.authenautho.demo.dto;

import com.amigos.authenautho.demo.entities.OrderStatusHistory;
import com.amigos.authenautho.demo.entities.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailsDto {
    private Long id;
    private List<ShortBookDto> items;
    private List<OrderStatusHistory> statusDtos;
    private PaymentDto paymentDto;
    private PaymentAddressDto paymentAddressDto;
    private CustomerDto customerDto;
}

