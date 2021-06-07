package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;

@Repository
public class AddressDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Creates an address entity using the given AddressEntity.
     *
     * @param addressEntity
     * @return AddressEntity
     */
    public AddressEntity createCustomerAddress(final AddressEntity addressEntity) {
        entityManager.persist(addressEntity);
        return addressEntity;
    }

    /**
     * Fetch all the addresses of a given customer.
     *
     * @param customer
     * @return List of CustomerAddressEntity
     */
    public List<CustomerAddressEntity> customerAddressByCustomer(CustomerEntity customer) {
        List<CustomerAddressEntity> addresses =
                entityManager
                        .createNamedQuery("customerAddressByCustomer", CustomerAddressEntity.class)
                        .setParameter("customer", customer)
                        .getResultList();
        if (addresses == null) {
            return Collections.emptyList();
        }
        return addresses;
    }

}