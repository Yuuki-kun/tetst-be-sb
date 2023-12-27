package com.amigos.authenautho.demo.dto;

import com.amigos.authenautho.demo.entities.Customer;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentAddressDto {
    private Date dcghNgayGiao;

    private String dcghDiaChi;

    private String dcghThanhPho;

    private String dcghTinh;

    private String dcghQuocGia;
    private String postalCode;
}
