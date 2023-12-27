package com.amigos.authenautho.demo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SACH")
public class Book {
    @Id
    @GeneratedValue
    @Column(name="S_ID")
    private long id;

    @ManyToOne
    @JoinColumn(name="TL_ID")
    private  BookCategory bookCategory;

    @ManyToOne
    @JoinColumn(name = "NXB_ID")
    private Publishing publishing;

    @OneToMany(mappedBy = "book")
    private List<DetailsImage> images;

    @Column(name = "S_TEN")
    private String ten;
    @Column(name = "S_TEN_TAC_GIA")
    private String ten_tac_gia;
    @Column(name = "S_TRANG_THAI")
    private String trang_thai;
    @Column(name="S_SO_LUONG")
    private int so_luong;
    @Column(name="S_MO_TA")
    private String mo_ta;
    @Column(name="S_DON_GIA")
    private float don_gia;
    @Column(name="S_IMG")
    private String img;
    @Column(name = "S_NGAY_XUAT_BAN")
    private LocalDate ngay_xb;
    @Column(name="S_RATING")
    private float rating;
    @Column(name="S_DA_BAN")
    private int soluong_daban;

    @OneToMany(mappedBy = "book")
    private List<Review> reviews;
}
