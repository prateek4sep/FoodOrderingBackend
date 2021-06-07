package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.AddressDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerAddressDao;
import com.upgrad.FoodOrderingApp.service.dao.StateDao;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
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

    public List<AddressEntity> getAllAddress(final CustomerEntity customerEntity) {
        List<AddressEntity> addressEntityList = new ArrayList<>();
        List<CustomerAddressEntity> customerAddressEntityList = addressDao.customerAddressByCustomer(customerEntity);
        if (customerAddressEntityList != null || !customerAddressEntityList.isEmpty()) {
            for(CustomerAddressEntity cae : customerAddressEntityList)
                    addressEntityList.add(cae.getAddress());
        }
        return addressEntityList;
    }


    public List<StateEntity> getAllStates() {
        return stateDao.getAllStates();
    }


    public StateEntity getStateByUUID(final String stateUuid) throws AddressNotFoundException {
        if (stateDao.getStateByUUID(stateUuid) == null) {
            throw new AddressNotFoundException("ANF-002", "No state by this id");
        }
        return stateDao.getStateByUUID(stateUuid);
    }

    private boolean isPinCodeValid(final String pincode) {
        if (pincode.length() != 6)
            return false;

        if (!StringUtils.isNumeric(pincode))
            return false;
        
        return true;
    }
}