package com.amigos.authenautho.demo.services;

import com.amigos.authenautho.demo.dto.*;
import com.amigos.authenautho.demo.entities.*;
import com.amigos.authenautho.demo.entities.user.User;
import com.amigos.authenautho.demo.entities.user.UserRepository;
import com.amigos.authenautho.demo.repositories.*;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TrackingOrderServices {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final StatusRepository statusRepository;
    private final CustomerRepository customerRepository;
    private final OrderItemRepository orderItemRepository;
    private final BookRepository bookRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    public List<OrderListDto> getAllOrdersByEmail(String email, String orderType) throws Exception {

        List<OrderListDto> orderListDtos = new ArrayList<>();

        User user = userRepository.findByEmail(email).orElseThrow(()->new Exception(""));
        Customer customer = customerRepository.findById(Long.valueOf(user.getKhId())).orElseThrow(()-> new Exception(""));

        List<Order> orders;
        if(orderType.equals("all"))
            orders = orderRepository.findAllByKhachHangId((long) customer.getId());
        else if(orderType.equals("processing")){
            orders = new ArrayList<>();
            List<Order> allOrders = orderRepository.findAllByKhachHangId((long) customer.getId());
            allOrders.forEach(o ->{
                if(o.getTrangThai().getTtTrangThai().equals("Đang xử lý")){
                    orders.add(o);
                }
            });
        }else if(orderType.equals("canceled")){
            orders = new ArrayList<>();
            List<Order> allOrders = orderRepository.findAllByKhachHangId((long) customer.getId());
            allOrders.forEach(o ->{
                if(o.getTrangThai().getTtTrangThai().equals("Đã hủy")){
                    orders.add(o);
                }
            });
        }else if(orderType.equals("delivering")){
            orders = new ArrayList<>();
            List<Order> allOrders = orderRepository.findAllByKhachHangId((long) customer.getId());
            allOrders.forEach(o ->{
                if(o.getTrangThai().getTtTrangThai().equals("Đang vận chuyển")){
                    orders.add(o);
                }
            });
        }else if(orderType.equals("delivered")){
            orders = new ArrayList<>();
            List<Order> allOrders = orderRepository.findAllByKhachHangId((long) customer.getId());
            allOrders.forEach(o ->{
                if(o.getTrangThai().getTtTrangThai().equals("Đã giao")){
                    orders.add(o);
                }
            });
        }else{
            orders = orderRepository.findAllByKhachHangId((long) customer.getId());
        }

        orders.forEach(order -> {

            List<OrderItem> orderItemList = orderItemRepository.findAllByDonHang(order);
//            System.out.println("order item =" + orderItemList);
            List<ShortBookDto> shortBookDtos = new ArrayList<>();

            orderItemList.forEach(orderItem -> {
                shortBookDtos.add(ShortBookDto.builder()
                        .qty(orderItem.getOiSoLuong())
                        .price(orderItem.getOiDonGia())
                        .name(orderItem.getSach().getTen())
                                .img(orderItem.getSach().getImg())
                        .build());
            });
            PaymentAddress paymentAddress = order.getDiaChiGiaoHang();
            Payment payment = order.getPayment();
            String billingAddress = paymentAddress!=null? paymentAddress.getDcghDiaChi()+", "+paymentAddress.getDcghThanhPho()+", "+paymentAddress.getDcghTinh()
                    + ", "+paymentAddress.getDcghQuocGia() : "Vui lòng tiếp tục thanh toán.";
            String pMethod = payment !=null ? "Phương thức thanh toán: thanh toán bằng "+(payment.getPMethod().equals("card") ? "thẻ tín dụng." : "tiền mặt khi nhận hàng.") : "Vui lòng tiếp tục thanh toán.";
            orderListDtos.add(OrderListDto.builder()
                            .id(order.getDh_id())
                            .items(shortBookDtos)
                            .status(order.getTrangThai()
                            .getTtTrangThai())
                            .billingAddress(billingAddress)
                            .paymentMethod(pMethod)
                            .dateTime(new Date()).build());

        });

//        System.out.println("Order list:");
//        for (OrderListDto orderListDto : orderListDtos) {
//            System.out.println("order: ");
//            System.out.println("time = "+orderListDto.getDateTime());
//            System.out.println("status = "+orderListDto.getStatus());
//            for(int i=0; i<orderListDto.getItems().size(); i++){
//                System.out.println("item = "+orderListDto.getItems().get(i));
//            }
//            System.out.println("-----------------------------------");
//        }
        return orderListDtos;
    }

    public OrderDetailsDto getOrderDetails(Long orderId) throws Exception {
        Order order = orderRepository.findById(orderId).orElseThrow(()->new Exception(""));
        List<OrderItem> orderItemList = orderItemRepository.findAllByDonHang(order);
        List<ShortBookDto> shortBookDtos = new ArrayList<>();

        orderItemList.forEach(item ->{
            shortBookDtos.add(ShortBookDto.builder().name(item.getSach().getTen()).price(item.getOiDonGia()).qty(item.getOiSoLuong()).img(item.getSach().getImg()).build());
        });
//        System.out.println("order="+order.toString());

        List<OrderStatusHistory> orderStatusHistories = orderStatusHistoryRepository.findAllByDonHang(order);

        CustomerDto customerDto = CustomerDto.builder().name(order.getKhachHang().getFullName()).phoneNumber(order.getKhachHang().getPhoneNumber()).build();
        PaymentAddressDto paymentAddressDto = PaymentAddressDto.builder().dcghDiaChi(order.getDiaChiGiaoHang().getDcghDiaChi())
                .dcghNgayGiao(order.getDiaChiGiaoHang().getDcghNgayGiao())
                .postalCode(order.getDiaChiGiaoHang().getDcghPostalCode())
                .dcghQuocGia(order.getDiaChiGiaoHang().getDcghQuocGia())
                .dcghThanhPho(order.getDiaChiGiaoHang().getDcghThanhPho())
                .dcghTinh(order.getDiaChiGiaoHang().getDcghTinh())
                .build();
        PaymentDto paymentDto = PaymentDto.builder().paymentDate(order.getPayment().getPDate()).paymentMethod(order.getPayment().getPMethod()).totalAmount(order.getPayment().getPAmount()).build();

        if(!orderStatusHistories.isEmpty()){
            List<OrderStatusHistory> orderStatusHistories1 = new ArrayList<>();
            orderStatusHistories.forEach(orderStatusHistory -> {
                if(orderStatusHistory!=null){
                    orderStatusHistories1.add(orderStatusHistory);
                }
            });
            return OrderDetailsDto.builder().items(shortBookDtos).id(orderId).statusDtos(orderStatusHistories1).customerDto(customerDto).paymentAddressDto(paymentAddressDto).paymentDto(paymentDto).build();

        }
    return null;
    }


}
