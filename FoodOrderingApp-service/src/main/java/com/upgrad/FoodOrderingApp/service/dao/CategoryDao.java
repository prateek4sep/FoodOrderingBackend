package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class CategoryDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<CategoryEntity> getAllCategoriesOrderedByName() {
        return entityManager.createNamedQuery("allCategoriesOrderedByName", CategoryEntity.class)
                .getResultList();
    }


    public CategoryEntity getCategoryById(final String categoryUuid) {
        try {
            return entityManager.createNamedQuery("categoryByUuid", CategoryEntity.class)
                    .setParameter("categoryUuid", categoryUuid)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }


    public List<RestaurantCategoryEntity> getCategoriesByRestaurant(final String restaurantUuid) {
        return entityManager.createNamedQuery("categoriesByRestaurant", RestaurantCategoryEntity.class)
                .setParameter("restaurantUuid", restaurantUuid)
                .getResultList();
    }

}
