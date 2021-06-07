package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.businness.ItemService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

@RestController
@CrossOrigin
public class RestaurantController {

  @Autowired
  private RestaurantService restaurantService;

  @Autowired
  private CategoryService categoryService;

  @Autowired
  private ItemService itemService;

  @Autowired
  private CustomerService customerService;

  /**
   * This is will fetch all the restaurants available in DB.
   * Request Method type -GET
   *
   * @return - list of restaurant
   */
  @RequestMapping(method = RequestMethod.GET, path = "/restaurant", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<RestaurantListResponse> getAllRestaurants() {

    final List<RestaurantEntity> restaurantEntities = restaurantService.restaurantsByRating();

    return new ResponseEntity<>(createRestaurantListResponse(restaurantEntities), HttpStatus.OK);
  }

  /**
   * This method will fetch restaurants based on restaurant name
   * Request Method type- GET
   *
   * @param restaurantName - input to fetch restaurant based on it
   * @return - list of restaurant
   * @throws RestaurantNotFoundException - if no restaurant found for input restaurant name
   */
  @RequestMapping(method = RequestMethod.GET, path = "/restaurant/name/{restaurant_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<RestaurantListResponse> restaurantsByName(@PathVariable("restaurant_name") final String restaurantName)
          throws RestaurantNotFoundException {
    final List<RestaurantEntity> restaurantEntities = restaurantService.restaurantsByName(restaurantName);

    return new ResponseEntity<>(createRestaurantListResponse(restaurantEntities), HttpStatus.OK);
  }

  /**
   * This method will fetch restaurants bassed on category id
   * Request Method type - GET
   *
   * @param categoryId - input to fetch restaurant based on it
   * @return - list of restaurant
   * @throws CategoryNotFoundException - if category id is null or invalid
   */
  @RequestMapping(method = RequestMethod.GET, path = "/restaurant/category/{category_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<RestaurantListResponse> restaurantByCategoryId(@PathVariable("category_id") final String categoryId)
          throws CategoryNotFoundException {
    final List<RestaurantEntity> restaurantEntities = restaurantService.restaurantByCategory(categoryId);

    return new ResponseEntity<>(createRestaurantListResponse(restaurantEntities), HttpStatus.OK);
  }


  /**
   * This method will fetch a restaurant object based on restaurant id
   * Request Method type- GET
   *
   * @param restaurantId- input to fetch restaurant based on it
   * @return - Restaurant details
   * @throws RestaurantNotFoundException - if restaurant id is invalid
   */

  @RequestMapping(method = RequestMethod.GET, path = "/restaurant/{restaurant_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<RestaurantDetailsResponse> restaurantByRestaurantId(@PathVariable("restaurant_id") final String restaurantId)
          throws RestaurantNotFoundException {
    final RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(restaurantId);

    final RestaurantDetailsResponse restaurantDetailsResponse = new RestaurantDetailsResponse();

    restaurantDetailsResponse.id(UUID.fromString(restaurantEntity.getUuid()));
    restaurantDetailsResponse.restaurantName(restaurantEntity.getRestaurantName());
    restaurantDetailsResponse.photoURL(restaurantEntity.getPhotoUrl());
    restaurantDetailsResponse.customerRating(BigDecimal.valueOf(restaurantEntity.getCustomerRating()));
    restaurantDetailsResponse.averagePrice(restaurantEntity.getAvgPrice());
    restaurantDetailsResponse.numberCustomersRated(restaurantEntity.getNumberCustomersRated());

    restaurantDetailsResponse.address(createRestaurantDetailsResponseAddress(restaurantEntity.getAddress()));

    final List<CategoryEntity> categoryEntities = categoryService.getCategoriesByRestaurant(restaurantId);
    if (!categoryEntities.isEmpty()) {
      final List<CategoryList> categoryLists = new ArrayList<>();
      categoryEntities.forEach(categoryEntity -> categoryLists.add(createCategoryList(restaurantId, categoryEntity)));
      restaurantDetailsResponse.categories(categoryLists);
    }

    return new ResponseEntity<>(restaurantDetailsResponse, HttpStatus.OK);

  }

  /**
   * This method will update restaurant details based on provided input values
   * Request Method type- PUT
   *
   * @param authorization  - Authorization String
   * @param restaurantId   - id of the restaurant
   * @param customerRating - customer rating for restaurant
   * @return - updated restaurant detail object
   * @throws AuthorizationFailedException - if user is not authorized
   * @throws RestaurantNotFoundException  - if restaurant id is invalid
   * @throws InvalidRatingException       - if customer rating is invalid
   */
  @RequestMapping(method = RequestMethod.PUT, path = ("/restaurant/{restaurant_id}"),
          produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
          consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<RestaurantUpdatedResponse> updateRestaurantDetails(
          @RequestHeader("authorization") final String authorization,
          @PathVariable("restaurant_id") final String restaurantId,
          @RequestParam("customer_rating") final Double customerRating)
          throws AuthorizationFailedException, RestaurantNotFoundException, InvalidRatingException {

    final String accessToken = authorization.split("Bearer ")[1];
    final CustomerEntity customerEntity = customerService.getCustomer(accessToken);
    RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(restaurantId);
    RestaurantEntity updatedRestaurantEntity = restaurantService.updateRestaurantRating(restaurantEntity, customerRating);
    RestaurantUpdatedResponse restaurantUpdatedResponse = new RestaurantUpdatedResponse();
    restaurantUpdatedResponse.id(UUID.fromString(restaurantId));
    restaurantUpdatedResponse.status("RESTAURANT RATING UPDATED SUCCESSFULLY");

    return new ResponseEntity<>(restaurantUpdatedResponse, HttpStatus.OK);
  }

  /**
   * Method to convert List of RestaurantEntity to RestaurantListResponse
   *
   * @param restaurantEntities
   */
  private RestaurantListResponse createRestaurantListResponse(final List<RestaurantEntity> restaurantEntities) {
    final RestaurantListResponse restaurantListResponse = new RestaurantListResponse();

    if (!restaurantEntities.isEmpty()) {
      final List<RestaurantList> restaurantLists = new ArrayList<>();
      for (RestaurantEntity restaurantEntity : restaurantEntities) {
        final RestaurantList restaurantList = createRestaurantList(restaurantEntity);
        restaurantList.address(createRestaurantDetailsResponseAddress(restaurantEntity.getAddress()));
        final List<CategoryEntity> categoryEntities = categoryService.getCategoriesByRestaurant(restaurantEntity.getUuid());
        restaurantList.categories(createCategoryName(categoryEntities));
        restaurantLists.add(restaurantList);
      }
      restaurantListResponse.restaurants(restaurantLists);
    }

    return restaurantListResponse;
  }

  /**
   * Method to set value of RestaurantList from RestaurantEntity
   *
   * @param restaurantEntity
   * @return - list of restaurant
   */
  private RestaurantList createRestaurantList(final RestaurantEntity restaurantEntity) {
    final RestaurantList restaurantList = new RestaurantList();
    restaurantList.id(UUID.fromString(restaurantEntity.getUuid()));
    restaurantList.restaurantName(restaurantEntity.getRestaurantName());
    restaurantList.photoURL(restaurantEntity.getPhotoUrl());
    restaurantList.customerRating(BigDecimal.valueOf(restaurantEntity.getCustomerRating()));
    restaurantList.averagePrice(restaurantEntity.getAvgPrice());
    restaurantList.numberCustomersRated(restaurantEntity.getNumberCustomersRated());
    return restaurantList;
  }

  /**
   * Method to set values from AddressEntity into RestaurantDetailsResponseAddress
   *
   * @return restaurant details object
   */
  private RestaurantDetailsResponseAddress createRestaurantDetailsResponseAddress(final AddressEntity addressEntity) {
    final RestaurantDetailsResponseAddress responseAddress = new RestaurantDetailsResponseAddress();
    responseAddress.id(UUID.fromString(addressEntity.getUuid()));
    responseAddress.flatBuildingName(addressEntity.getFlatBuilNo());
    responseAddress.locality(addressEntity.getLocality());
    responseAddress.city(addressEntity.getCity());
    responseAddress.pincode(addressEntity.getPincode());

    //Setting state of that address of type RestaurantDetailsResponseAddressState
    final RestaurantDetailsResponseAddressState responseAddressState = new RestaurantDetailsResponseAddressState();
    final StateEntity stateEntity = addressEntity.getState();
    responseAddressState.id(UUID.fromString(stateEntity.getUuid()));
    responseAddressState.stateName(stateEntity.getStateName());

    //setting state of type RestaurantDetailsResponseAddressState in address of type RestaurantDetailsResponseAddress
    responseAddress.state(responseAddressState);
    return responseAddress;
  }

  /**
   * Method to convert list of category into string
   *
   * @return Category list as String
   */
  private String createCategoryName(final List<CategoryEntity> categoryEntities) {
    StringJoiner categoryName = new StringJoiner(", ");
    for (CategoryEntity categoryEntity : categoryEntities) {
      categoryName.add(categoryEntity.getCategoryName());
    }
    return categoryName.toString();
  }

  /**
   * Method to set values  from CategoryEntity into CategoryList
   *
   * @return - Category List
   */
  private CategoryList createCategoryList(final String restaurantUuid, final CategoryEntity categoryEntity) {
    final CategoryList categoryList = new CategoryList();
    categoryList.id(UUID.fromString(categoryEntity.getUuid()));
    categoryList.categoryName(categoryEntity.getCategoryName());

    final List<ItemEntity> itemEntities =
            itemService.getItemsByCategoryAndRestaurant(restaurantUuid, categoryEntity.getUuid());
    final List<ItemList> itemLists = new ArrayList<>();
    for (ItemEntity itemEntity : itemEntities) {
      itemLists.add(createItemList(itemEntity));
    }
    categoryList.itemList(itemLists);
    return categoryList;
  }

  /**
   * Method to set values from ItemEntity into ItemList
   *
   * @return - Item List
   */
  private ItemList createItemList(final ItemEntity itemEntity) {
    final ItemList itemList = new ItemList();
    itemList.id(UUID.fromString(itemEntity.getUuid()));
    itemList.itemName(itemEntity.getItemName());
    itemList.price(itemEntity.getPrice());
    final String itemType = itemEntity.getType().equals("0") ? "VEG" : "NON_VEG";
    itemList.itemType(ItemList.ItemTypeEnum.fromValue(itemType));
    return itemList;
  }

}

