package com.amigos.authenautho.demo.controllers;

import com.amigos.authenautho.demo.dto.CustomerDto;
import com.amigos.authenautho.demo.dto.UpdateUserInfo;
import com.amigos.authenautho.demo.entities.Customer;
import com.amigos.authenautho.demo.entities.user.User;
import com.amigos.authenautho.demo.entities.user.UserRepository;
import com.amigos.authenautho.demo.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class UserInfoController {
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    @GetMapping("get-role")
    public ResponseEntity<String> getRole(@RequestParam("email") String email) throws Exception {
        System.out.println(email);
        User user = userRepository.findByEmail(email).orElseThrow(()->new Exception(("")));

        return ResponseEntity.ok(String.valueOf(user.getRole()));
    }
    @GetMapping("/get-info")
    public ResponseEntity<CustomerDto> getCustomerInfo(@RequestParam("email") String email) throws Exception {
        System.out.println(email);
        User user = userRepository.findByEmail(email).orElseThrow(()->new Exception(("")));
        Optional<Customer> customer = customerRepository.findById(Long.valueOf(user.getKhId()));
        return ResponseEntity.ok(CustomerDto.builder().name(customer.get().getFullName()).phoneNumber(customer.get().getPhoneNumber())
                .address(customer.get().getAddress()).id((long) customer.get().getId()).build());
    }

    @PutMapping("/edit")
    public ResponseEntity<Boolean> updateKH(@RequestParam("email") String email, @RequestBody UpdateUserInfo updateUserInfo) throws Exception {

        System.out.println("ud="+updateUserInfo);
        User user = userRepository.findByEmail(email).orElseThrow(()->new Exception(""));
        List<Customer> customers = customerRepository.findAll();
        for(Customer c : customers){
            if(c.getPhoneNumber().equals(updateUserInfo.getPhone()) && !user.getEmail().equals((email))){
                return ResponseEntity.ok(false);
            }
        }
        Customer customer = customerRepository.findById((long) user.getKhId()).orElseThrow(()->new Exception(("")));
        customer.setAddress(updateUserInfo.getAddress());
        customer.setPhoneNumber(updateUserInfo.getPhone());
        customer.setFullName(updateUserInfo.getName());
        customerRepository.save(customer);
        return ResponseEntity.ok(true);
    }
}
