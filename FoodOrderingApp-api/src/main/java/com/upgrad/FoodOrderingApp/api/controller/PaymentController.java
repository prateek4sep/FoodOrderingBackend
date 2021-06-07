package com.upgrad.FoodOrderingApp.api.controller;


import com.upgrad.FoodOrderingApp.api.model.PaymentListResponse;
import com.upgrad.FoodOrderingApp.api.model.PaymentResponse;
import com.upgrad.FoodOrderingApp.service.businness.PaymentService;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @RequestMapping(path = "/payment",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<PaymentListResponse>getAllPayments(){
        List<PaymentEntity>allPayments = paymentService.getAllPaymentMethods();

        List<PaymentResponse> allPaymentsResponse = new ArrayList<>();

        for(PaymentEntity paymentEntity:allPayments){
            PaymentResponse paymentResponse = new PaymentResponse();
            paymentResponse.id(UUID.fromString(paymentEntity.getUuid())).paymentName(paymentEntity.getPaymentName());

            allPaymentsResponse.add(paymentResponse);
        }

        PaymentListResponse paymentListResponse = new PaymentListResponse();
        paymentListResponse.paymentMethods(allPaymentsResponse);
        ResponseEntity<PaymentListResponse> response = new ResponseEntity<>(paymentListResponse, HttpStatus.OK);

        return response;
    }
}
