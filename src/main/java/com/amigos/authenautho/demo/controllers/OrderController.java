package com.amigos.authenautho.demo.controllers;

import com.amigos.authenautho.demo.dto.OrderDetailsDto;
import com.amigos.authenautho.demo.dto.OrderListDto;
import com.amigos.authenautho.demo.entities.Order;
import com.amigos.authenautho.demo.entities.OrderStatusHistory;
import com.amigos.authenautho.demo.entities.Status;
import com.amigos.authenautho.demo.repositories.OrderRepository;
import com.amigos.authenautho.demo.repositories.OrderStatusHistoryRepository;
import com.amigos.authenautho.demo.repositories.StatusRepository;
import com.amigos.authenautho.demo.services.TrackingOrderServices;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;
    private final TrackingOrderServices trackingOrderServices;
    private final StatusRepository statusRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;

    @GetMapping("/tracking")
    public ResponseEntity<OrderDetailsDto> trackingOrder(@RequestParam("orderid") Long orderid) throws Exception {
        System.out.println("Order = "+orderid);
        OrderDetailsDto orderDetailsDto = trackingOrderServices.getOrderDetails(orderid);
//        System.out.println(orderDetailsDto);
        return ResponseEntity.ok(orderDetailsDto);
    }

//    const queryString = "email=email@2309";
    //fetch(`/api/myendpoint?${queryString}`);
    @GetMapping("/all")
    public ResponseEntity<List<OrderListDto>> getAllOrders(@RequestParam("email") String email) throws Exception {
        return ResponseEntity.ok(trackingOrderServices.getAllOrdersByEmail(email,"all"));
    }

    @GetMapping("/processing")
    public ResponseEntity<List<OrderListDto>> getAllProcessingOrders(@RequestParam("email") String email) throws Exception {
        return ResponseEntity.ok(trackingOrderServices.getAllOrdersByEmail(email,"processing"));
    }

    @GetMapping("/canceled")
    public ResponseEntity<List<OrderListDto>> getAllCanceledOrders(@RequestParam("email") String email) throws Exception {
        System.out.println("Email="+email);
        return ResponseEntity.ok(trackingOrderServices.getAllOrdersByEmail(email,"canceled"));
    }

    @GetMapping("/delivering")
    public ResponseEntity<List<OrderListDto>> getAllDeliveringOrders(@RequestParam("email") String email) throws Exception {
        return ResponseEntity.ok(trackingOrderServices.getAllOrdersByEmail(email,"delivering"));
    }

    @GetMapping("/delivered")
    public ResponseEntity<List<OrderListDto>> getAllDeliveredOrders(@RequestParam("email") String email) throws Exception {
        return ResponseEntity.ok(trackingOrderServices.getAllOrdersByEmail(email,"delivered"));
    }

    @PutMapping("/cancel-order")
    public ResponseEntity<String> cancelOrder(@RequestParam("id") Long id) throws Exception {
        System.out.println("Cancel id="+id);
        Order order = orderRepository.findById(id).orElseThrow(()-> new Exception(""));
        Status status = statusRepository.findByTtTrangThai("Khách hàng đã hủy đơn").orElseThrow(()-> new Exception(""));

        PaymentIntent paymentIntent = PaymentIntent.retrieve(order.getPiId());
        PaymentIntent update = paymentIntent.cancel();

        order.setTrangThai(status);
        OrderStatusHistory orderStatusHistory = OrderStatusHistory.builder().donHang(order).trangThai(status).statusChangeDate(new Date()).build();
        orderRepository.save(order);
        orderStatusHistoryRepository.save(orderStatusHistory);
        return ResponseEntity.ok("OK");
    }
}
