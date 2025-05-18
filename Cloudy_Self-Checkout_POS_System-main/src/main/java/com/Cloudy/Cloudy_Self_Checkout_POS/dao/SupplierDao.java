package com.Cloudy.Cloudy_Self_Checkout_POS.dao;


import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Brand;
import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface SupplierDao  extends CrudRepository<Supplier,Long> {
    Page<Supplier> findAll(Pageable pageable);

    @Query("""
    SELECT s FROM Supplier s WHERE s.isActive = false
    """)
    Page<Supplier> findByInactive(Pageable pageable);
}
