package com.Cloudy.Cloudy_Self_Checkout_POS.restImpl;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Brand;
import com.Cloudy.Cloudy_Self_Checkout_POS.constants.ValueConstant;
import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.BrandDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.rest.BrandRest;
import com.Cloudy.Cloudy_Self_Checkout_POS.service.BrandService;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.CloudyUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class BrandRestImpl implements BrandRest {
    private final BrandService brandService;


    @Override
    public ResponseEntity<ApiResponseWrapper<BrandDTO>> addBrand(UserDetails userDetails, BrandDTO brandDTO) {
        try{
            return  brandService.addBrand(userDetails.getUsername(),brandDTO);
        }catch (Exception e){
            e.printStackTrace();
        }
        return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG );
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<String>> deleteBrand(UserDetails userDetails, Long id) {
        try{
            return  brandService.deleteBrand(userDetails.getUsername(),id);
        }catch (Exception e){
            e.printStackTrace();
        }
        return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG );
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<Brand>> getBrandByID(UserDetails userDetails, Long id) {
        try{
            return  brandService.getBrandById(userDetails.getUsername(),id);
        }catch (Exception e){
            e.printStackTrace();
        }
        return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG );
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<PaginatedResponse<Brand>>> getAllBrand(String filterValue, int min, int max) throws CustomSystemException {
        try{
            return  brandService.getAllBrand(filterValue, min,max);
        }catch (Exception e){
            e.printStackTrace();
        }
        throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<Brand>> updateBrand(UserDetails userDetails, BrandDTO brandDTO) {
        try{
            return  brandService.updateBrand(userDetails.getUsername(),brandDTO);
        }catch (Exception e){
            e.printStackTrace();
        }
        return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG );
    }

}
