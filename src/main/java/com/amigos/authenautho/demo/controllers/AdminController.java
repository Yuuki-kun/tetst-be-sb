package com.amigos.authenautho.demo.controllers;

import com.amigos.authenautho.demo.dto.*;
import com.amigos.authenautho.demo.entities.*;
import com.amigos.authenautho.demo.entities.Order;
import com.amigos.authenautho.demo.repositories.*;
import com.amigos.authenautho.demo.services.CheckoutService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.*;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.io.File;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin-service")
@RequiredArgsConstructor
public class AdminController {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final StatusRepository statusRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final CheckoutService checkoutService;
    private final BookRepository bookRepository;
    private final DetaisImageRepository detaisImageRepository;
    private final ProductStripeRepository productStripeRepository;
    private final PublishingRepository publishingRepository;
    private final BookCategoryRepository bookCategoryRepository;
    @GetMapping()
    public ResponseEntity<List<AdminOrderReviewDto>> getAllOrders(){
        System.out.println("get all order");
        List<Order> orders = orderRepository.findAll();
        List<AdminOrderReviewDto> adminOrderReviewDtos = new ArrayList<>();
        if(!(orders.isEmpty() || orders==null)){
             adminOrderReviewDtos =
                    orders.stream().map(order -> AdminOrderReviewDto
                            .builder()
                            .id(order.getDh_id())
                            .orderDate(order.getDhNgayDatHang())
                            .totalAmount(order.getDhGiaTri())
                            .currentStatus(order.getTrangThai().getTtTrangThai())
                            .cus(order.getKhachHang().getFullName())
                            .build()).collect(Collectors.toList());
        }

        System.out.println("All OK");
        return ResponseEntity.ok(adminOrderReviewDtos);

    }

//    @PostMapping("/invoice")
//    public String invoice(){
//        try{
//            PaymentIntent paymentIntent = PaymentIntent.retrieve("pi_3OCvzUCB4m1aFTFZ0p1fSIri");
//            InvoiceCreateParams.Builder invoiceParamsBuilder =  InvoiceCreateParams.builder()
//                    .setCustomer(paymentIntent.getCustomer())
//                    .setAutoAdvance(true);
//            InvoiceItemCreateParams invoiceItemParams = InvoiceItemCreateParams.builder()
//                    .setAmount((long) paymentIntent.getAmount().intValue())
//                    .setCurrency(paymentIntent.getCurrency())
//                    .setDescription("Your description here")
//                    .setQuantity(1L)
//                    .build();
//            Invoice invoice = Invoice.create(invoiceParamsBuilder.build());
//
//        } catch (StripeException e) {
//            throw new RuntimeException(e);
//        }
//        return "OK";
//    }

    /*
    * id = order's id
    * AdminOrderReviewDto = Order Data Transfer Object
    * */

    // /admin-service?id=${id}
    @PutMapping
    public ResponseEntity<Boolean> updateOrderStatus(@RequestParam("id") Long id, @RequestBody AdminOrderReviewDto order) throws Exception {

        Order update = orderRepository.findById(id).orElseThrow(()-> new Exception(""));
        Status status = statusRepository.findByTtTrangThai(order.getCurrentStatus()).orElseThrow(()->new Exception(""));
        System.out.println("TT="+status.getTtId());
        update.setTrangThai(status);
        orderRepository.save(update);

        OrderStatusHistory orderStatusHistory = OrderStatusHistory.builder().statusChangeDate(new Date()).donHang(update).trangThai(status).build();
        orderStatusHistoryRepository.save(orderStatusHistory);
        return ResponseEntity.ok(true);
    }

    @PostMapping("/upload")
    public ResponseEntity<Boolean> uploadImageTest(@RequestPart("img")List<MultipartFile> images,
                                               @RequestParam("formData") String data) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        AddBookDto addBookDto = objectMapper.readValue(data, AddBookDto.class);
        System.out.println(addBookDto);
        List<File> files = new ArrayList<>();
          for(int i=0; i<images.size(); i++){

              byte[] img_data;
              try {
                  img_data = images.get(i).getBytes();
                  String currentWorkingDirectory = System.getProperty("user.dir");
                  System.out.println("name="+images.get(i).getOriginalFilename());
                  String filePath = currentWorkingDirectory + "/"+images.get(i).getOriginalFilename();
                  System.out.println("FP="+filePath);

                  files.add(new File(filePath));

                  FileOutputStream fileOutputStream;
                  fileOutputStream = new FileOutputStream(filePath);
                  fileOutputStream.write(img_data);
                  fileOutputStream.close();
              } catch (IOException e) {
                  throw new RuntimeException(e);
              }
          }

          if(!files.isEmpty()){
              checkoutService.addProduct(files, addBookDto);
              files.forEach(f -> {
                  f.delete();
              });
          }
        return ResponseEntity.ok(true);
    }

    @DeleteMapping()
    public ResponseEntity<String> deleteBook(@RequestParam("id") Long id) throws Exception {
        Book book = bookRepository.findById(id).orElseThrow((()->new Exception("")));
        List<DetailsImage> detailsImages = detaisImageRepository.findAllByBook(book);
        ProductStripe productStripe = productStripeRepository.findById((int)(book.getId())).orElseThrow(()->new Exception(""));
        try{
            detaisImageRepository.deleteAll(detailsImages);
            bookRepository.delete(book);
            productStripeRepository.delete(productStripe);
        }catch (Exception ex){
            ex.printStackTrace();
            return ResponseEntity.ok("failed");
        }

        return ResponseEntity.ok("delete book #"+id+" successfully!");
    }

    @PutMapping("/edit-book")
    public ResponseEntity<Boolean> updateBook(@RequestParam("id") Long id, @RequestBody AddBookDto addBookDto) throws Exception {
        Book b = bookRepository.findById(id).orElseThrow(()->new Exception(""));
        System.out.println(addBookDto);
        b.setDon_gia(addBookDto.getDon_gia()/1000);
        b.setMo_ta(addBookDto.getMo_ta());

        Publishing p = publishingRepository.findBynxb(addBookDto.getNxbname());
        BookCategory bc = bookCategoryRepository.findByTl(addBookDto.getCategoryName());

        b.setPublishing(p);
        b.setBookCategory(bc);
        b.setRating(addBookDto.getRating());
        b.setSo_luong(addBookDto.getSo_luong());
        b.setSoluong_daban(addBookDto.getSoluong_daban());
        b.setTen(addBookDto.getTen());
        b.setTen_tac_gia(addBookDto.getTen_tac_gia());
        b.setTrang_thai(addBookDto.getTrang_thai());
        try{
            bookRepository.save(b);
        }catch (Exception e){
            e.printStackTrace();
            ResponseEntity.ok(false);
        }

        //Cần thay đổi thêm sản phẩm stripe tương ứng

        return ResponseEntity.ok(true);
    }

    @PutMapping("/edit-status")
    public ResponseEntity<String> updateListOrderStatuses(@RequestParam("id") Long id, @RequestBody List<String> statusList) throws Exception {


        return ResponseEntity.ok("id = "+id + "stt="+statusList);
    }
    private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(multipartFile.getOriginalFilename());
        multipartFile.transferTo(file);
        return file;
    }

    @PutMapping("/confirm-order")
    public ResponseEntity<String> confirmOrder(@RequestParam("id") Long id) throws Exception {
        System.out.println("Confirm id="+id);
        Order order = orderRepository.findById(id).orElseThrow(()-> new Exception(""));

        if(order.getPayment().getPMethod().equals("card")){
            PaymentIntent paymentIntent = PaymentIntent.retrieve(order.getPiId());
            PaymentIntent update = paymentIntent.capture();
        }

        Status status = statusRepository.findByTtTrangThai("Đã xác nhận").orElseThrow(()-> new Exception(""));

        order.setTrangThai(status);
        OrderStatusHistory orderStatusHistory = OrderStatusHistory.builder().donHang(order).trangThai(status).statusChangeDate(new Date()).build();
        orderRepository.save(order);
        orderStatusHistoryRepository.save(orderStatusHistory);
        return ResponseEntity.ok("OK");
    }

    @PutMapping("/cancel-order")
    public ResponseEntity<String> cancelOrder(@RequestParam("id") Long id) throws Exception {
        System.out.println("Cancel id="+id);
        Order order = orderRepository.findById(id).orElseThrow(()-> new Exception(""));
        Status status = statusRepository.findByTtTrangThai("Đã hủy").orElseThrow(()-> new Exception(""));

        PaymentIntent paymentIntent = PaymentIntent.retrieve(order.getPiId());
        PaymentIntent update = paymentIntent.cancel();

        order.setTrangThai(status);
        OrderStatusHistory orderStatusHistory = OrderStatusHistory.builder().donHang(order).trangThai(status).statusChangeDate(new Date()).build();
        orderRepository.save(order);
        orderStatusHistoryRepository.save(orderStatusHistory);
        return ResponseEntity.ok("OK");
    }

    @PutMapping("/refund-payment")
    public ResponseEntity<String > refundPayment(@RequestParam("id") Long id) throws Exception {
        Order order = orderRepository.findById(id).orElseThrow(()->new Exception(""));
        RefundCreateParams refundCreateParams = RefundCreateParams.builder().setPaymentIntent(order.getPiId()).setReason(RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER).build();
        Refund refund = Refund.create(refundCreateParams);
        Status status = statusRepository.findByTtTrangThai("Đã hoàn tiền").orElseThrow(()->new Exception(""));
        order.setTrangThai(status);
        orderRepository.save(order);
        OrderStatusHistory orderStatusHistory = OrderStatusHistory.builder().donHang(order).trangThai(status).statusChangeDate(new Date()).build();
        orderStatusHistoryRepository.save(orderStatusHistory);
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/cate-publi-alls")
    public ResponseEntity<CatePublishDto> getAlls(){
        List<BookCategory> bookCategories = bookCategoryRepository.findAll();
        List<Publishing> publishings = publishingRepository.findAll();
        CatePublishDto catePublishDto = CatePublishDto.builder().bookCategories(bookCategories).publishings(publishings).build();
        return ResponseEntity.ok(catePublishDto);
    }

    @PostMapping("/add-publisher")
    public ResponseEntity<Boolean> addPublisher(@RequestPart("img")MultipartFile image,
                                                   @RequestParam("pubFormData") String data) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        AddPublisherDto addPublisherDto = objectMapper.readValue(data, AddPublisherDto.class);
        System.out.println("pubs="+addPublisherDto);
        byte[] img_data;
        File file;
        try {
            img_data = image.getBytes();
            String currentWorkingDirectory = System.getProperty("user.dir");
            System.out.println("name="+image.getOriginalFilename());
            String filePath = currentWorkingDirectory + "/"+image.getOriginalFilename();
            System.out.println("FP="+filePath);

            file = new File(filePath);

            FileOutputStream fileOutputStream;
            fileOutputStream = new FileOutputStream(filePath);
            fileOutputStream.write(img_data);

            fileOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try{
            if(file!=null){
                FileCreateParams fileCreateParams = FileCreateParams.builder()
                        .setFile(file)
                        .setPurpose(FileCreateParams.Purpose.DISPUTE_EVIDENCE)
                        .build();
                com.stripe.model.File fileModel = com.stripe.model.File.create(fileCreateParams);
                System.out.println("FILE="+fileModel.getId());
                String url =fileModel.getUrl();
                System.out.println("url = "+url);
                FileLinkCreateParams params =
                        FileLinkCreateParams.builder()
                                .setFile(fileModel.getId())
                                .build();

                FileLink fileLink = FileLink.create(params);

                Publishing publishing = Publishing.builder().nbx_email(addPublisherDto.getEmail()).nxb(addPublisherDto.getTen())
                        .nxb_dia_chi(addPublisherDto.getDia_chi())
                        .nxb_img(fileLink.getUrl()).build();
                publishingRepository.save(publishing);

                file.delete();

                return ResponseEntity.ok(true);
            }
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.ok(false);
        }


        return ResponseEntity.ok(false);

    }

    @PostMapping("/add-cate")
    public ResponseEntity<Boolean> addCate(@RequestParam("name") String name){
        System.out.println(name);
        try{
            BookCategory bookCategory = BookCategory.builder().tl(name).build();
            bookCategoryRepository.save(bookCategory);
            return ResponseEntity.ok(true);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.ok(false);
        }

    }
}
