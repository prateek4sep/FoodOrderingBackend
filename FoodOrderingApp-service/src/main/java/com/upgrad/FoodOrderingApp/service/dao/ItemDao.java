package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class ItemDao {

    @PersistenceContext
    private EntityManager entityManager;

    public ItemEntity getItemById(final String itemUuid) {
        try {
            return entityManager.createNamedQuery("itemById", ItemEntity.class)
                    .setParameter("itemUuid", itemUuid)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }


    public List<ItemEntity> getItemsByPopularity(final Integer restaurantId) {
        return entityManager.createNamedQuery("topFivePopularItemsByRestaurant", ItemEntity.class)
                .setParameter(0, restaurantId)
                .getResultList();
    }


    public List<ItemEntity> getItemsByCategoryAndRestaurant(final String restaurantUuid, final String categoryUuid) {
        return entityManager.createNamedQuery("itemsByCategoryByRestaurant", ItemEntity.class)
                .setParameter("restaurantUuid", restaurantUuid)
                .setParameter("categoryUuid", categoryUuid)
                .getResultList();
    }
}
