package com.Cloudy.Cloudy_Self_Checkout_POS.dao;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Coupon;
import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Customer;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.CouponDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CouponDao extends CrudRepository<Coupon,Long> {
    Optional<Coupon> findByCouponCode(String couponCode);

    List<Coupon> findByCustomers(Customer customer);

    List<Coupon> findAll();

    @Query(value = "SELECT c.coupon_id AS couponId, c.coupon_code AS couponCode, c.discount_amount AS discountAmount, " +
            "c.min_purchase_amount AS minPurchaseAmount, FORMAT(c.expiration_date, 'yyyy-MM-dd') AS expirationDate, " +
            "CAST(c.is_active AS BIT) AS isActive, " +
            "STRING_AGG(cust.username,',') AS assignedTo " +
            "FROM coupon c " +
            "LEFT JOIN customer_coupon cc ON c.coupon_id = cc.coupon_id " +
            "LEFT JOIN cloudy_user cust ON cc.customer_id = cust.user_id " +
            "GROUP BY c.coupon_id, c.coupon_code, c.discount_amount, c.min_purchase_amount, c.expiration_date, c.is_active",
            nativeQuery = true)
    List<Map<String,Object>> findAllCouponsWithAssignmentDetails();



    @Query("SELECT NEW com.Cloudy.Cloudy_Self_Checkout_POS.dto.CouponDTO(" +
            "c.couponId, c.couponCode, c.discountAmount, c.minPurchaseAmount, c.expirationDate, c.isActive, NULL) " +
            "FROM Coupon c WHERE c.customers IS EMPTY")
    List<CouponDTO> findUnassignedCoupons();
}
