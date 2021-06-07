package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.common.CommonValidation;
import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class RestaurantService {

  @Autowired
  private RestaurantDao restaurantDao;

  @Autowired
  private CategoryDao categoryDao;

  @Autowired
  private CommonValidation commonValidation;

  /**
   * Fetch list of all restaurants
   *
   * @return restaurant list
   */
  public List<RestaurantEntity> getAllRestaurants() {
    return restaurantDao.getAllRestaurants();
  }

  /**
   * Fetch restaurants based on restaurant name
   *
   * @param restaurantName- input for which restaurants needs to be fetched
   * @throws RestaurantNotFoundException - if restaurant name is empty
   * @return- List of restaurant
   */
  public List<RestaurantEntity> restaurantsByName(final String restaurantName) throws RestaurantNotFoundException {

    // Throw exception if the restaurant name is empty
    if (commonValidation.isEmptyFieldValue(restaurantName)) {
      throw new RestaurantNotFoundException("RNF-003", "Restaurant name field should not be empty");
    }

    return restaurantDao.restaurantsByName(restaurantName);
  }

  /**
   * Fetch list of restaurant based on category
   *
   * @param categoryUuid- input for which restaurants needs to be fetched
   * @throws CategoryNotFoundException - if category uuid is empty or invalid
   * @return- List of restaurant
   */
  public List<RestaurantEntity> restaurantByCategory(final String categoryUuid) throws CategoryNotFoundException {

    if (commonValidation.isEmptyFieldValue(categoryUuid)) {
      throw new CategoryNotFoundException("CNF-001", "Category id field should not be empty");
    }

    CategoryEntity categoryEntity = categoryDao.getCategoryById(categoryUuid);
    if (categoryEntity == null) {
      throw new CategoryNotFoundException("CNF-002", "No category by this id");
    }

    List<RestaurantEntity> restaurantEntities = new ArrayList<>();

    List<RestaurantCategoryEntity> restaurantCategoryEntities = restaurantDao
            .restaurantsByCategoryId(categoryEntity.getUuid());
    restaurantCategoryEntities.forEach(restaurantCategoryEntity -> restaurantEntities.add(restaurantCategoryEntity.getRestaurant()));
    return restaurantEntities;
  }

  /**
   * Fetch restaurant object based on restaurant id
   *
   * @param restaurantUuid - input for which restaurant details needs to be fetched
   * @return - restaurant object
   * @throws RestaurantNotFoundException - if restaurant id is empty or invalid
   */
  public RestaurantEntity restaurantByUUID(final String restaurantUuid) throws RestaurantNotFoundException {

    if (commonValidation.isEmptyFieldValue(restaurantUuid)) {
      throw new RestaurantNotFoundException("RNF-002", "Restaurant id field should not be empty");
    }

    RestaurantEntity restaurantEntity = restaurantDao.restaurantByUUID(restaurantUuid);

    if (restaurantEntity == null) {
      throw new RestaurantNotFoundException("RNF-001", "No restaurant by this id");
    }
    return restaurantEntity;
  }

  /**
   * Update restaurant entity based on provided input
   *
   * @param restaurantEntity - object for which it needs to be updated
   * @param customerRating-  input rating provided by customer
   * @throws InvalidRatingException - if customer rating is invalid
   * @return- Updated restaurant object
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public RestaurantEntity updateRestaurantRating(final RestaurantEntity restaurantEntity, final Double customerRating)
          throws InvalidRatingException {
    if (customerRating < 1 || customerRating > 5) {
      throw new InvalidRatingException("IRE-001", "Restaurant should be in the range of 1 to 5");
    }

    Double currentRating = restaurantEntity.getCustomerRating();
    Integer numberCustomersRated = restaurantEntity.getNumberCustomersRated();

    Double newRating =
            ((currentRating * numberCustomersRated) + currentRating) / (++numberCustomersRated);

    restaurantEntity.setCustomerRating(newRating);
    restaurantEntity.setNumberCustomersRated(numberCustomersRated);

    return restaurantDao.updateRestaurantEntity(restaurantEntity);
  }
}
