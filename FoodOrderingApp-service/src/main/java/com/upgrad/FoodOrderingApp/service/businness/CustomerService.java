package com.upgrad.FoodOrderingApp.service.businness;


import com.upgrad.FoodOrderingApp.service.dao.CustomerAuthDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CustomerService {

    @Autowired private CustomerDao userDao;

    @Autowired private CustomerAuthDao userAuthDao;

    @Autowired private PasswordCryptographyProvider passwordCryptographyProvider;

    /**
     * This service method assigns a UUID, sets an encrypted password and salt for the user signing up.
     * This method handle also exceptions in case of a duplicate username or if the user exists in the DB.
     *
     * @throws SignUpRestrictedException : Exception thrown if user/email already exists in the DB.
     * @return CustomerEntity with user details
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity saveCustomer(CustomerEntity customerEntity) throws SignUpRestrictedException {
        if (contactNumberExists(customerEntity.getContactNumber())) {
            throw new SignUpRestrictedException(
                    "SGR-001", "This contact number is already registered! Try other contact number.");
        }

        if (emptyFields(customerEntity)) {
            throw new SignUpRestrictedException(
                    "SGR-005", "Except last name all fields should be filled");
        }

        if (emailNotInCorrectFormat(customerEntity.getEmail())) {
            throw new SignUpRestrictedException(
                    "SGR-002", "Invalid email-id format!");
        }

        if (contactNumberNotInCorrectFormat(customerEntity.getContactNumber())) {
            throw new SignUpRestrictedException(
                    "SGR-003", "Invalid contact number!");
        }

        if (passwordNotInCorrectFormat(customerEntity.getContactNumber())) {
            throw new SignUpRestrictedException(
                    "SGR-004", "Weak password!");
        }

        customerEntity.setUuid(UUID.randomUUID().toString());

        //Encrypt the password and set salt
        String[] encryptedText = passwordCryptographyProvider.encrypt(customerEntity.getPassword());
        customerEntity.setSalt(encryptedText[0]);
        customerEntity.setPassword(encryptedText[1]);
        return userDao.createCustomer(customerEntity);
    }

    /**
     * Sign in method takes contactNumber and password as argument, create and sets an auth token and opens a session.
     *
     * @param contactNumber
     * @param password
     * @throws AuthenticationFailedException : If the user is not found or password is invalid
     * @return CustomerAuthEntity access token and response.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity authenticate(String contactNumber, String password)
            throws AuthenticationFailedException {
        if (emptyUsernameOrPassword(contactNumber,password)) {
            throw new AuthenticationFailedException("ATH-003", "Incorrect format of decoded customer name and password");
        }

        CustomerEntity customerEntity = userDao.getCustomerByContactNumber(contactNumber);
        if (customerEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This contact number has not been registered!");
        }

        final String encryptedPassword = passwordCryptographyProvider.encrypt(password, customerEntity.getSalt());
        if (!encryptedPassword.equals(customerEntity.getPassword())) {
            throw new AuthenticationFailedException("ATH-002", "Invalid Credentials");
        }

        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
        CustomerAuthEntity customerAuthEntity = new CustomerAuthEntity();
        customerAuthEntity.setUuid(UUID.randomUUID().toString());
        customerAuthEntity.setCustomer(customerEntity);

        final ZonedDateTime now = ZonedDateTime.now();
        final ZonedDateTime expiresAt = now.plusHours(8);
        customerAuthEntity.setAccessToken(
                jwtTokenProvider.generateToken(customerEntity.getUuid(), now, expiresAt));
        customerAuthEntity.setLoginAt(now);
        customerAuthEntity.setExpiresAt(expiresAt);

        userAuthDao.createAuthToken(customerAuthEntity);

        return customerAuthEntity;
    }

    /**
     * This method takes the access Token for validation and signs the user out.
     *
     * @param authorization : required for validation and sign out
     * @throws AuthorizationFailedException : Thrown if the access-token is not found in the DB.
     * @return CustomerEntity : Signed out user.
//     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity logout(final String authorization) throws AuthorizationFailedException {
        CustomerAuthEntity customerAuthEntity = userDao.getCustomerByAccessToken(authorization);

        if (customerAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }

        ZonedDateTime expireTime = customerAuthEntity.getExpiresAt();
        ZonedDateTime currentTime = ZonedDateTime.now();

        if (expireTime.isBefore(currentTime)) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }


        ZonedDateTime logoutAtTime = customerAuthEntity.getLogoutAt();
        if (logoutAtTime != null) {
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");

        }

        customerAuthEntity.setLogoutAt(ZonedDateTime.now());
        userDao.updateCustomerAuth(customerAuthEntity);

        return customerAuthEntity;
    }

    /**
     * Check if the Contact Number already exists in the DB.
     *
     * @param contactNumber
     * @return true/false
     */
    private boolean contactNumberExists(final String contactNumber) {
        return userDao.getCustomerByContactNumber(contactNumber) != null;
    }

    /**
     * Checks if the email is in correct format.
     *
     * @param email
     * @return true/false
     */
    private boolean emailNotInCorrectFormat(final String email) {
        Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Za-z0-9]+@[A-Za-z0-9]+\\.[A-Za-z0-9]$");
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return !matcher.find();
    }

    /**
     * Checks if any field except Last Name is empty.
     * @param customerEntity
     * @return
     */
    private boolean emptyFields(CustomerEntity customerEntity){
        if (customerEntity.getFirstName() == null || customerEntity.getEmail() == null || customerEntity.getContactNumber() == null
                || customerEntity.getPassword() == null) {
            return true;
        } else
            return false;
    }

    /**
     * Checks if the contact number is in correct format.
     *
     * @param contactNumber
     * @return true/false
     */
    private boolean contactNumberNotInCorrectFormat(final String contactNumber) {
        Pattern PHONE_NUMBER_REGEX = Pattern.compile("^\\d{10}$");
        Matcher matcher = PHONE_NUMBER_REGEX.matcher(contactNumber);
        return !matcher.find();
    }

    /**
     * Checks if the password is in correct format.
     *
     * @param password
     * @return true/false
     */
    private boolean passwordNotInCorrectFormat(final String password) {
        Pattern PHONE_NUMBER_REGEX = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
        Matcher matcher = PHONE_NUMBER_REGEX.matcher(password);
        return !matcher.find();
    }

    /**
     * Checks whether the Username and password extracted from decoded Header is not empty.
     *
     * @return
     */
    private boolean emptyUsernameOrPassword(final String username, final String password) {
        if(username=="" || password=="") return true;
        else return false;
    }
}
