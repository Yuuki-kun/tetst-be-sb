package com.amigos.authenautho.demo.mappers;

import com.amigos.authenautho.demo.dto.BookDto;
import com.amigos.authenautho.demo.entities.Book;

import java.util.ArrayList;
import java.util.List;

public class DtoService {

    public static BookDto BookToBookDto(Book book){
        List<String> details_img = new ArrayList<>();
        book.getImages().forEach(img->{
            details_img.add(img.getImg());
        });

        return BookDto.builder()
                .id(book.getId())
                .ten(book.getTen())
                .trang_thai(book.getTrang_thai())
                .so_luong(book.getSo_luong())
                .mo_ta(book.getMo_ta())
                .don_gia(book.getDon_gia())
                .img(book.getImg())
                .ngay_xb(book.getNgay_xb())
                .rating(book.getRating())
                .theloai(book.getBookCategory().getTl())
                .nxb(book.getPublishing().getNxb())
                .details_images(details_img)
                .tac_gia(book.getTen_tac_gia())
                .soluong_daban(book.getSoluong_daban())
                .build();
    }
}
