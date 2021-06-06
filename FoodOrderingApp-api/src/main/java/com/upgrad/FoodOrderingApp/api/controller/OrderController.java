package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.CouponDetailsResponse;
import com.upgrad.FoodOrderingApp.service.businness.CouponService;
import com.upgrad.FoodOrderingApp.service.common.CommonValidation;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/")
@CrossOrigin
public class OrderController {
    @Autowired
    private CouponService couponService;

    @Autowired
    private CommonValidation commonValidation;

    @RequestMapping(path = "/order/coupon/{coupon_name}",method = RequestMethod.GET,produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CouponDetailsResponse> getCoupon(@RequestHeader("Authorization") String authHeader,@PathVariable("coupon_name") String couponName) throws CouponNotFoundException, AuthorizationFailedException {
        commonValidation.validateCustomerAuthEntity(authHeader);

        CouponEntity couponEntity = couponService.getCouponByName(couponName);
        CouponDetailsResponse couponDetailsResponse = new CouponDetailsResponse();
        couponDetailsResponse.id(UUID.fromString(couponEntity.getUuid())).couponName(couponEntity.getCouponName()).percent(couponEntity.getPercent());
        ResponseEntity<CouponDetailsResponse> response = new ResponseEntity<>(couponDetailsResponse, HttpStatus.OK);

        return response;
    }
}
