package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class RestaurantDao {

  @PersistenceContext
  private EntityManager entityManager;

  public List<RestaurantEntity> getAllRestaurants() {
    return entityManager.createNamedQuery("allRestaurants", RestaurantEntity.class).getResultList();
  }


  public List<RestaurantEntity> restaurantsByName(final String restaurantName) {
    return entityManager.createNamedQuery("restaurantsByName", RestaurantEntity.class)
            .setParameter("restaurantName", "%" + restaurantName.toLowerCase() + "%")
            .getResultList();
  }


  public List<RestaurantCategoryEntity> restaurantsByCategoryId(final String categoryUuid) {
    return entityManager.createNamedQuery("restaurantsByCategory", RestaurantCategoryEntity.class)
            .setParameter("categoryUuid", categoryUuid)
            .getResultList();
  }


  public RestaurantEntity restaurantByUUID(final String restaurantUuid) {
    try {
      return entityManager.createNamedQuery("restaurantByUuid", RestaurantEntity.class)
              .setParameter("restaurantUuid", restaurantUuid)
              .getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }

  public RestaurantEntity updateRestaurantEntity(final RestaurantEntity restaurantEntity) {
    return entityManager.merge(restaurantEntity);
  }
}
