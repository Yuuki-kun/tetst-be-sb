package com.amigos.authenautho.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseByCashInfo {
    private List<ProductPurchase> productPurchases;
    private String customerEmail;
    private String address;
    private String province;
    private String city;

    private String postalCode;
}
