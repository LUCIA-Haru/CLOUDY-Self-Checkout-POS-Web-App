package com.Cloudy.Cloudy_Self_Checkout_POS.dao;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.OtpAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface OtpDao extends JpaRepository<OtpAuth,Long> {

//    OtpAuth findByEmail(@Param("email")String email);

    Optional<OtpAuth> findByOtpEmail(String otpEmail);

    @Query("SELECT o.user.userId, o.otpEmail, o.otpFailedAttempts, o.otpLockedUntil " +
            "FROM OtpAuth o " +
            "WHERE o.otpFailedAttempts > 0")
    List<Object[]> findOtpFailureAlerts();
}
