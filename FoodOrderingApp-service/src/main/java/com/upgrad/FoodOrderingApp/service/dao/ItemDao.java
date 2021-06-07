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

    /**
     * Fetch Item by Given ItemUuID
     * @param itemUuid
     * @return ItemEntity
     */
    public ItemEntity getItemById(final String itemUuid) {
        try {
            return entityManager.createNamedQuery("itemById", ItemEntity.class)
                    .setParameter("itemUuid", itemUuid)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Fetch All Items by Popularity using given RestaurantUuID
     * @param restaurantId
     * @return List of ItemEntity
     */
    public List<ItemEntity> getItemsByPopularity(final Integer restaurantId) {
        return entityManager.createNamedQuery("topFivePopularItemsByRestaurant", ItemEntity.class)
                .setParameter(0, restaurantId)
                .getResultList();
    }

    /**
     *Fetch List of ItemEntity using by RestaurantUuID and CategoryUuID
     * @param restaurantUuid
     * @param categoryUuid
     * @return
     */
    public List<ItemEntity> getItemsByCategoryAndRestaurant(final String restaurantUuid, final String categoryUuid) {
        return entityManager.createNamedQuery("itemsByCategoryByRestaurant", ItemEntity.class)
                .setParameter("restaurantUuid", restaurantUuid)
                .setParameter("categoryUuid", categoryUuid)
                .getResultList();
    }
}
