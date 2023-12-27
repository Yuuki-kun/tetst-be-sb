package com.amigos.authenautho.demo.dto;

import com.amigos.authenautho.demo.entities.BookCategory;
import com.amigos.authenautho.demo.entities.Publishing;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatePublishDto {
    private List<BookCategory> bookCategories;
    private List<Publishing> publishings;
}
