package com.upgrad.FoodOrderingApp.service.businness;


import com.upgrad.FoodOrderingApp.service.dao.CouponDao;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CouponService {
    @Autowired
    private CouponDao couponDao;

    public CouponEntity getCouponByName(String couponName) throws CouponNotFoundException {
        CouponEntity couponEntity = couponDao.getCouponByName(couponName);

        if(couponEntity == null){
            throw new CouponNotFoundException("CPF-001","No coupon by this name");
        }else if(couponName==" "){
            throw new CouponNotFoundException("CPF-002","Coupon name field should not be empty");
        }
        return couponEntity;
    }
}
