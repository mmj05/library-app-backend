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


@Service
@Transactional
public class PaymentService {

    private PaymentRepository paymentRepository;


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
