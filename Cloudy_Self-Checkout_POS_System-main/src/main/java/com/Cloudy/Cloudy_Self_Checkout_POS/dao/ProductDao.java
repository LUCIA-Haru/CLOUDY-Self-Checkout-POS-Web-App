package com.Cloudy.Cloudy_Self_Checkout_POS.dao;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductDao extends JpaRepository<Product,Long> {


    Optional<Product> findByBarcode(String barcode);

    @Modifying
    @Query("UPDATE Product p SET p.hasDiscount = :status WHERE p.productId = :productId")
    int updateDisStatus(Long productId, boolean status);

    @Query("SELECT p FROM Product p JOIN p.discounts d " +
            "WHERE" +
            " d.startDate <= :today AND d.endDate >= :today")
    List<Product> findProductsOnDiscount(LocalDate today);

    @Query(value = """
        SELECT product_id, product_name, SUM(quantity) AS quantity_sold, SUM(sub_total) AS total_revenue
        FROM (
            SELECT 
                pr.product_id,
                pr.product_name,
                pi.quantity,
                pi.sub_total
            FROM product pr
            JOIN purchase_items pi ON pr.product_id = pi.product_id
            JOIN purchase p ON pi.purchase_id = p.purchase_id
            WHERE (:startDate IS NULL OR CAST(p.created_on AS date) >= :startDate)
              AND (:endDate IS NULL OR CAST(p.created_on AS date) <= :endDate)
        ) AS sub
        GROUP BY product_id, product_name
        ORDER BY quantity_sold DESC
        """, nativeQuery = true)
    List<Object[]> findTopSellingProducts(LocalDate startDate, LocalDate endDate);

    @Query(value = """
    WITH LowStockProducts AS (
        SELECT 
            p.category_id,
            p.product_id,
            p.product_name
        FROM product p
        WHERE COALESCE(p.stock_unit, 0) <= 50
    ),
    LowStockDetails AS (
        SELECT 
            lsp.category_id,
            (
                SELECT 
                    lsp2.product_id AS productId,
                    lsp2.product_name AS productName
                FROM LowStockProducts lsp2
                WHERE lsp2.category_id = lsp.category_id
                FOR JSON PATH
            ) AS lowStockProductsDetails
        FROM LowStockProducts lsp
        GROUP BY lsp.category_id
    )
    SELECT
        c.category_id AS categoryId,
        c.category_name AS categoryName,
        COUNT(DISTINCT pr.product_id) AS totalProducts,
        SUM(COALESCE(pr.stock_unit, 0)) AS totalStockUnits,
        COUNT(DISTINCT lsp.product_id) AS lowStockProducts,
        ISNULL(ld.lowStockProductsDetails, '[]') AS lowStockProductsDetails
    FROM product pr
    JOIN category c ON pr.category_id = c.category_id
    LEFT JOIN LowStockProducts lsp ON lsp.category_id = c.category_id
    LEFT JOIN LowStockDetails ld ON ld.category_id = c.category_id
    GROUP BY c.category_id, c.category_name, ld.lowStockProductsDetails
""", nativeQuery = true)
    List<Object[]> findStockLevelsByCategory();

    @Query(value = """
        SELECT pr.product_id, pr.product_name,pr.barcode, pr.exp_date, pr.stock_unit
        FROM product pr
        WHERE pr.exp_date IS NOT NULL
          AND DATEDIFF(day, GETDATE(), pr.exp_date) <= :daysThreshold
        """, nativeQuery = true)
    List<Object[]> findProductExpiryAlerts(int daysThreshold);
}
