package com.Cloudy.Cloudy_Self_Checkout_POS.serviceImpl;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Brand;
import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Category;
import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Staff;
import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.User;
import com.Cloudy.Cloudy_Self_Checkout_POS.constants.ValueConstant;
import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.dao.CategoryDao;
import com.Cloudy.Cloudy_Self_Checkout_POS.dao.StaffDao;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.CategoryRequestBody;
import com.Cloudy.Cloudy_Self_Checkout_POS.service.CategoryService;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.CloudyUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.UserUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {


  private final  CategoryDao categoryDao;
    private final StaffDao staffDao;

    @Autowired
    UserUtils userUtils;


    @Override
    public ResponseEntity<ApiResponseWrapper<CategoryRequestBody>> addCategory(UserDetails userDetails, CategoryRequestBody requestBody) {
        try{

            Category category = Category.builder()
                    .guid(UUID.randomUUID().toString())
                    .categoryName(requestBody.getCategoryName())
                    .categoryDesc(requestBody.getCategoryDesc())
                    .aisle(requestBody.getAisle())
                    .createdOn(LocalDate.now())
                    .createdBy(userDetails.getUsername())
                    .build();
            Category savedCategory = categoryDao.save(category);

            CategoryRequestBody responseBody = CategoryRequestBody.builder()
                    .categoryId(savedCategory.getCategoryId())
                    .categoryName(savedCategory.getCategoryName())
                    .categoryDesc(savedCategory.getCategoryDesc())
                    .aisle(savedCategory.getAisle())
                    .createdBy(savedCategory.getCreatedBy())
                    .createdOn(savedCategory.getCreatedOn())
                    .build();

            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.ADD, responseBody);

        } catch (Exception e) {
            e.printStackTrace();

        }
        return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<PaginatedResponse<Category>>> getAllCategory(String filterValue, int page, int size) throws CustomSystemException {
        try {
            Page<Category> categories = null;
            Pageable pageable= PageRequest.of(page,size);

            if ("true".equalsIgnoreCase(filterValue)) {
//                categories = categoryService.getActiveCategories(); // Assuming there's a method for this
            } else {
                categories  = categoryDao.findAll(pageable);
            }
//            categories = categoryDao.getAllCategory();

            if (categories == null || categories.isEmpty()) {
                return CloudyUtils.getResponseEntityCustom(HttpStatus.BAD_REQUEST, "No categories found");
            }

            // Wrap the Page<T> into PaginatedResponse
            PaginatedResponse<Category> paginatedResponse = new PaginatedResponse<>(categories);

            return CloudyUtils.getPaginatedResponseEntityCustom(HttpStatus.OK,ValueConstant.RETRIEVE, paginatedResponse);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<Category>> getCategoryById(Long id) throws CustomSystemException {
        try{
            Category categoryDetails = categoryDao.findById(id)
                    .orElseThrow(() -> new CustomSystemException(HttpStatus.BAD_REQUEST,ValueConstant.ID_NOT_FOUND));

            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK,ValueConstant.RETRIEVE,categoryDetails);

        } catch (CustomSystemException e) {
            // Custom exception is already handled, rethrow it
            throw e;
        } catch (Exception e) {
            e.printStackTrace(); // Consider using a logger instead of printStackTrace
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<Category>> updateCategoryByID(UserDetails userDetails, Long id, CategoryRequestBody requestBody) throws CustomSystemException {
        try{
            Staff staff;
            Category category;
            User user = userUtils.getUserByUsernameOptional(userDetails.getUsername());
            staff = staffDao.findByUserId(user.getUserId());

            if (staff == null){
                log.warn("Staff not found for userId: {}", user.getUserId());
                return CloudyUtils.getResponseEntityCustom(HttpStatus.BAD_REQUEST,ValueConstant.STAFF_NOT_FOUND);
            }
            Category existCategory = categoryDao.findById(id)
                    .orElseThrow(()-> new CustomSystemException(HttpStatus.BAD_REQUEST,"Category ID is not Found"));
               Category updateCategory = updateCategoryFields(existCategory,requestBody,staff.getUsername() );
                Category saveCategory = categoryDao.save(updateCategory);
                return  CloudyUtils.getResponseEntityCustom(HttpStatus.OK,ValueConstant.UPDATE,saveCategory);

        } catch (CustomSystemException e) {
            // Custom exception is already handled, rethrow it
            throw e;
        } catch (Exception e) {
            e.printStackTrace(); // Consider using a logger instead of printStackTrace

        }
        return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<String>> deleteCategory(String username, Long id) {
        log.info("{} => deleteCategory() => Subject: delete category id {} ||| username: {}", "CategoryServiceImpl", id,username);
        try{
            Category category = categoryDao.findById(id).orElseThrow(() -> new CustomSystemException(HttpStatus.BAD_REQUEST,ValueConstant.ID_NOT_FOUND));
            categoryDao.deleteById(category.getCategoryId());
            log.info("✅ {} => deleteBrand() => Subject: deleting brand id {} || username: {}",
                    "CategoryServiceImpl",id, username);

            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.DELETE, ValueConstant.ACTION_SUCCESS);

        }catch (Exception e){
            log.error("{} => deleteBrand() => Subject: adding brand => Unexpected Error: {}",  "CategoryServiceImpl", e.getMessage());

        }
        return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
    }

    private Category updateCategoryFields(Category existCategory,CategoryRequestBody requestBody,String username){
        return Category.builder()
                .categoryId(existCategory.getCategoryId())
                .categoryName(requestBody.getCategoryName() != null && !requestBody.getCategoryName().isEmpty()
                ? requestBody.getCategoryName() : existCategory.getCategoryName())
                .categoryDesc(requestBody.getCategoryDesc() != null && !requestBody.getCategoryDesc().isEmpty()
                ? requestBody.getCategoryDesc() : existCategory.getCategoryDesc())
                .aisle(requestBody.getAisle() != null
                ? requestBody.getAisle() : existCategory.getAisle())
                .createdBy(existCategory.getCreatedBy())
                .createdOn(existCategory.getCreatedOn())
                .updatedBy(username)
                .updatedOn(LocalDate.now())
                .build();
    }



}
