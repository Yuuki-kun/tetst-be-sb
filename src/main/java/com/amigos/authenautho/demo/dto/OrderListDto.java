package com.amigos.authenautho.demo.dto;

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
public class OrderListDto {
    private Long id;
    private List<ShortBookDto> items;
    private String status;
    private Date dateTime;
    private String billingAddress;
    private String paymentMethod;
}
