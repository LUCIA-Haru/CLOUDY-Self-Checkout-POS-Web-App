package com.Cloudy.Cloudy_Self_Checkout_POS.utils;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Staff;
import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.User;
import com.Cloudy.Cloudy_Self_Checkout_POS.constants.ValueConstant;
import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.customException.GlobalExceptionHandler;
import com.Cloudy.Cloudy_Self_Checkout_POS.dao.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserUtils {


    private final UserDao userDao;
    private final StaffDao staffDao;

    @Autowired
    GlobalExceptionHandler globalExceptionHandler;
    /**
     * Finds a user by email and wraps the result in an Optional.
     *
     * @param email The email to search for.
     * @return An Optional containing the user if found, or an empty Optional otherwise.
     */


    public Optional<User> getUserByEmail(String email) throws CustomSystemException {
        Optional<User> userOptional = Optional.ofNullable(userDao.findByEmail(email));
        // If the user is null, throw a CustomSystemException
        if (userOptional == null) {
            throw new CustomSystemException(HttpStatus.BAD_REQUEST, ValueConstant.USER_NOT_FOUND);
        }

        return userOptional;
    }

    public Optional<User> getUserByUserName(String username) {
        Optional<User> userOptional = Optional.ofNullable(userDao.findByUsername(username));
        return userOptional;
    }

    public User getUserByUsernameOptional(String username) throws CustomSystemException {
        // Retrieve the user from the DAO layer
        User user = userDao.findByUsername(username);

        // If the user is null, throw a CustomSystemException
        if (user == null) {
            throw new CustomSystemException(HttpStatus.BAD_REQUEST, ValueConstant.USER_NOT_FOUND);
        }

        // Wrap the non-null user in an Optional and return it
        return user;
    }
    public void validateStaffForUser(User user, String className, String methodName) throws CustomSystemException {

            Staff staff = staffDao.findByUserId(user.getUserId());
            if (staff == null) {
                log.warn("{} => {} => Staff not found for userId: {}", className, methodName, user.getUserId());
               throw new CustomSystemException(HttpStatus.BAD_REQUEST, ValueConstant.STAFF_NOT_FOUND);
            }

    }

}
