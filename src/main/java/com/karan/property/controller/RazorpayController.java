package com.karan.property.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RazorpayController {

    // Inject the Razorpay public API key from application.properties
    @Value("${razorpay.api.key}")
    private String razorpayApiKey;

    // Endpoint to get the Razorpay public API key
    @GetMapping("/api/payment/razorpay-key")
    public String getRazorpayKey() {
        return razorpayApiKey;  // Return the Razorpay public key to the frontend
    }
}
