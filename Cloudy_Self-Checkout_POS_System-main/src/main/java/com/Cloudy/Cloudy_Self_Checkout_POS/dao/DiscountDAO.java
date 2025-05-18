package com.Cloudy.Cloudy_Self_Checkout_POS.dao;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Discount;
import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.Cloudy.Cloudy_Self_Checkout_POS.dto.DiscountBarcodeDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiscountDAO extends JpaRepository<Discount,Long> {


    Optional<Discount> findByProduct(Product product);
//    boolean existByProduct(Product product);


    @Query("SELECT d FROM Discount d WHERE d.product.productId = :productId")
    Optional<Discount> findByProductId(@Param("productId") Long productId);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN TRUE ELSE FALSE END FROM Discount d WHERE d.product.productId = :productId")
    boolean existsByProductId(@Param("productId") Long productId);


    @Query("SELECT new com.Cloudy.Cloudy_Self_Checkout_POS.dto.DiscountBarcodeDTO(d.product.barcode, " +
            "new com.Cloudy.Cloudy_Self_Checkout_POS.dto.DiscountDTO(d.discountId, d.guid, d.discountValue, d.isPercentage, d.startDate, d.endDate, d.product.productId, d.category.categoryId," +
            "d.product.productName,d.category.categoryName)) " +
            "FROM Discount d WHERE d.product.barcode IN :barcodes " +
            "AND d.startDate <= :today AND (d.endDate IS NULL OR d.endDate >= :today)")
    List<DiscountBarcodeDTO> findActiveDiscountsByBarcodes(@Param("barcodes") List<String> barcodes, @Param("today") LocalDate today);





}
