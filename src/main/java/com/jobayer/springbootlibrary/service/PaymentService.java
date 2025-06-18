package com.jobayer.springbootlibrary.service;

import com.jobayer.springbootlibrary.dao.PaymentRepository;
import com.jobayer.springbootlibrary.entity.Payment;
import com.jobayer.springbootlibrary.requestmodels.PaymentInfoRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class PaymentService {

    private PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public ResponseEntity<String> createPaymentIntent(PaymentInfoRequest paymentInfoRequest, String userEmail) throws Exception {
        Payment payment = paymentRepository.findByUserEmail(userEmail);

        if (payment == null) {
            throw new Exception("Payment information is missing");
        }

        // For demonstration purposes, return a mock response with proper Stripe format
        // In a real application, you would integrate with Stripe API here
        long timestamp = System.currentTimeMillis();
        String mockPaymentIntentId = "pi_mock" + timestamp;
        String mockSecret = "secret_" + timestamp;
        String clientSecret = mockPaymentIntentId + "_secret_" + mockSecret;
        
        // Convert map to JSON string manually since we're returning String
        String jsonResponse = "{\"client_secret\":\"" + clientSecret + "\"}";
        
        return ResponseEntity.ok(jsonResponse);
    }

    public ResponseEntity<String> stripePayment(String userEmail) throws Exception {
        Payment payment = paymentRepository.findByUserEmail(userEmail);

        if (payment == null) {
            throw new Exception("Payment information is missing");
        }

        payment.setAmount(00.00);
        paymentRepository.save(payment);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
