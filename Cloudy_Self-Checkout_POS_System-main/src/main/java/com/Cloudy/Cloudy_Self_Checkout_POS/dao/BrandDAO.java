package com.Cloudy.Cloudy_Self_Checkout_POS.dao;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface BrandDAO extends CrudRepository<Brand,Long> {
    Page<Brand> findAll(Pageable pageable);

    @Query("""
    SELECT b FROM Brand b WHERE b.isActive = false
    """)
    Page<Brand> findByInactive(Pageable pageable);

    @Query("""
    SELECT b.id FROM Brand b WHERE b.name = :brandName
    """)
    Long findIdByName(@Param("brandName") String brandName);
}
