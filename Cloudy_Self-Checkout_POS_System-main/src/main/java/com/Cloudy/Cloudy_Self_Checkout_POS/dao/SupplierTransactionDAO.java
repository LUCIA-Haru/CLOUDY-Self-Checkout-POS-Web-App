package com.Cloudy.Cloudy_Self_Checkout_POS.dao;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.SupplierTransaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface SupplierTransactionDAO extends CrudRepository<SupplierTransaction,Long> {

    List<SupplierTransaction> findAll();
    List<SupplierTransaction> findAllByOrderByTransactionDateDesc();

    @Query("SELECT t FROM SupplierTransaction t " +
            "LEFT JOIN FETCH t.supplier " +
            "LEFT JOIN FETCH t.brand " +
            "LEFT JOIN FETCH t.category " +
            "LEFT JOIN FETCH t.products p")
    List<SupplierTransaction> findAllTransaction();

    @Query(value = """
        SELECT name, SUM(quantity) AS total_quantity, COUNT(transaction_id) AS transaction_count
        FROM (
            SELECT
                s.name,
                st.quantity,
                st.transaction_id
            FROM supplier_transaction st
            JOIN supplier s ON st.supplier_id = s.id
            WHERE (:startDate IS NULL OR CAST(st.transaction_date AS date) >= :startDate)
              AND (:endDate IS NULL OR CAST(st.transaction_date AS date) <= :endDate)
        ) AS sub
        GROUP BY name
        """, nativeQuery = true)
    List<Object[]> findSupplierPerformance(LocalDate startDate, LocalDate endDate);
}
