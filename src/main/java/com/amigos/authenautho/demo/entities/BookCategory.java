package com.amigos.authenautho.demo.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="THE_LOAI")
public class BookCategory {
    @Id
    @GeneratedValue
    @Column(name="TL_ID")
    private Long tl_id;
    @Column(name="TL_TEN")
    private String tl;
    @Column(name="TL_IMG")
    private String tl_img;

}

