package com.Cloudy.Cloudy_Self_Checkout_POS.dao;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.LoyaltyPoints;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.loyaltyPointsHistoryDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LoyaltyPointsDAO extends CrudRepository<LoyaltyPoints,Long> {

    @Query("Select Coalesce(Sum(lp.pointsEarned) - Sum(lp.pointsUsed),0) from LoyaltyPoints lp where lp.user.userId = :userId")
    Integer calculateTotalPoints(@Param("userId") Long userId);


    @Query("SELECT NEW com.Cloudy.Cloudy_Self_Checkout_POS.dto.loyaltyPointsHistoryDTO(" +
            "u.username, p.purchaseId, p.finalAmount, lp.pointsEarned, lp.pointsUsed, lp.transactionType, lp.transactionDate) " +
            "FROM LoyaltyPoints lp " +
            "JOIN lp.user u " +
            "LEFT JOIN lp.purchase p " +
            "WHERE u.userId = :userId " +
            "ORDER BY lp.transactionDate DESC")
    List<loyaltyPointsHistoryDTO> findByUserIdOrderByTransactionDateDesc(@Param("userId") Long userId);


    @Query(value = """
        SELECT user_id, first_name, SUM(points_earned) AS points_earned, SUM(points_used) AS points_used
        FROM (
            SELECT
                c.user_id,
                c.first_name,
                lp.points_earned,
                lp.points_used
            FROM loyalty_points lp
            JOIN customer c ON lp.user_id = c.user_id
            WHERE (:startDate IS NULL OR CAST(lp.transaction_date AS date) >= :startDate)
              AND (:endDate IS NULL OR CAST(lp.transaction_date AS date) <= :endDate)
        ) AS sub
        GROUP BY user_id, first_name
        """, nativeQuery = true)
    List<Object[]> findLoyaltyPointsUsage(LocalDate startDate, LocalDate endDate);

}