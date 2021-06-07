package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.AddressDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerAddressDao;
import com.upgrad.FoodOrderingApp.service.dao.OrderDao;
import com.upgrad.FoodOrderingApp.service.dao.StateDao;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressService {

    @Autowired
    private StateDao stateDao;

    @Autowired
    private AddressDao addressDao;

    @Autowired
    private CustomerAddressDao customerAddressDao;

    @Autowired
    private OrderDao orderDao;

    /**
     * Save Address for a given customer.
     * This methods checks for a valid address and creates a mapping of a customer and corresponding address.
     *
     * @param addressEntity
     * @param customerEntity
     * @return
     * @throws SaveAddressException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity saveAddress(final AddressEntity addressEntity, final CustomerEntity customerEntity) throws SaveAddressException {
        if (addressEntity.getActive() == null
                || addressEntity.getLocality() == null || addressEntity.getLocality().isEmpty()
                || addressEntity.getCity() == null || addressEntity.getCity().isEmpty()
                || addressEntity.getFlatBuilNo() == null || addressEntity.getFlatBuilNo().isEmpty()
                || addressEntity.getPincode() == null || addressEntity.getPincode().isEmpty()
                || addressEntity.getState() == null) {
            throw new SaveAddressException("SAR-001", "No field can be empty");
        }

        if (!isPinCodeValid(addressEntity.getPincode())) {
            throw new SaveAddressException("SAR-002", "Invalid pincode");
        }

        AddressEntity createdCustomerAddress = addressDao.createCustomerAddress(addressEntity);

        CustomerAddressEntity createdCustomerAddressEntity = new CustomerAddressEntity();
        createdCustomerAddressEntity.setCustomer(customerEntity);
        createdCustomerAddressEntity.setAddress(createdCustomerAddress);
        customerAddressDao.createCustomerAddress(createdCustomerAddressEntity);
        return createdCustomerAddress;
    }

    /**
     * Get all the addresses for a particular customer.
     *
     * @param customerEntity
     * @return list of all addresses
     */
    public List<AddressEntity> getAllAddress(final CustomerEntity customerEntity) {
        List<AddressEntity> addressEntityList = new ArrayList<>();
        List<CustomerAddressEntity> customerAddressEntityList = addressDao.customerAddressByCustomer(customerEntity);
        if (customerAddressEntityList != null || !customerAddressEntityList.isEmpty()) {
            for(CustomerAddressEntity cae : customerAddressEntityList)
                    addressEntityList.add(cae.getAddress());
        }
        return addressEntityList;
    }

    /**
     * This method takes an address entity as input and deletes it.
     *
     * @param addressEntity
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity deleteAddress(final AddressEntity addressEntity) {
        final List<OrderEntity> orders = orderDao.getAllOrdersByAddress(addressEntity);
        if (orders == null || orders.isEmpty()) {
            return addressDao.deleteAddress(addressEntity);
        }
        addressEntity.setActive(0);
        return addressDao.updateAddress(addressEntity);
    }

    /**
     * This method returns a list of all the states in the database.
     * @return
     */
    public List<StateEntity> getAllStates() {
        return stateDao.getAllStates();
    }

    /**
     * This method returns the state corresponding to the provided UUID.
     * @param stateUuid
     * @return
     * @throws AddressNotFoundException
     */
    public StateEntity getStateByUUID(final String stateUuid) throws AddressNotFoundException {
        if (stateDao.getStateByUUID(stateUuid) == null) {
            throw new AddressNotFoundException("ANF-002", "No state by this id");
        }
        return stateDao.getStateByUUID(stateUuid);
    }

    /**
     * This method returns the address corresponding to the provided UUID.
     * @param addressId
     * @param customerEntity
     * @return
     * @throws AuthorizationFailedException
     * @throws AddressNotFoundException
     */
    public AddressEntity getAddressByUUID(final String addressId, final CustomerEntity customerEntity)
            throws AuthorizationFailedException, AddressNotFoundException {
        AddressEntity addressEntity = addressDao.getAddressByUUID(addressId);
        if (addressId.isEmpty()) {
            throw new AddressNotFoundException("ANF-005", "Address id can not be empty");
        }
        if (addressEntity == null) {
            throw new AddressNotFoundException("ANF-003", "No address by this id");
        }
        CustomerAddressEntity customerAddressEntity = customerAddressDao.customerAddressByAddress(addressEntity);
        if (!customerAddressEntity.getCustomer().getUuid().equals(customerEntity.getUuid())) {
            throw new AuthorizationFailedException(
                    "ATHR-004", "You are not authorized to view/update/delete any one else's address");
        }
        return addressEntity;
    }

    /**
     * This method validates the format of the provided pincode.
     * @param pincode
     * @return
     */
    private boolean isPinCodeValid(final String pincode) {
        if (pincode.length() != 6)
            return false;

        if (!StringUtils.isNumeric(pincode))
            return false;

        return true;
    }
}