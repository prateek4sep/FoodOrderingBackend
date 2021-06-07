package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class OrderDao {

    @PersistenceContext private EntityManager entityManager;

    /**
     * Fetch all the orders for a given address.
     *
     * @param addressEntity
     * @return List<OrderEntity>
     */
    public List<OrderEntity> getAllOrdersByAddress(final AddressEntity addressEntity) {
        return entityManager.createNamedQuery("allOrdersByAddress", OrderEntity.class)
                .setParameter("address", addressEntity).getResultList();
    }
}