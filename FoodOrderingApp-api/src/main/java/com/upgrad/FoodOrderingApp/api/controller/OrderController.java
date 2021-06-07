package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CouponService;
import com.upgrad.FoodOrderingApp.service.businness.OrderService;
import com.upgrad.FoodOrderingApp.service.common.CommonValidation;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
@CrossOrigin
public class OrderController {
    @Autowired
    private CouponService couponService;

    @Autowired
    private CommonValidation commonValidation;

    @Autowired
    private OrderService orderService;

    @RequestMapping(path = "/order/coupon/{coupon_name}",method = RequestMethod.GET,produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CouponDetailsResponse> getCoupon(@RequestHeader("Authorization") String authHeader,@PathVariable("coupon_name") String couponName) throws CouponNotFoundException, AuthorizationFailedException {
        commonValidation.validateCustomerAuthEntity(authHeader);

        CouponEntity couponEntity = couponService.getCouponByName(couponName);
        CouponDetailsResponse couponDetailsResponse = new CouponDetailsResponse();
        couponDetailsResponse.id(UUID.fromString(couponEntity.getUuid())).couponName(couponEntity.getCouponName()).percent(couponEntity.getPercent());
        ResponseEntity<CouponDetailsResponse> response = new ResponseEntity<>(couponDetailsResponse, HttpStatus.OK);

        return response;
    }

    @RequestMapping(path = "/order",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<OrderList>>getAllPastOrders(@RequestHeader("Authorization")String authHeader) throws AuthorizationFailedException {
        CustomerAuthEntity customerAuthEntity = commonValidation.validateCustomerAuthEntity(authHeader);

        CustomerEntity customerEntity = customerAuthEntity.getCustomer();

        List<OrderEntity>orderEntities = orderService.getAllPastOrders(customerEntity);
        List<OrderList>ordersList = new ArrayList<>();

        for(OrderEntity orderEntity:orderEntities){
            CustomerEntity orderCustomer = orderEntity.getCustomer();
            CouponEntity couponEntity = orderEntity.getCoupon();
            AddressEntity addressEntity = orderEntity.getAddress();
            StateEntity orderState = addressEntity.getState();
            PaymentEntity paymentEntity = orderEntity.getPayment();

            OrderList orderList = new OrderList();

            OrderListCustomer orderListCustomer = new OrderListCustomer();
            OrderListCoupon orderListCoupon = new OrderListCoupon();
            OrderListAddress orderListAddress = new OrderListAddress();
            OrderListAddressState orderListAddressState = new OrderListAddressState();
            OrderListPayment orderListPayment = new OrderListPayment();


            orderListCoupon.id(UUID.fromString(couponEntity.getUuid())).couponName(couponEntity.getCouponName()).percent(couponEntity.getPercent());
            orderListCustomer.contactNumber(orderCustomer.getContactNumber()).id(UUID.fromString(customerEntity.getUuid())).emailAddress(customerEntity.getEmail()).firstName(customerEntity.getFirstName()).lastName(customerEntity.getLastName());
            orderListAddressState.id(UUID.fromString(orderState.getUuid())).stateName(orderState.getStateName());
            orderListAddress.city(addressEntity.getCity()).id(UUID.fromString(addressEntity.getUuid())).flatBuildingName(addressEntity.getFlatBuilNo()).locality(addressEntity.getLocality()).pincode(addressEntity.getPincode()).state(orderListAddressState);
            orderListPayment.id(UUID.fromString(paymentEntity.getUuid())).paymentName(paymentEntity.getPaymentName());

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String date = formatter.format(orderEntity.getDate());

            List<OrderItemEntity>orderItemEntities = orderEntity.getOrderItemEntities();
            List<ItemQuantityResponse> itemQuantityResponseList = new ArrayList<>();

            for(OrderItemEntity orderItemEntity:orderItemEntities){
                ItemEntity item = orderItemEntity.getItem();
                ItemQuantityResponse itemQuantityResponse = new ItemQuantityResponse();
                ItemQuantityResponseItem itemQuantityResponseItem = new ItemQuantityResponseItem();

                ItemQuantityResponseItem.TypeEnum typeEnum = ItemQuantityResponseItem.TypeEnum.valueOf(item.getType());
                itemQuantityResponseItem.id(UUID.fromString(item.getUuid())).itemName(item.getItemName()).itemPrice(item.getPrice()).type(typeEnum);

                itemQuantityResponse.item(itemQuantityResponseItem).quantity(orderItemEntity.getQuantity()).price(item.getPrice());

                itemQuantityResponseList.add(itemQuantityResponse);
            }

            orderList.id(UUID.fromString(orderEntity.getUuid())).bill(BigDecimal.valueOf(orderEntity.getBill())).coupon(orderListCoupon).discount(BigDecimal.valueOf(orderEntity.getDiscount())).date(date).payment(orderListPayment).customer(orderListCustomer).address(orderListAddress).itemQuantities(itemQuantityResponseList);

            ordersList.add(orderList);
        }

        ResponseEntity<List<OrderList>>responseEntity = new ResponseEntity<>(ordersList,HttpStatus.OK);
        return responseEntity;
    }
}
