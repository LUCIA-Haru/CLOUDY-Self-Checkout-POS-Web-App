package com.Cloudy.Cloudy_Self_Checkout_POS.restImpl;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Supplier;
import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.SupplierTransaction;
import com.Cloudy.Cloudy_Self_Checkout_POS.constants.ValueConstant;
import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.SupplierDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.SupplierTransactionDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.rest.SupplierRest;
import com.Cloudy.Cloudy_Self_Checkout_POS.service.SupplierService;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.CloudyUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.PaginatedResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
public class SupplierRestImpl implements SupplierRest {
    private final SupplierService supplierService;

    @Override
    public ResponseEntity<ApiResponseWrapper<Supplier>> addSupplier(UserDetails userDetails, SupplierDTO dto) {
        try{
            return supplierService.addSupplier(userDetails.getUsername(),dto);
        }catch (Exception e){
            e.printStackTrace();
        }
        return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG );
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<Supplier>> updateSupplier(UserDetails userDetails, SupplierDTO dto) {
        try{
            return supplierService.updateSupplier(userDetails.getUsername(),dto);
        }catch (Exception e){
            e.printStackTrace();
        }
        return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG );
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<String>> deleteSupplier(UserDetails userDetails, Long id) {
        try{
            return supplierService.deleteSupplier(userDetails.getUsername(),id);
        }catch (Exception e){
            e.printStackTrace();
        }
        return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG );
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<Supplier>> getSupplierById(UserDetails userDetails, Long id) {
        try{
            return supplierService.getSupplierById(userDetails.getUsername(),id);
        }catch (Exception e){
            e.printStackTrace();
        }
        return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG );
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<PaginatedResponse<Supplier>>> getAllSupplier(UserDetails userDetails, String filterValue, int page, int size) throws CustomSystemException {
        try{
            return supplierService.getAllSuppliers(userDetails.getUsername(),filterValue,page,size);
        }catch (Exception e){
            e.printStackTrace();
        }
        throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<List<SupplierTransactionDTO>>> getSupplierTransaction(UserDetails userDetails) throws CustomSystemException {
        try{
            return supplierService.getSupplierTransaction(userDetails.getUsername());
        }catch (Exception e){
            e.printStackTrace();
        }
        throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
    }
}
