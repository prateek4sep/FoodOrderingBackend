package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.ItemDao;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.ItemNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private CategoryDao categoryDao;

    /**
     * Fetch item details based on item uuid
     *
     */
    public ItemEntity getItemById(final String itemUuid) throws ItemNotFoundException {
        final ItemEntity itemEntity = itemDao.getItemById(itemUuid);

        // Throw exception if no item exists in the database for the given item UUID
        if (itemEntity == null) {
            throw new ItemNotFoundException("INF-003", "No item by this id exist");
        }
        return itemEntity;
    }

    /**
     * Fetch list of item details restaurantUuid and categoryUuid
     *
     */
    public List<ItemEntity> getItemsByCategoryAndRestaurant(final String restaurantUuid, final String categoryUuid) {
        return itemDao.getItemsByCategoryAndRestaurant(restaurantUuid, categoryUuid);
    }

    /**
     * Fetch top five items of that restaurant based on the number of times that item was ordered
     *
     */
    public List<ItemEntity> getItemsByPopularity(final RestaurantEntity restaurantEntity) {
        return itemDao.getItemsByPopularity(restaurantEntity.getId());
    }
}
