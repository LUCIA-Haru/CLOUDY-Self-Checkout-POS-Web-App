package com.Cloudy.Cloudy_Self_Checkout_POS.restImpl;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Category;
import com.Cloudy.Cloudy_Self_Checkout_POS.constants.ValueConstant;
import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.CategoryRequestBody;
import com.Cloudy.Cloudy_Self_Checkout_POS.rest.CategoryRest;
import com.Cloudy.Cloudy_Self_Checkout_POS.service.CategoryService;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.CloudyUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.PaginatedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryRestImpl implements CategoryRest {

    @Autowired
    CategoryService categoryService;

    @Override
    public ResponseEntity<ApiResponseWrapper<CategoryRequestBody>> addCategory(UserDetails userDetails, CategoryRequestBody categoryRequestBody) {
        try{
            return  categoryService.addCategory(userDetails,categoryRequestBody);
        }catch (Exception e){
            e.printStackTrace();
        }
        return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG );
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<PaginatedResponse<Category>>> getAllCategory(String filterValue, int max, int min) throws CustomSystemException {
        try{
            return  categoryService.getAllCategory(filterValue, max, min);
        }catch (Exception e){
            e.printStackTrace();
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }

    }

    @Override
    public ResponseEntity<ApiResponseWrapper<Category>> getCategoryById(Long id) throws CustomSystemException {
        try {
            return categoryService.getCategoryById(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<Category>> updateCategoryByID(UserDetails userDetails, Long id, CategoryRequestBody categoryRequestBody) {
        try {
            return categoryService.updateCategoryByID(userDetails,id,categoryRequestBody);
        } catch (Exception e) {
            e.printStackTrace();
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR,ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<String>> deleteCategory(UserDetails userDetails, Long id) {
        try {
            return categoryService.deleteCategory(userDetails.getUsername(),id);
        } catch (Exception e) {
            e.printStackTrace();
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR,ValueConstant.SOMETHING_WENT_WRONG);
        }
    }
}
