package com.Cloudy.Cloudy_Self_Checkout_POS.dao;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Customer;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.CustomerWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ResponseBody.UserResponseBody;
import jakarta.persistence.NamedQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface CustomerDao extends JpaRepository<Customer,Long> {


    Customer findByUserId(Long userId);

    @Query( value = "select new com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.CustomerWrapper" +
            "(c.profilePhoto,c.userId,c.guid,c.username,c.firstName,c.lastName,c.email," +
            "c.createdOn,c.updatedOn,c.status,c.role,c.address,c.dob,c.phoneNo) from Customer c ")
    List<CustomerWrapper> getAllCustomers();

    @Query(value = """
        SELECT user_id, first_name, last_name, COUNT(purchase_id) AS purchase_count, SUM(total_amount) AS total_spent
        FROM (
            SELECT 
                c.user_id,
                c.first_name,
                c.last_name,
                p.purchase_id,
                p.total_amount
            FROM customer c
            LEFT JOIN purchase p ON c.user_id = p.user_id
            WHERE (:startDate IS NULL OR CAST(p.created_on AS date) >= :startDate)
              AND (:endDate IS NULL OR CAST(p.created_on AS date) <= :endDate)
        ) AS sub
        GROUP BY user_id, first_name, last_name
        """, nativeQuery = true)
    List<Object[]> findCustomerPurchaseTrends(LocalDate startDate, LocalDate endDate);

    @Query(value = """
        SELECT
            CASE
                WHEN DATEDIFF(year, dob, GETDATE()) < 18 THEN '<18'
                WHEN DATEDIFF(year, dob, GETDATE()) BETWEEN 18 AND 24 THEN '18-24'
                WHEN DATEDIFF(year, dob, GETDATE()) BETWEEN 25 AND 34 THEN '25-34'
                WHEN DATEDIFF(year, dob, GETDATE()) BETWEEN 35 AND 44 THEN '35-44'
                ELSE '45+'
            END AS age_group,
            COUNT(user_id) AS customer_count
        FROM customer
        WHERE dob IS NOT NULL
        GROUP BY
            CASE
                WHEN DATEDIFF(year, dob, GETDATE()) < 18 THEN '<18'
                WHEN DATEDIFF(year, dob, GETDATE()) BETWEEN 18 AND 24 THEN '18-24'
                WHEN DATEDIFF(year, dob, GETDATE()) BETWEEN 25 AND 34 THEN '25-34'
                WHEN DATEDIFF(year, dob, GETDATE()) BETWEEN 35 AND 44 THEN '35-44'
                ELSE '45+'
            END
        """, nativeQuery = true)
    List<Object[]> findCustomerDemographics();
}
