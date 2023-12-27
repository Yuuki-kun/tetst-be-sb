package com.amigos.authenautho.demo.entities;

import com.amigos.authenautho.demo.entities.Order;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="TRANG_THAI")
public class Status {
    @Id
    @GeneratedValue
    @Column(name = "TT_ID")
    private int ttId;

    @Column(name = "TT_TRANG_THAI")
    private String ttTrangThai;

}
