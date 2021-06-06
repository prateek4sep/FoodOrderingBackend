package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.common.CommonValidation;
import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private CommonValidation commonValidation;

    /**
     * Get  all categories
     *
     */
    public List<CategoryEntity> getAllCategoriesOrderedByName() {
        return categoryDao.getAllCategoriesOrderedByName();
    }

    /**
     * fetch  category details based on category uuid
     *
     */
    public CategoryEntity getCategoryById(final String categoryUuid) throws CategoryNotFoundException {
        if (commonValidation.isEmptyFieldValue(categoryUuid)) {
            throw new CategoryNotFoundException("CNF-001", "Category id field should not be empty");
        }
        final CategoryEntity categoryEntity = categoryDao.getCategoryById(categoryUuid);

        if (categoryEntity == null) {
            throw new CategoryNotFoundException("CNF-002", "No category by this id");
        }
        return categoryEntity;

    }

    /**
     * fetch  List of Category Entities using restaurant uuid
     */

    public List<CategoryEntity> getCategoriesByRestaurant(final String restaurantUuid) {
        final List<RestaurantCategoryEntity> categoriesByRestaurant = categoryDao.getCategoriesByRestaurant(restaurantUuid);

        final List<CategoryEntity> categoryEntities = new ArrayList<>();
        if (!categoriesByRestaurant.isEmpty()) {
            categoriesByRestaurant.forEach(restaurantCategoryEntity -> categoryEntities.add(restaurantCategoryEntity.getCategory()));
        }
        return categoryEntities;
    }
}
