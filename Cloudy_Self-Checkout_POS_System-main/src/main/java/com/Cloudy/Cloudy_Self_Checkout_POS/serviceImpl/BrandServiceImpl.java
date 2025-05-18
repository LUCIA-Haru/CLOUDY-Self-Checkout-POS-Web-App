package com.Cloudy.Cloudy_Self_Checkout_POS.serviceImpl;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Brand;
import com.Cloudy.Cloudy_Self_Checkout_POS.constants.ValueConstant;
import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.dao.BrandDAO;
import com.Cloudy.Cloudy_Self_Checkout_POS.dao.StaffDao;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.BrandDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.service.BrandService;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.CloudyUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.UserUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.PaginatedResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class BrandServiceImpl implements BrandService {

    @Autowired
    UserUtils userUtils;

    private final BrandDAO brandDAO;
    private final StaffDao staffDao;

    private final String BrandServiceImpl = "BrandServiceImpl";

    @Override
    public ResponseEntity<ApiResponseWrapper<BrandDTO>> addBrand(String username, BrandDTO request) {
        log.info("{} => addBrand() => Subject: adding brand ||| username: {}", BrandServiceImpl, username);
        try{
            Brand brand = Brand.builder()
                    .name(request.getName())
                    .guid(UUID.randomUUID().toString())
                    .createdOn(LocalDate.now())
                    .createdBy(username)
                    .isActive(request.getIsActive() == true ? request.getIsActive() : false)
                    .build();
            Brand savedBrand = brandDAO.save(brand);
            log.info("✅ {} => addBrand() => Subject: adding brand || username: {}",
                    BrandServiceImpl, username);
            BrandDTO dto = BrandDTO.builder()
                    .name(savedBrand.getName())
                    .id(savedBrand.getId())
                    .guid(savedBrand.getGuid())
                    .createdBy(savedBrand.getCreatedBy())
                    .createdOn(savedBrand.getCreatedOn())
                    .isActive(savedBrand.getIsActive())
                    .build();


            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.ADD, dto);


        }catch (Exception e){
            log.error("{} => addBrand() => Subject: adding brand => Unexpected Error: {}",  BrandServiceImpl, e.getMessage());

        }
        return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG, request);

    }

    @Override
    public ResponseEntity<ApiResponseWrapper<Brand>> updateBrand(String username, BrandDTO brandDTO) {
        log.info("{} => updateBrand() => Subject: updating brand ||| username: {}, brandId: {}", BrandServiceImpl, username, brandDTO.getId());

        try {
            // Validate user
            userUtils.getUserByUserName(username);

            // Fetch the existing brand by ID
            Brand existingBrand = brandDAO.findById(brandDTO.getId())
                    .orElseThrow(() -> new CustomSystemException(HttpStatus.BAD_REQUEST,ValueConstant.ID_NOT_FOUND));

            // Update the brand fields
            existingBrand.setName( brandDTO.getName() != null ?  brandDTO.getName() : existingBrand.getName());
            existingBrand.setIsActive( brandDTO.getIsActive() != null ?  brandDTO.getIsActive() : existingBrand.getIsActive());
            existingBrand.setUpdatedOn(LocalDate.now());
            existingBrand.setUpdatedBy(username);

            // Save the updated brand
            Brand updatedBrand = brandDAO.save(existingBrand);

            log.info("✅ {} => updateBrand() => Subject: updating brand || username: {}, brandId: {}",
                    BrandServiceImpl, username, brandDTO.getId());

            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.UPDATE, updatedBrand);

        } catch (Exception e) {
            log.error("{} => updateBrand() => Subject: updating brand => Unexpected Error: ", BrandServiceImpl, e);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<String>> deleteBrand(String username, Long id) {
        log.info("{} => deleteBrand() => Subject: delete brand id {} ||| username: {}", BrandServiceImpl, id,username);
        try{
            Brand brand = brandDAO.findById(id).orElseThrow(() -> new CustomSystemException(HttpStatus.BAD_REQUEST,ValueConstant.ID_NOT_FOUND));
            brandDAO.deleteById(brand.getId());
            log.info("✅ {} => deleteBrand() => Subject: deleting brand || username: {}",
                    BrandServiceImpl, username);

            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.DELETE, ValueConstant.ACTION_SUCCESS);

        }catch (Exception e){
            log.error("{} => deleteBrand() => Subject: adding brand => Unexpected Error: {}",  BrandServiceImpl, e.getMessage());

        }
        return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<Brand>> getBrandById(String username, Long id) {
        log.info("{} => getBrandById() => Subject: get brand id ::: {}||| username: {}", BrandServiceImpl, id,username);
        try{
            userUtils.getUserByUserName(username);
            Brand brand = brandDAO.findById(id).orElseThrow(() -> new CustomSystemException(HttpStatus.BAD_REQUEST,ValueConstant.ID_NOT_FOUND));

            log.info("✅ {} =>getBrandById() => Subject: getting brand || username: {}",
                    BrandServiceImpl, username);

            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.ACTION_SUCCESS, brand);

        }catch (Exception e){
            log.error("{} => getBrandById() => Subject: getting brand => Unexpected Error: {}",  BrandServiceImpl, e.getMessage());

        }
        return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<PaginatedResponse<Brand>>> getAllBrand(String filterValue, int min, int max) {
        log.info("{} => getAllBrand() => Subject: get all brand", BrandServiceImpl);
        try {


            // Validate pagination parameters
                Page<Brand> brands = null;
                Pageable pageable = PageRequest.of(min,max);

                // Fetch brands based on filterValue
                if ("false".equalsIgnoreCase(filterValue)) {
                    brands = brandDAO.findByInactive(pageable);
                } else {
                    brands = brandDAO.findAll(pageable);
                }
                PaginatedResponse<Brand> paginatedResponse = new PaginatedResponse<>(brands);

                log.info("✅ {} =>getAllBrand() => Subject: get all brand",
                        BrandServiceImpl);

                return CloudyUtils.getPaginatedResponseEntityCustom(HttpStatus.OK, ValueConstant.RETRIEVE, paginatedResponse);
            }catch(Exception e){
                log.error("{} => getAllBrand() => Subject: get all brand => Unexpected Error: {}", BrandServiceImpl, e.getMessage());
                throw e;
            }
    }
}
