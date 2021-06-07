package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class CustomerAuthDao {

    @PersistenceContext private EntityManager entityManager;

    /**
     * Persists userAuthEntity in DB.
     *
     * @param userAuthEntity to be persisted in the DB.
     * @return UserAuthEntity
     */
    public CustomerAuthEntity createAuthToken(final CustomerAuthEntity userAuthEntity) {
        entityManager.persist(userAuthEntity);
        return userAuthEntity;
    }
}