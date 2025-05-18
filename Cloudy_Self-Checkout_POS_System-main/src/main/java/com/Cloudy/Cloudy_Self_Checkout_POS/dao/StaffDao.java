package com.Cloudy.Cloudy_Self_Checkout_POS.dao;


import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Staff;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.StaffWrapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StaffDao extends JpaRepository<Staff, Long> {

    Staff findByUserId(Long userId);


    Page<Staff> findAll(Pageable pageable);

    @Query("""
    SELECT s FROM Staff s WHERE s.status = false
    """)
    Page<Staff> findByInactive(Pageable pageable);

    @Query(value = "SELECT COUNT(DISTINCT s.user_id) " +
            "FROM staff s " +
            "JOIN cloudy_user u ON s.user_id = u.user_id " +
            "WHERE u.status = 1", nativeQuery = true)
    long countActiveStaff();


//    List<UserResponseBody> getAllStaff();
}
