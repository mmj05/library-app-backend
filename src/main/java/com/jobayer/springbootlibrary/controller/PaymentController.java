package com.jobayer.springbootlibrary.controller;

import com.jobayer.springbootlibrary.requestmodels.PaymentInfoRequest;
import com.jobayer.springbootlibrary.service.PaymentService;
import com.jobayer.springbootlibrary.utils.ExtractJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment/secure")
public class PaymentController {

    private PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payment-intent")
    public ResponseEntity<String> createPaymentIntent(@RequestHeader(value = "Authorization") String token,
                                                      @RequestBody PaymentInfoRequest paymentInfoRequest) throws Exception {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        if (userEmail == null) {
            throw new Exception("User email is missing");
        }

        return paymentService.createPaymentIntent(paymentInfoRequest, userEmail);
    }

    @PutMapping("/payment-complete")
    public ResponseEntity<String> stripePaymentComplete(@RequestHeader(value = "Authorization") String token) throws Exception {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        if (userEmail == null) {
            throw new Exception("User email is missing");
        }

        return paymentService.stripePayment(userEmail);
    }
}
