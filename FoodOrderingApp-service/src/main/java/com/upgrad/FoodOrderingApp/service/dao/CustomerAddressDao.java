package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class CustomerAddressDao {

    @PersistenceContext private EntityManager entityManager;

    /**
     * Creates a customer address using the given address entity.
     *
     * @param customerAddressEntity
     * @return CustomerAddressEntity
     */
    public void createCustomerAddress(final CustomerAddressEntity customerAddressEntity) {
        entityManager.persist(customerAddressEntity);
    }

}