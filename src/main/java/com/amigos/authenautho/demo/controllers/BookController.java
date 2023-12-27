package com.amigos.authenautho.demo.controllers;

import com.amigos.authenautho.demo.entities.Book;
import com.amigos.authenautho.demo.entities.Publishing;
import com.amigos.authenautho.demo.entities.Review;
import com.amigos.authenautho.demo.mappers.DtoService;
import com.amigos.authenautho.demo.repositories.BookRepository;
import com.amigos.authenautho.demo.repositories.PublishingRepository;
import com.amigos.authenautho.demo.services.BookServices;
import com.amigos.authenautho.demo.dto.BookDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
public class BookController {

    private final BookServices bookServices;
    private final BookRepository bookRepository;
    private final PublishingRepository publishingRepository;

    @GetMapping
    public ResponseEntity<List<BookDto>> getAllBooks(){
        return ResponseEntity.ok(bookServices.getAllBooks());
    }


    @GetMapping("/product-details")
    public ResponseEntity<BookDto> getBookDetails(@RequestParam("id") Long bookId) throws Exception {
        return ResponseEntity.ok(bookServices.getBook(bookId));
    }

    @GetMapping("/search-book")
    public ResponseEntity<List<BookDto>> getApproximateBooks(@RequestParam String searchTerm){

        Publishing publishing = publishingRepository.findBynxb(searchTerm);

        List<BookDto> approximateBooks = new ArrayList<>();
        List<Book> books = new ArrayList<>();
        if(publishing!=null){
            books = bookRepository.findAllByPublishing(publishing);
            List<BookDto> bookDtos = books
                    .stream()
                    .map(DtoService::BookToBookDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(bookDtos);
        }else{
            books = bookRepository.findAll();

        }

        List<BookDto> bookDtos = books
                .stream()
                .map(DtoService::BookToBookDto)
                .collect(Collectors.toList());
        bookDtos.forEach(bookDto -> {
            boolean match = bookServices.isApproximate(0,searchTerm, bookDto.getTen());
            if(match){
                approximateBooks.add(bookDto);
            }

        });

        return ResponseEntity.ok(approximateBooks);
    }

    @GetMapping("/get-nxbs")
    public ResponseEntity<List<Publishing>> getAllPublishing(){
        return ResponseEntity.ok(publishingRepository.findAll());
    }


//    @GetMapping("/product-review")
//    public ResponseEntity<Review> getReview(@RequestParam("bid") Long bid){
//        return
//    }



}
