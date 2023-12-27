package com.amigos.authenautho.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddBookDto {
    private float don_gia;
    private String mo_ta;
    private String nxbname;
    private String categoryName;
    private float rating;
    private int so_luong;
    private int soluong_daban;
    private String ten;
    private String ten_tac_gia;
    private String trang_thai;
}
