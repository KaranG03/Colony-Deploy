package com.karan.property.controller;

import com.karan.property.entity.User;
import com.karan.property.repository.UserRepo;
import com.karan.property.service.RazorPayService;
import com.razorpay.RazorpayException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private RazorPayService razorpayService;

    @Autowired
    private UserRepo userRepo;


    @PostMapping("/create-order")
    public String createOrder(@RequestParam int amount, @RequestParam String currency) {
        try {
            String res = razorpayService.createOrder(amount, currency, "recepient_100");
            if(res.contains("created")) {
                Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                    String username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
                    User user = userRepo.findByusername(username);
                    user.setPrimeStatus(1);
                    userRepo.save(user);
                }
            }
            return res;
        } catch (RazorpayException e) {
            throw new RuntimeException(e);
        }
    }


}