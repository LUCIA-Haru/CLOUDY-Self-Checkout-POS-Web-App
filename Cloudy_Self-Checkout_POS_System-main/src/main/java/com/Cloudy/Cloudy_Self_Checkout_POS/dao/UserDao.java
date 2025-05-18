package com.Cloudy.Cloudy_Self_Checkout_POS.dao;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.User;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ResponseBody.UserResponseBody;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserDao extends JpaRepository<User,Long> {
    User findByUsername(String username);

//    Optional<com.Cloudy.Cloudy_Self_Checkout_POS.POJO.User> findByUsernameOptional (String username);
    User findByEmail(String email);

//    List<UserResponseBody>getAllUser();

    @Query(value = "SELECT username, user_type FROM cloudy_user WHERE username = :username", nativeQuery = true)
    Map<String, Object> findUserTypeByUsername(@Param("username") String username);


}
