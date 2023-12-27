package com.amigos.authenautho.demo.services;

import com.amigos.authenautho.demo.dto.*;
import com.amigos.authenautho.demo.entities.*;
import com.amigos.authenautho.demo.entities.user.User;
import com.amigos.authenautho.demo.entities.user.UserRepository;
import com.amigos.authenautho.demo.repositories.*;
import com.stripe.exception.StripeException;
import com.stripe.model.File;
import com.stripe.model.FileLink;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.param.FileCreateParams;
import com.stripe.param.FileLinkCreateParams;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.ProductCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CheckoutService {
    private final CustomerRepository customerRepository;
    private final BookRepository bookRepository;
    private final CartRepsitory cartRepsitory;
    private final OrderRepository orderRepository;
    private final PaymentAddressRepository paymentAddressRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final StatusRepository statusRepository;

    private final PublishingRepository publishingRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final ProductStripeRepository productStripeRepository;
    private final DetaisImageRepository detaisImageRepository;
    public void saveDH(String customerEmail, float totalAmount, List<ShortBookDto> prodPurchaseList, PaymentAddressDto paymentAddressDto, PaymentDto paymentDto, String csid, String piid) throws Exception {

        Optional<User> user = userRepository.findByEmail(customerEmail);

        if(user.isPresent()){
            Optional<Customer> customer = customerRepository.findById((long) user.get().getKhId());
            if(customer.isPresent()){
                PaymentAddress paymentAddress = PaymentAddress.builder().khachHang(customer.get())
                        .dcghNgayGiao(paymentAddressDto.getDcghNgayGiao())
                        .dcghQuocGia(paymentAddressDto.getDcghQuocGia())
                        .dcghThanhPho(paymentAddressDto.getDcghThanhPho())
                        .dcghDiaChi(paymentAddressDto.getDcghDiaChi())
                        .dcghTinh(paymentAddressDto.getDcghTinh())
                        .dcghPostalCode(paymentAddressDto.getPostalCode())
                        .build();

                paymentAddressRepository.save(paymentAddress);


                Payment payment = Payment.builder()
                        .pAmount(paymentDto.getTotalAmount())
                        .pMethod(paymentDto.getPaymentMethod())
                        .pDate(paymentDto.getPaymentDate())
                        .build();
                paymentRepository.save(payment);

                Order order = Order.builder().csId(csid).piId(piid).diaChiGiaoHang(paymentAddress).khachHang(customer.get()).payment(payment).dhNgayDatHang(new Date()).dhGiaTri(totalAmount).trangThai(Status.builder().ttId(1).build()).build();
                orderRepository.save(order);

                Optional<Cart> cart = cartRepsitory.findById(customer.get().getGioHang().getId());

                List<OrderItem> orderItemList = new ArrayList<>();
                if(cart.isPresent())
                {
                    prodPurchaseList.forEach(
                            item ->{
                                Optional<Book> book = bookRepository.findByTen(item.getName());
                                if (book.isPresent())
                                    orderItemList.add(OrderItem.builder()
                                            .oiDonGia(item.getPrice())
                                            .oiSoLuong((int) item.getQty())
                                            .donHang(order)
                                            .gioHang(cart.get())
                                            .sach(book.get()).build());
                            }
                    );
                }else {
                    System.out.println("cart not found");
                }

                orderItemRepository.saveAll(orderItemList);
                Status status = statusRepository.findByTtTrangThai("Chờ xác nhận").orElseThrow(()->new Exception(""));
                    OrderStatusHistory orderStatusHistory = OrderStatusHistory.builder().statusChangeDate(new Date()).donHang(order).trangThai(status).build();
                    orderStatusHistoryRepository.save(orderStatusHistory);

            }else {
                System.out.println("Customer not found");
            }
        }else {
            System.out.println("User account not found");
        }


    }

    public void addProductTest(java.io.File imgFile, AddBookDto addBook) throws Exception {



        //UPLOAD IMAGE TO STRIPE, COMMENT TO TEST MODE
        FileCreateParams fileCreateParams = FileCreateParams.builder()
                .setFile(imgFile)
                .setPurpose(FileCreateParams.Purpose.DISPUTE_EVIDENCE)
                .build();
        File file = File.create(fileCreateParams);
        System.out.println("FILE="+file.getId());
        String url =file.getUrl();
        System.out.println("url = "+url);
        FileLinkCreateParams params =
                FileLinkCreateParams.builder()
                        .setFile(file.getId())
                        .build();

        FileLink fileLink = FileLink.create(params);
        System.out.println("link="+fileLink.getUrl());


        ProductCreateParams productCreateParams = ProductCreateParams.builder()
                .setName(addBook.getTen())
                .setDescription(addBook.getMo_ta())
                .setActive(true)
                .addImage(fileLink.getUrl())
                .build();

        Product product = Product.create(productCreateParams);

        String prodId = product.getId();
        System.out.println("new product'id = "+prodId);

        PriceCreateParams priceCreateParams = PriceCreateParams.builder()
                .setCurrency("vnd")
                .setProduct(prodId)
                .setUnitAmount((long) addBook.getDon_gia())
                .build();
        Price price = Price.create(priceCreateParams);
        String priceId = price.getId();
        System.out.println("Giá One-Time đã tạo có ID: " + priceId);


        System.out.println("BAT DAU THEM SACH");

        //COMMENT FOR TEST MODE
//        writebook(addBook, fileLink.getUrl(), priceId, prodId);
//        writebook(addBook, null, priceId, prodId);

    }

    public void addProduct(List<java.io.File> imgFiles, AddBookDto addBook) throws Exception {


        List<FileLink> fileLinks = new ArrayList<>();
        //UPLOAD IMAGE TO STRIPE, COMMENT TO TEST MODE
        for(int i=0; i<imgFiles.size(); i++){
            FileCreateParams fileCreateParams = FileCreateParams.builder()
                    .setFile(imgFiles.get(i))
                    .setPurpose(FileCreateParams.Purpose.DISPUTE_EVIDENCE)
                    .build();
            File file = File.create(fileCreateParams);
            System.out.println("FILE="+file.getId());
            String url =file.getUrl();
            System.out.println("url = "+url);
            FileLinkCreateParams params =
                    FileLinkCreateParams.builder()
                            .setFile(file.getId())
                            .build();

            FileLink fileLink = FileLink.create(params);
            fileLinks.add(fileLink);
            System.out.println("link="+fileLink.getUrl());
        }


        ProductCreateParams productCreateParams = ProductCreateParams.builder()
                .setName(addBook.getTen())
                .setDescription(addBook.getMo_ta())
                .setActive(true)
                .addImage(fileLinks.get(0).getUrl())
                .build();

        Product product = Product.create(productCreateParams);

        String prodId = product.getId();
        System.out.println("new product'id = "+prodId);

        PriceCreateParams priceCreateParams = PriceCreateParams.builder()
                .setCurrency("vnd")
                .setProduct(prodId)
                .setUnitAmount((long) addBook.getDon_gia())
                .build();
        Price price = Price.create(priceCreateParams);
        String priceId = price.getId();
        System.out.println("Giá One-Time đã tạo có ID: " + priceId);


        System.out.println("BAT DAU THEM SACH");

        //COMMENT FOR TEST MODE
        writebook(addBook, fileLinks, priceId, prodId);
//        writebook(addBook, null, priceId, prodId);

    }


    public void writebook(AddBookDto addBook, List<FileLink> fileLinks, String priceId, String prodId){
        Publishing publishing = publishingRepository.findBynxb(addBook.getNxbname());
        BookCategory bookCategory = bookCategoryRepository.findByTl(addBook.getCategoryName());

        LocalDate date = LocalDate.now();

        Book book = Book.builder()
                .don_gia(addBook.getDon_gia()/1000)
                .img(fileLinks.get(0).getUrl())
                .mo_ta(addBook.getMo_ta())
                .ngay_xb(date)
                .publishing(publishing)
                .rating(addBook.getRating())
                .so_luong(addBook.getSo_luong())
                .soluong_daban(addBook.getSoluong_daban())
                .ten(addBook.getTen())
                .ten_tac_gia(addBook.getTen_tac_gia())
                .bookCategory(bookCategory)
                .trang_thai(addBook.getTrang_thai())
                .images(null)
                .reviews(null)
                .build();
        System.out.println(addBook);
        bookRepository.save(book);
        int bid = (int) book.getId();
        System.out.println(bid);
        ProductStripe productStripe = ProductStripe.builder()
                .currency("vnd")
                .priceId(priceId)
                .prodId(prodId)
                .product_name(addBook.getTen())
                .id(bid)
                .build();

        List<DetailsImage> detailsImages = new ArrayList<>();
        if(fileLinks.size()>1){
            for(int i=1; i< fileLinks.size(); i++){
                detailsImages.add(DetailsImage.builder().img(fileLinks.get(i).getUrl()).book(book).build());
            }
        }
        if(!detailsImages.isEmpty()){
            detaisImageRepository.saveAll(detailsImages);
        }
        productStripeRepository.save(productStripe);
    }
}
