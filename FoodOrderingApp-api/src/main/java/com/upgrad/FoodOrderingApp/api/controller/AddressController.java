package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.AddressService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.common.CommonValidation;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
public class AddressController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AddressService addressService;

    private CommonValidation commonValidation = new CommonValidation();

    /**
     * Save address of a customer in the database.
     * This method performs the validation of access Token and creates an AddressEntity based on the request.
     *
     * @param bearerToken (Bearer <access-token>).
     * @return ResponseEntity<SaveAddressResponse>
     * @throws AuthorizationFailedException
     */
    @RequestMapping(
            method = RequestMethod.POST,
            path = "/address",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveAddressResponse> saveAddress(
            @RequestHeader("authorization") final String bearerToken,
            @RequestBody(required = false) final SaveAddressRequest saveAddressRequest)
            throws SaveAddressException, AuthorizationFailedException, AddressNotFoundException {

        // Extracting the access Token from bearer token.
        String accessToken = commonValidation.getAccessTokenFromBearer(bearerToken);

        CustomerEntity customerEntity = customerService.getCustomer(accessToken);

        // Creating an address entity with details passed in the request.
        final AddressEntity addressEntity = new AddressEntity();
        if (saveAddressRequest != null) {
            addressEntity.setUuid(UUID.randomUUID().toString());
            addressEntity.setCity(saveAddressRequest.getCity());
            addressEntity.setLocality(saveAddressRequest.getLocality());
            addressEntity.setPincode(saveAddressRequest.getPincode());
            addressEntity.setFlatBuilNo(saveAddressRequest.getFlatBuildingName());
            addressEntity.setActive(1);
        }
        addressEntity.setState(addressService.getStateByUUID(saveAddressRequest.getStateUuid()));

        final AddressEntity savedAddress = addressService.saveAddress(addressEntity, customerEntity);
        SaveAddressResponse saveAddressResponse =
                new SaveAddressResponse().id(savedAddress.getUuid()).status("ADDRESS SUCCESSFULLY REGISTERED");

        return new ResponseEntity<>(saveAddressResponse, HttpStatus.CREATED);
    }

    /**
     * Fetch all the saved addresses for a customer from the database.
     * THis method validates access token, fetches the Address list and adds it to response.
     *
     * @param bearerToken (Bearer <access-token>)
     * @return ResponseEntity<AddressListResponse>
     */
    @RequestMapping(
            method = RequestMethod.GET,
            path = "/address/customer",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AddressListResponse> getAllAddress(
            @RequestHeader("authorization") final String bearerToken) throws AuthorizationFailedException {

        // Extracting the access Token from bearer token.
        String accessToken = commonValidation.getAccessTokenFromBearer(bearerToken);

        final CustomerEntity customerEntity = customerService.getCustomer(accessToken);

        final List<AddressEntity> addressEntityList = addressService.getAllAddress(customerEntity);

        final AddressListResponse addressListResponse = new AddressListResponse();

        // Adding addresses to the response or return an empty list if no record is found.
        if (!addressEntityList.isEmpty()) {
            for (AddressEntity addressEntity : addressEntityList) {
                AddressList addressResponseList = new AddressList()
                                .id(UUID.fromString(addressEntity.getUuid()))
                                .flatBuildingName(addressEntity.getFlatBuilNo())
                                .city(addressEntity.getCity())
                                .pincode(addressEntity.getPincode())
                                .locality(addressEntity.getLocality())
                                .state(new AddressListState().id(UUID.fromString(addressEntity.getState().getUuid()))
                                                .stateName(addressEntity.getState().getStateName()));
                addressListResponse.addAddressesItem(addressResponseList);
            }
        } else {
            List<AddressList> addresses = Collections.emptyList();
            addressListResponse.addresses(addresses);
        }

        return new ResponseEntity<>(addressListResponse, HttpStatus.OK);
    }

    /**
     * Simply Fetch all the states from the database and add to response.
     *
     * @return ResponseEntity<StatesListResponse>
     */
    @RequestMapping(
            method = RequestMethod.GET,
            path = "/states",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StatesListResponse> getAllStates() {
        final StateEntity stateEntity = new StateEntity();
        stateEntity.setUuid(UUID.randomUUID().toString());

        final List<StateEntity> statesLists = addressService.getAllStates();

        final StatesListResponse statesListResponse = new StatesListResponse();
        for (StateEntity statesEntity : statesLists) {
            StatesList states = new StatesList().id(UUID.fromString(statesEntity.getUuid()))
                    .stateName(statesEntity.getStateName());
            statesListResponse.addStatesItem(states);
        }
        return new ResponseEntity<>(statesListResponse, HttpStatus.OK);
    }

    /**
     * Delete Address using the address ID provided and respond with ID.
     *
     * @param bearerToken
     * @param addressId
     * @return Response with UUID
     * @throws AuthorizationFailedException
     * @throws AddressNotFoundException
     */
    @RequestMapping(
            path = "/address/{address_id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DeleteAddressResponse> deleteAddress(
            @RequestHeader("authorization") final String bearerToken,
            @PathVariable("address_id") final String addressId)
            throws AuthorizationFailedException, AddressNotFoundException {

        // Extracting the access Token from bearer token.
        String accessToken = commonValidation.getAccessTokenFromBearer(bearerToken);

        // Deleting the Address and add ID to response.
        final CustomerEntity customerEntity = customerService.getCustomer(accessToken);
        AddressEntity addressEntity = addressService.getAddressByUUID(addressId, customerEntity);
        final AddressEntity deleteAddress = addressService.deleteAddress(addressEntity);
        final DeleteAddressResponse deleteAddressResponse =
                new DeleteAddressResponse().id(UUID.fromString(deleteAddress.getUuid()))
                        .status("ADDRESS DELETED SUCCESSFULLY");

        return new ResponseEntity<>(deleteAddressResponse, HttpStatus.OK);
    }
}