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

    /**
     * Validate the access token provided and returns the corresponding CustomerAuthEntity.
     * @param accessToken
     * @return
     * @throws AuthorizationFailedException
     */
    public CustomerAuthEntity validateCustomerAuthEntity(final String accessToken) throws AuthorizationFailedException {
        CustomerAuthEntity authEntity = customerDao.getCustomerByAccessToken(accessToken);

        // Throw exception if the customer is not logged in
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

    /**
     * Fetch access token from bearer token
     * @param bearerToken
     * @return
     * @throws AuthorizationFailedException
     */
    public String getAccessTokenFromBearer(String bearerToken) throws AuthorizationFailedException {
        String accessToken;
        try {
            accessToken = bearerToken.split("Bearer ")[1];
        } catch (ArrayIndexOutOfBoundsException e){
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }
        return accessToken;
    }

    /**
     * This method validates if a field is empty or not.
     * @param fieldValue
     * @return
     */
    public boolean isEmptyFieldValue(final String fieldValue) {
        return fieldValue == null || fieldValue.isEmpty();
    }
}
