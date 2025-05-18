package com.Cloudy.Cloudy_Self_Checkout_POS.serviceImpl;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Coupon;
import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Customer;
import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Staff;
import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.User;
import com.Cloudy.Cloudy_Self_Checkout_POS.constants.ValueConstant;
import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.dao.CouponDao;
import com.Cloudy.Cloudy_Self_Checkout_POS.dao.CustomerDao;
import com.Cloudy.Cloudy_Self_Checkout_POS.dao.StaffDao;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.CouponDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.service.CouponService;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.CloudyUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.UserUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private static final String CouponServiceImpl = "CouponServiceImpl";

    private final CouponDao  couponDao;
    private final UserUtils userUtils;
    private final CustomerDao customerDao;
    private final StaffDao staffDao;

    @Override
    public ResponseEntity<ApiResponseWrapper<CouponDTO>> generateCoupon(String username, CouponDTO couponDTO) {
        log.info("{} => {} => Subject : generateCoupon ||| username::: {}",
                CouponServiceImpl, "generateCoupon()", username);
        try{
            String couponCode = generateUniqueCouponCode();
            System.out.println("couponCode" + couponCode);
            String guid = UUID.randomUUID().toString();

            Coupon coupon = Coupon.builder()
                    .guid(guid)
                    .couponCode(couponCode)
                    .isActive(true)
                    .discountAmount(couponDTO.getDiscountAmount())
                    .expirationDate(couponDTO.getExpirationDate())
                    .minPurchaseAmount(couponDTO.getMinPurchaseAmount())
                    .createdBy(username)
                    .createdOn(LocalDateTime.now())
                    .build();
            Coupon save = couponDao.save(coupon);

            log.info("✅ {} => {} => Subject : generate coupon::: {} successfully",
                    CouponServiceImpl, "generateCoupon()", save.getCouponId());

            CouponDTO dto = mapDTO(coupon);

            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.ADD,dto);

        }catch (Exception e) {
            // Log the specific error and return the corresponding response
            log.error("{} => {} => Subject : generating coupon  => Error ::: {}",
                    CouponServiceImpl, "generateCoupon()", e.getMessage());
            throw (e);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<BigDecimal>> useCoupon(String username, Map<String, String> request) throws CustomSystemException {
        log.info("{} => {} => Subject : useCoupon ||| username::: {}",
                CouponServiceImpl, "useCoupon()", username);
        try{
            String couponCode = request.get("couponCode");
            BigDecimal totalAmount = new BigDecimal(request.get("totalAmount"));
            BigDecimal discount = applyCoupon(couponCode, totalAmount);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, "Coupon applied", discount);
        }catch (Exception e){
            log.error("{} => {} => Subject : Applying coupon  => Error ::: {}",
                    CouponServiceImpl, "useCoupon()", e.getMessage());
            throw (e);
        }
    }

    @Transactional
    @Override
    public ResponseEntity<ApiResponseWrapper<String>> assignCoupon(String username, Long cusId, String couponCode) throws CustomSystemException {
        log.info("{} => {} => Subject : assign Coupon to cusId {} ||| username::: {}",
                CouponServiceImpl, "assignCoupon()",cusId, username);
        try{
            User user = userUtils.getUserByUsernameOptional(username);
            Staff admin = staffDao.findByUserId(user.getUserId());

            if (admin == null && !"ADMIN".equals(admin.getRole().toUpperCase()))
                throw new CustomSystemException(HttpStatus.BAD_REQUEST,ValueConstant.BAD_CREDENTIALS);

            Customer customer = customerDao.findByUserId(cusId);

            Coupon coupon = couponDao.findByCouponCode(couponCode)
                    .orElseThrow(() -> new CustomSystemException(HttpStatus.NOT_FOUND, "Coupon not found with code: " + couponCode));

            if (!coupon.isActive()) {
                throw new CustomSystemException(HttpStatus.BAD_REQUEST, "Coupon is not active.");
            }

//            due to Many-to-Many
            customer.getCoupons().add(coupon);
            coupon.getCustomers().add(customer);

            customerDao.save(customer);
            couponDao.save(coupon);
            log.info("✅ {} => {} => Subject : assign Coupon to cusId {} ||| username::: {} successfully",
                    CouponServiceImpl, "assignCoupon()",cusId, username);

            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, "Coupon assigned");
        }catch (Exception e){
            log.error("{} => {} => Subject : Applying coupon  => Error ::: {}",
                    CouponServiceImpl, "useCoupon()", e.getMessage());
            throw (e);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<List<CouponDTO>>> fetchCoupons(String username) throws CustomSystemException {
        log.info("{} => {} => Subject : fetch Coupon ||| username::: {}",
                CouponServiceImpl, "fetchCoupons", username);
        try{
            User user = userUtils.getUserByUsernameOptional(username);
            Customer customer = customerDao.findByUserId(user.getUserId());

            List<Coupon> assignedCoupons = couponDao.findByCustomers(customer);

            List<CouponDTO> response = assignedCoupons.stream()
                    .map(coupon -> CouponDTO.builder()
                            .couponId(coupon.getCouponId())
                            .couponCode(coupon.getCouponCode())
                            .discountAmount(coupon.getDiscountAmount())
                            .minPurchaseAmount(coupon.getMinPurchaseAmount())
                            .expirationDate(coupon.getExpirationDate())
                            .isActive(coupon.isActive())
                            .build())
                    .collect(Collectors.toList());
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, "Assigned coupons retrieved successfully", response);


        }catch (Exception e){
            log.error("{} => {} => Subject :fetching coupon  => Error ::: {}",
                    CouponServiceImpl, "fetchCoupons)", e.getMessage());
            throw (e);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<Map<String, Object>>> getAllCouponsAssignments(String username) throws CustomSystemException {
        log.info("{} => {} => Subject : get All Coupons Assignments ||| username::: {}",
                CouponServiceImpl, "getAllCouponsAssignments()", username);
        try{
            User user = userUtils.getUserByUsernameOptional(username);
            Staff admin = staffDao.findByUserId(user.getUserId());

            if (admin == null || !"ADMIN".equals(admin.getRole().toUpperCase()))
                throw new CustomSystemException(HttpStatus.BAD_REQUEST,ValueConstant.BAD_CREDENTIALS);

            // Fetch all coupons with assignment details
            List<Map<String, Object>> rawResults = couponDao.findAllCouponsWithAssignmentDetails();
            List<CouponDTO> couponAssignments = new ArrayList<>();

            for (Map<String, Object> row : rawResults) {
                // Convert expirationDate from String to LocalDate
                String dateStr = (String) row.get("expirationDate");
                LocalDate expirationDate = LocalDate.parse(dateStr); // Correct way to parse

                CouponDTO dto = new CouponDTO(
                        ((Number) row.get("couponId")).longValue(),  // Convert numeric ID
                        (String) row.get("couponCode"),
                        (BigDecimal) row.get("discountAmount"),
                        (BigDecimal) row.get("minPurchaseAmount"),
                        expirationDate, // Now correctly parsed from String
                        (Boolean) row.get("isActive"),
                        (String) row.get("assignedTo") // Directly map usernames
                );
                couponAssignments.add(dto);
            }

            // Fetch unassigned coupons
            List<CouponDTO> unassignedCoupons = couponDao.findUnassignedCoupons();

            Map<String, Object> response = new HashMap<>();
            response.put("assignedCoupons", couponAssignments);
            response.put("unassignedCoupons", unassignedCoupons);

            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, "Assigned coupons retrieved successfully", response);


        }catch (Exception e){
            log.error("{} => {} => Subject :get All Coupons Assignments  => Error ::: {}",
                    CouponServiceImpl, "getAllCouponsAssignments()", e.getMessage());
            throw (e);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<CouponDTO>> updateCoupon(String username, CouponDTO couponDTO, Long couponId) throws CustomSystemException {
        log.info("{} => {} => Subject: Updating coupon with ID: {}",
                CouponServiceImpl.class.getSimpleName(), "updateCoupon()", couponId);
        try{
             userUtils.getUserByUsernameOptional(username);

            // Fetch the existing coupon
            Coupon coupon = couponDao.findById(couponId)
                    .orElseThrow(() -> new CustomSystemException(HttpStatus.BAD_REQUEST, "Coupon not found with ID: " + couponId));

            // Update the coupon fields
            coupon.setDiscountAmount(couponDTO.getDiscountAmount());
            coupon.setMinPurchaseAmount(couponDTO.getMinPurchaseAmount());
            coupon.setExpirationDate(couponDTO.getExpirationDate());
            coupon.setActive(couponDTO.isActive());
            coupon.setUpdatedBy(username);
            coupon.setUpdatedOn(LocalDateTime.now());

            // Save the updated coupon
            Coupon updatedCoupon = couponDao.save(coupon);

            log.info("✅ {} => {} => Successfully updated coupon with ID: {}",
                    CouponServiceImpl.class.getSimpleName(), "updateCoupon()", couponId);

            // Map the updated entity to DTO
            CouponDTO dto =  mapDTO(updatedCoupon);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, "Assigned coupons retrieved successfully", dto);

        }
        catch (Exception e){
            log.error("{} => {} => Subject :get All Coupons Assignments  => Error ::: {}",
                    CouponServiceImpl, "getAllCouponsAssignments()", e.getMessage());
            throw (e);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<String>> deleteCoupon(String username, Long couponId) throws CustomSystemException {
        log.info("{} => {} => Subject: Deleting coupon with ID: {}",
                CouponServiceImpl.class.getSimpleName(), "deleteCoupon()", couponId);
        try{
            userUtils.getUserByUsernameOptional(username);
            Coupon coupon = couponDao.findById(couponId)
                    .orElseThrow(() -> new CustomSystemException(HttpStatus.BAD_REQUEST, "Coupon not found with ID: " + couponId));

            // Delete the coupon
            couponDao.delete(coupon);

            log.info("✅ {} => {} => Successfully deleted coupon with ID: {}",
                    CouponServiceImpl.class.getSimpleName(), "deleteCoupon()", couponId);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, "Delete Coupon successfully");

        }
        catch (Exception e){
            log.error("{} => {} => Subject :get All Coupons Assignments  => Error ::: {}",
                    CouponServiceImpl, "getAllCouponsAssignments()", e.getMessage());
            throw (e);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<List<CouponDTO>>> getAllCoupons(String username) throws CustomSystemException {
        log.info("{} => {} => Subject : get All Coupons ||| username::: {}", CouponServiceImpl, "getAllCoupons()", username);
        try {
            User user = userUtils.getUserByUsernameOptional(username);
            Staff admin = staffDao.findByUserId(user.getUserId());

//            if (admin == null || !"ADMIN".equals(admin.getRole().toUpperCase()) || !"MANAGER".equals(admin.getRole().toUpperCase()) )
//                throw new CustomSystemException(HttpStatus.BAD_REQUEST, ValueConstant.BAD_CREDENTIALS);

            List<Coupon> allCoupons = couponDao.findAll();

            List<CouponDTO> response = allCoupons.stream()
                    .map(coupon -> CouponDTO.builder()
                            .couponId(coupon.getCouponId())
                            .couponCode(coupon.getCouponCode())
                            .discountAmount(coupon.getDiscountAmount())
                            .minPurchaseAmount(coupon.getMinPurchaseAmount())
                            .expirationDate(coupon.getExpirationDate())
                            .isActive(coupon.isActive())
                            .guid(coupon.getGuid())
                            .CreatedBy(coupon.getCreatedBy())
                            .CreatedOn(coupon.getCreatedOn())
                            .UpdatedBy(coupon.getUpdatedBy())
                            .UpdatedOn(coupon.getUpdatedOn())
                            .build())
                    .collect(Collectors.toList());

            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, "All coupons retrieved successfully", response);
        } catch (Exception e) {
            log.error("{} => {} => Subject :get All Coupons  => Error ::: {}", CouponServiceImpl, "getAllCoupons()", e.getMessage());
            throw (e);
        }
    }



    //    ////////////////////////////////////////////////////
    // Generate a unique Coupon Code
    private String generateUniqueCouponCode(){
        String prefix = "CLOUDY-";
        return prefix + UUID.randomUUID().toString().substring(0, 8).toUpperCase(); // 8 chars for ~2.8T combinations
    }
//    maptodto
    private CouponDTO mapDTO (Coupon coupon){
        CouponDTO dto = new CouponDTO();
        dto.setCouponId(coupon.getCouponId());
        dto.setGuid(coupon.getGuid());
        dto.setActive(coupon.isActive());
        dto.setExpirationDate(coupon.getExpirationDate());
        dto.setDiscountAmount(coupon.getDiscountAmount());
        dto.setMinPurchaseAmount(coupon.getMinPurchaseAmount());
        dto.setCouponCode(coupon.getCouponCode());
        dto.setCreatedBy(coupon.getCreatedBy());
        dto.setUpdatedBy(coupon.getUpdatedBy());
        dto.setCreatedOn(coupon.getCreatedOn());
        dto.setUpdatedOn(coupon.getUpdatedOn());
        dto.setAssignedTo(null);
        return dto;
    }

//    apply coupon
    public BigDecimal applyCoupon (String couponCode, BigDecimal totalAmount) throws CustomSystemException {
        log.info("{} => {} => Subject : applyCoupon ||| couponCode::: {} ||| totalAmount::: {}",
                CouponServiceImpl, "applyCoupon()", couponCode, totalAmount);
        try{
            Coupon coupon = couponDao.findByCouponCode(couponCode)
                    .orElseThrow(() -> new CustomSystemException(HttpStatus.BAD_REQUEST,"Coupon Code is not found"));

            if (!coupon.isActive()) throw new CustomSystemException(HttpStatus.BAD_REQUEST,"Coupon is not active");

            if (coupon.getExpirationDate() != null && coupon.getExpirationDate().isBefore(LocalDate.now()))
                throw new CustomSystemException(HttpStatus.BAD_REQUEST,"Coupon has expired");

//            < 0 means totalAmount is smaller than the required minimum purchase amount.
            if (totalAmount.compareTo(coupon.getMinPurchaseAmount()) < 0)
                throw new CustomSystemException(HttpStatus.BAD_REQUEST,"Total amount does not meet minimum purchase requirement");
            log.info("✅ {} => {} => Subject : apply coupon::: discountAmount {} successfully",
                    CouponServiceImpl, "applyCoupon()", coupon.getDiscountAmount());
            return coupon.getDiscountAmount();

        }catch (Exception e){
            log.error("{} => {} => Subject : apply coupon  => Error ::: {}",
                    CouponServiceImpl, "applyCoupon()", e.getMessage());
            throw (e);
        }
    }


}
