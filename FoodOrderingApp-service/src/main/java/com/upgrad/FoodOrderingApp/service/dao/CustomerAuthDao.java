package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class CustomerAuthDao {

    @PersistenceContext private EntityManager entityManager;
    /**
     * Takes access token as an argument and provides the authentication.
     *
     * @param accessToken : Access Token for authentication
     * @return User Auth Details
     */
    public CustomerAuthEntity getUserAuthByToken(final String accessToken) {
        try {
            return entityManager
                    .createNamedQuery("customerByAccessToken", CustomerAuthEntity.class)
                    .setParameter("accessToken", accessToken)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
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