package com.amigos.authenautho.demo.entities;

import com.amigos.authenautho.demo.entities.Book;
import com.amigos.authenautho.demo.entities.Cart;
import com.amigos.authenautho.demo.entities.Order;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ORDER_ITEM")
public class OrderItem {
    @Id
    @GeneratedValue
    @Column(name = "OI_ID")
    private int oiId;

    @ManyToOne
    @JoinColumn(name = "S_ID")
    private Book sach;

    @ManyToOne
    @JoinColumn(name = "DH_ID")
    private Order donHang;

    @ManyToOne
    @JoinColumn(name = "GH_ID")
    private Cart gioHang;

    @Column(name = "OI_SO_LUONG")
    private int oiSoLuong;

    @Column(name = "OI_DON_GIA")
    private float oiDonGia;
}
