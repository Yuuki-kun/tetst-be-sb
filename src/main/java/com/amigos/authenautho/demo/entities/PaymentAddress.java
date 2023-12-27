package com.amigos.authenautho.demo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="DIA_CHI_GIAO_HANG")
public class PaymentAddress {
    @Id
    @GeneratedValue
    @Column(name = "DCGH_ID")
    private int dcghId;

    @ManyToOne
    @JoinColumn(name = "KH_ID")
    private Customer khachHang;

    @Column(name="DCGH_NGAY_GIAO")
    private Date dcghNgayGiao;

    @Column(name = "DCGH_DIA_CHI")
    private String dcghDiaChi;

    @Column(name = "DCGH_THANH_PHO")
    private String dcghThanhPho;

    @Column(name = "DCGH_QUOC_GIA")
    private String dcghQuocGia;

    @Column(name = "DCGH_TINH")
    private String dcghTinh;

    @Column(name = "DCGH_POSTAL_CODE")
    private String dcghPostalCode;
}
