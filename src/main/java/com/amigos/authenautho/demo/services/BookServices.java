package com.amigos.authenautho.demo.services;

import com.amigos.authenautho.demo.dto.BookDto;
import com.amigos.authenautho.demo.dto.ProductPurchase;
import com.amigos.authenautho.demo.dto.StripeProductPrice;
import com.amigos.authenautho.demo.entities.Book;
import com.amigos.authenautho.demo.entities.BookCategory;
import com.amigos.authenautho.demo.entities.ProductStripe;
import com.amigos.authenautho.demo.entities.Publishing;
import com.amigos.authenautho.demo.mappers.DtoService;
import com.amigos.authenautho.demo.repositories.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.similarity.FuzzyScore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServices {
    private final BookRepository bookRepository;
    private final PublishingRepository publishingRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final ProductStripeRepository productStripeRepository;
    private final ReviewRepository reviewRepository;



    public BookDto getBook(Long id) throws Exception {
        Book book = bookRepository.findById(id).orElseThrow(()-> new Exception(("")));
        return DtoService.BookToBookDto(book);
    }

    public List<BookDto> getAllBooks(){
        List<Book> bookList = bookRepository.findAll();

        List<BookDto> bookDtos = bookList
                                .stream()
                                .map(DtoService::BookToBookDto)
                                .collect(Collectors.toList());
        return bookDtos;
    }
    public List<StripeProductPrice> productStripePriceId(List<ProductPurchase> products){
        //return list of (price_id, qty)
        List<StripeProductPrice> listOfProductStripePrice = new ArrayList<>();
        products.forEach(prod ->{
            Optional<ProductStripe> productStripe = productStripeRepository.findById(prod.getItemId());
            if(productStripe.isPresent()){
                listOfProductStripePrice.
                                add(StripeProductPrice.builder()
                                .price(productStripe.get().getPriceId())
                                .quantity(prod.getSoluong())
                                .build());
            }
        });
        System.out.println(listOfProductStripePrice);
        return listOfProductStripePrice;
    }
    public List<StripeProductPrice> getPriceIds(List<ProductPurchase> productPurchases){
        List<StripeProductPrice> stripeProductPrices = new ArrayList<>();
        productPurchases.forEach(prodp->{
            Optional<ProductStripe> productStripe = productStripeRepository.findById(prodp.getItemId());
            if(productStripe.isPresent()){
                stripeProductPrices.add(StripeProductPrice.builder().price(productStripe.get().getPriceId()).quantity(prodp.getSoluong()).build());
            }
        });
        return stripeProductPrices;
    }

    public boolean isApproximate(int threshold, String searchTerm, String word) {

        Locale vietnameseLocale =  new Locale("vi"
               , "VN");
        FuzzyScore fuzzyScore = new FuzzyScore(vietnameseLocale);

        int score = fuzzyScore.fuzzyScore(searchTerm, word);
        System.out.println("Fuzzy score between '" + searchTerm + "' and '" + word + "': " + score);

        return score>threshold;
    }
}
