package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.CategoriesListResponse;
import com.upgrad.FoodOrderingApp.api.model.CategoryDetailsResponse;
import com.upgrad.FoodOrderingApp.api.model.CategoryListResponse;
import com.upgrad.FoodOrderingApp.api.model.ItemList;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * This api is called to retieve Category list which will be ordered by Name
     *
     * @return - ResponseEntity
     */
    @RequestMapping(method = RequestMethod.GET, path = "/category",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoriesListResponse> getAllCategories() {
        final List<CategoryEntity> categoriesList = categoryService.getAllCategoriesOrderedByName();

        final CategoriesListResponse categoriesListResponse = new CategoriesListResponse();
        if (!categoriesList.isEmpty()) {
            final List<CategoryListResponse> categoryLists = new ArrayList<>();
            categoriesList.forEach(
                    category -> categoryLists.add(createCategoryList(category))
            );
            categoriesListResponse.categories(categoryLists);
        }

        return new ResponseEntity<>(categoriesListResponse, HttpStatus.OK);

    }

    /**
     * This method is called to retieve Category Details based on category id
     *
     * @param categoryId - This represents category id
     * @return - ResponseEntity of CategoryDetails
     */
    @RequestMapping(method = RequestMethod.GET, path = "/category/{category_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoryDetailsResponse> getCategoryById(
            @PathVariable("category_id") final String categoryId) throws CategoryNotFoundException {
        final CategoryEntity categoryEntity = categoryService.getCategoryById(categoryId);
        final List<ItemEntity> itemEntities = categoryEntity.getItems();

        final CategoryDetailsResponse categoryDetailsResponse = new CategoryDetailsResponse()
                .id(UUID.fromString(categoryEntity.getUuid()))
                .categoryName(categoryEntity.getCategoryName());

        if (!itemEntities.isEmpty()) {
            final List<ItemList> itemLists = new ArrayList<>();
            itemEntities.forEach(itemEntity -> itemLists.add(createItemList(itemEntity)));
            categoryDetailsResponse.itemList(itemLists);
        }

        return new ResponseEntity<>(categoryDetailsResponse, HttpStatus.OK);
    }

    /**
     * Method to set itemEntity and return ItemList
     *
     * @param itemEntity
     * @return - ItemList
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

    /**
     * Method to set all  field of categoryEntity into CategoryListResponse
     *
     * @param categoryEntity
     * @return - CategoryListResponse
     */
    private CategoryListResponse createCategoryList(final CategoryEntity categoryEntity) {
        final CategoryListResponse categoryListResponse = new CategoryListResponse();
        categoryListResponse.categoryName(categoryEntity.getCategoryName());
        categoryListResponse.id(UUID.fromString(categoryEntity.getUuid()));
        return categoryListResponse;
    }
}
