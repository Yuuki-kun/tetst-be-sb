package com.amigos.authenautho.demo.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="NHA_XUAT_BAN")
public class Publishing {
    @Id
    @GeneratedValue
    @Column(name="NXB_ID")
    private Long nbx_id;
    @Column(name="NXB_TEN")
    private String nxb;
    @Column(name="NXB_DIA_CHI")
    private String nxb_dia_chi;
    @Column(name="NXB_EMAIL")
    private String nbx_email;
    @Column(name = "NXB_IMG")
    private String nxb_img;
}
