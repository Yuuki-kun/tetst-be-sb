package com.amigos.authenautho.demo.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "DON_HANG")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="DH_ID")
    private Long dh_id;

    @ManyToOne
    @JoinColumn(name = "DCGH_ID")
    private PaymentAddress diaChiGiaoHang;

    @ManyToOne
    @JoinColumn(name = "KH_ID")
    private Customer khachHang;

    @Column(name = "DH_NGAY_DAT_HANG")
    private Date dhNgayDatHang;

    @Column(name = "DH_GIA_TRI")
    private float dhGiaTri;

    @ManyToOne
    @JoinColumn(name = "TT_ID")
    private Status trangThai;
//
    @OneToOne
    @JoinColumn(name = "P_ID")
    private Payment payment; // Thêm mối quan hệ với Payment

    @Column(name="cs_id")
    private String csId;

    @Column(name = "pi_id")
    private String piId;

//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,mappedBy = "donHang")
//    private List<OrderItem> orderItemList;
//@JsonManagedReference
//@OneToMany(mappedBy = "donHang", cascade = CascadeType.ALL)
//private List<OrderStatusHistory> orderStatusHistories;
}
