package com.amigos.authenautho.demo.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Table(name="order_status_history")
public class OrderStatusHistory {
    @Id
    @GeneratedValue
    @Column(name = "history_id")
    private Long id;

    //không chuyển đổi thành json để tránh vòng lặp
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "DH_ID")
    private Order donHang;

    @ManyToOne
    @JoinColumn(name="TT_ID")
    private Status trangThai;

    @Column(name = "status_change_date")
    private Date statusChangeDate;
}
