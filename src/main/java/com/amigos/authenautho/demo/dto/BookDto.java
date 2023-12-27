package com.amigos.authenautho.demo.dto;

import com.amigos.authenautho.demo.entities.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookDto {

    private long id;
    private String ten;
    private String trang_thai;
    private int so_luong;
    private String mo_ta;
    private float don_gia;
    private String img;
    private LocalDate ngay_xb;
    private float rating;
    private String theloai;
    private String nxb;
    private int soluong_daban;
    private List<String> details_images;
    private String tac_gia;
}
