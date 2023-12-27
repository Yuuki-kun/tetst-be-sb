package com.amigos.authenautho.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortBookDto {
    private String name;
    private long qty;
    private float price;
    private String img;
}

