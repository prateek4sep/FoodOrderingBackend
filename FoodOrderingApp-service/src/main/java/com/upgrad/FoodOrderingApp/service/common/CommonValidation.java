package com.upgrad.FoodOrderingApp.service.common;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class CommonValidation {

    @Autowired
    private CustomerDao customerDao;

    public CustomerAuthEntity validateCustomerAuthEntity(final String accessToken) throws AuthorizationFailedException {
        CustomerAuthEntity authEntity = customerDao.getCustomerByAccessToken(accessToken);

        
        if (authEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }

        
        if (authEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }

        final ZonedDateTime now = ZonedDateTime.now();
        if (authEntity.getExpiresAt().isBefore(now)) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }
        return authEntity;
    }
}
