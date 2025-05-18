package com.Cloudy.Cloudy_Self_Checkout_POS.JWT;

import com.Cloudy.Cloudy_Self_Checkout_POS.dao.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CloudyUserDetailService  implements UserDetailsService {

    @Autowired
    UserDao userDao;

    private com.Cloudy.Cloudy_Self_Checkout_POS.POJO.User userDetails;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        userDetails = userDao.findByUsername(username);

        if (userDetails != null) {
            // Check if the user is a customer
           Map<String, Object> user = userDao.findUserTypeByUsername(username);

           String userType = (String) user.get("user_type");
           boolean isCustomer = userType.equalsIgnoreCase("customer");

            // Fetch roles from the user entity and map them to GrantedAuthority
            List<GrantedAuthority> authorities = new ArrayList<>();

            if (isCustomer) {
                // Assign ROLE_CUSTOMER if the user is a customer
                authorities.add(new SimpleGrantedAuthority("CUSTOMER"));
            } else {
                // Assign other roles for non-customers
                authorities.add(new SimpleGrantedAuthority(userDetails.getRole()));
            }

            // Return UserDetails with or without roles depending on the user type
            return new User(
                    userDetails.getUsername(),
                    userDetails.getPassword(),
                    authorities
            );
        } else {
            throw new UsernameNotFoundException("User Not Found with Username: " + username);
        }
    }

    public com.Cloudy.Cloudy_Self_Checkout_POS.POJO.User getUserDetails(){return userDetails;}


//
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @
//    public boolean isEnabled() {
//        return true;
//    }
}
