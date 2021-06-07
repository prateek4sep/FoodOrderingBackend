package com.upgrad.FoodOrderingApp.service.dao;


import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class OrderDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<OrderEntity> getAllPastOrders(String customerUUID){
        try{
            return entityManager.createNamedQuery("getAllPastOrders",OrderEntity.class).setParameter("uuid",customerUUID).getResultList();
        }
        catch (NoResultException nre){
            return null;
        }
    }
}
