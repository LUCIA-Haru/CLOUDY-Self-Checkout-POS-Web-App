package com.Cloudy.Cloudy_Self_Checkout_POS.dao;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Category;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.CustomerWrapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Optional;

public interface CategoryDao extends JpaRepository<Category,Long> {
    Page<Category> findAll(Pageable pageable);

    Optional<Category> findById(Long Id);

    //    @Query(value = "SELECT * FROM category", nativeQuery = true)
//    List<Map<String, Object>>getAllCategory(Pageable pageable);
//
//    @Query(value = "select count(*) from category",nativeQuery = true)
//    Long getCategoryCount();

    @Query("""
    SELECT c.id FROM Category c WHERE c.categoryName = :categoryName
    """)
    Long findIdByName(@Param("categoryName") String categoryName);

}
