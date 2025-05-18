package com.Cloudy.Cloudy_Self_Checkout_POS.dao;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Purchase;
import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface PurchaseDao extends JpaRepository<Purchase,Long> {

    List<Purchase> findByUser(User user);
    List<Purchase> findByUserOrderByPurchaseIdDesc(User user);


    @Query(value = """
        SELECT period, SUM(total_amount) AS total
        FROM (
            SELECT
                CASE :period
                    WHEN 'daily' THEN CONVERT(varchar, p.created_on, 23)
                    WHEN 'weekly' THEN CAST(DATEPART(year, p.created_on) AS varchar) + '-W' + RIGHT('0' + CAST(DATEPART(isowk, p.created_on) AS varchar), 2)
                    WHEN 'monthly' THEN LEFT(CONVERT(varchar, p.created_on, 120), 7)
                END AS period,
                p.total_amount
            FROM purchase p
            WHERE (:startDate IS NULL OR CAST(p.created_on AS date) >= :startDate)
              AND (:endDate IS NULL OR CAST(p.created_on AS date) <= :endDate)
        ) AS sub
        GROUP BY period
        ORDER BY period
        """, nativeQuery = true)
    List<Object[]> findRevenueOverTime(String period, LocalDate startDate, LocalDate endDate);

    @Query(value = """
        SELECT category_name, SUM(total_amount) AS total
        FROM (
            SELECT
                c.category_name,
                p.total_amount
            FROM purchase p
            JOIN purchase_items pi ON p.purchase_id = pi.purchase_id
            JOIN product pr ON pi.product_id = pr.product_id
            JOIN category c ON pr.category_id = c.category_id
            WHERE (:startDate IS NULL OR CAST(p.created_on AS date) >= :startDate)
              AND (:endDate IS NULL OR CAST(p.created_on AS date) <= :endDate)
        ) AS sub
        GROUP BY category_name
        """, nativeQuery = true)
    List<Object[]> findRevenueByCategory(LocalDate startDate, LocalDate endDate);

    @Query(value = """
        SELECT coupon_code, SUM(coupon_discount) AS total_discount, COUNT(purchase_id) AS usage_count
        FROM (
            SELECT 
                c.coupon_code,
                p.coupon_discount,
                p.purchase_id
            FROM purchase p
            JOIN coupon c ON p.coupon_id = c.coupon_id
            WHERE (:startDate IS NULL OR CAST(p.created_on AS date) >= :startDate)
              AND (:endDate IS NULL OR CAST(p.created_on AS date) <= :endDate)
        ) AS sub
        GROUP BY coupon_code
        """, nativeQuery = true)
    List<Object[]> findCouponDiscountImpact(LocalDate startDate, LocalDate endDate);

    @Query(value = """
        SELECT brand_name, SUM(quantity) AS total_quantity, SUM(sub_total) AS total_revenue
        FROM (
            SELECT 
                b.name AS brand_name,
                pi.quantity,
                pi.sub_total
            FROM purchase p
            JOIN purchase_items pi ON p.purchase_id = pi.purchase_id
            JOIN product pr ON pi.product_id = pr.product_id
            JOIN brand b ON pr.brand_id = b.id
            WHERE (:startDate IS NULL OR CAST(p.created_on AS date) >= :startDate)
              AND (:endDate IS NULL OR CAST(p.created_on AS date) <= :endDate)
        ) AS sub
        GROUP BY brand_name
        ORDER BY total_quantity DESC
        """, nativeQuery = true)
    List<Object[]> findBrandPopularity(LocalDate startDate, LocalDate endDate);

    @Query(value = """
        SELECT user_id, first_name, last_name, COUNT(purchase_id) AS transaction_count
        FROM (
            SELECT
                s.user_id,
                s.first_name,
                s.last_name,
                p.purchase_id
            FROM purchase p
            JOIN staff s ON p.user_id = s.user_id
            WHERE (:startDate IS NULL OR CAST(p.created_on AS date) >= :startDate)
              AND (:endDate IS NULL OR CAST(p.created_on AS date) <= :endDate)
        ) AS sub
        GROUP BY user_id, first_name, last_name
        """, nativeQuery = true)
    List<Object[]> findStaffActivity(LocalDate startDate, LocalDate endDate);

    @Query(value = """
        SELECT TOP 15 p.purchase_id, p.created_on, p.total_amount
        FROM purchase p
        WHERE
          (:startDate IS NULL OR CAST(p.created_on AS date) >= :startDate)
          AND (:endDate IS NULL OR CAST(p.created_on AS date) <= :endDate)
        ORDER BY p.created_on DESC
        """, nativeQuery = true)
    List<Object[]> findPurchasesByUserId(LocalDate startDate, LocalDate endDate);


}
