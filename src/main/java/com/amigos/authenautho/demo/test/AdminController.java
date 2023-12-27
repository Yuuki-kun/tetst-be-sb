//package com.amigos.authenautho.demo.test;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/v1/admin")
//@PreAuthorize("hasRole('ADMIN')")
//public class AdminController {
//
//    @PreAuthorize("hasAnyAuthority('admin:read')")
//    @GetMapping
////    public String get(){
////        return "GET::Admin Controller";
////    }
//        public ResponseEntity<String> get(){
//        return ResponseEntity.ok("ADMIN_GET");
//    }
//
//    @PreAuthorize("hasAnyAuthority('admin:create')")
//    @PostMapping
//    public String post(){
//        return "POST::Admin Controller";
//    }
//
//    @PreAuthorize("hasAnyAuthority('admin:update')")
//    @PutMapping
//    public String put(){
//        return "PUT::Admin Controller";
//    }
//
//    @PreAuthorize("hasAnyAuthority('admin:delete')")
//    @DeleteMapping
//    public String delete(){
//        return "DELETE::Admin Controller";
//    }
//}
