package com.Cloudy.Cloudy_Self_Checkout_POS.serviceImpl;

import com.Cloudy.Cloudy_Self_Checkout_POS.JWT.CloudyUserDetailService;
import com.Cloudy.Cloudy_Self_Checkout_POS.JWT.JWTFilter;
import com.Cloudy.Cloudy_Self_Checkout_POS.JWT.JWTUtil;
import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Customer;
import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.OtpAuth;
import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Staff;
import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.User;
import com.Cloudy.Cloudy_Self_Checkout_POS.constants.ValueConstant;
import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.customException.GlobalExceptionHandler;
import com.Cloudy.Cloudy_Self_Checkout_POS.dao.CustomerDao;
import com.Cloudy.Cloudy_Self_Checkout_POS.dao.OtpDao;
import com.Cloudy.Cloudy_Self_Checkout_POS.dao.StaffDao;
import com.Cloudy.Cloudy_Self_Checkout_POS.dao.UserDao;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.RegisterationRequestBody;
import com.Cloudy.Cloudy_Self_Checkout_POS.service.UserService;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.CloudyUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.EmailUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.UserUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ResponseBody.UserResponseBody;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;

    @Autowired
    CustomerDao customerDao;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CloudyUserDetailService cloudyUserDetailService;

    @Autowired
    JWTUtil jwtUtil;

    @Autowired
    JWTFilter jwtFilter;

    @Autowired
    EmailUtils emailUtils;

    @Autowired
    UserUtils userUtils;

    @Autowired
    GlobalExceptionHandler globalExceptionHandler;

    @Autowired
    OtpDao otpDao;

    @Autowired
    StaffDao staffDao;


    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    private final String UserServiceImpl = "UserServiceImpl";

//----------------Customer--------
    @Override
    public ResponseEntity<ApiResponseWrapper<UserResponseBody>> signUp(RegisterationRequestBody requestBody) {
        log.info("Inside SignUP {}",requestBody);
        try {
            if (userUtils.getUserByUserName(requestBody.getUsername()).isPresent())
                return CloudyUtils.getResponseEntityCustom(HttpStatus.BAD_REQUEST,ValueConstant.USEER_EXIST);
            // Check if user already exists
            if (userUtils.getUserByEmail(requestBody.getEmail()).isPresent())
                return CloudyUtils.getResponseEntityCustom(HttpStatus.BAD_REQUEST,ValueConstant.USEER_EXIST);
//            Generate OTP
            String otp = generateOTP();
//            Save OTP to the db
            OtpAuth otpAuth = new OtpAuth();
            otpAuth.setOtp(otp);
            otpAuth.setOtpEmail(requestBody.getEmail());
            otpAuth.setOtpCreatedAt(LocalDateTime.now());
            otpAuth.setOtpFailedAttempts(0); //reset attempts
            otpAuth.setOtpLockedUntil(0L); //reset lockedout time
            otpAuth.setOtpExpiresAt(LocalDateTime.now().plusMinutes(5));// OTP expires in 5 minutes
            otpDao.save(otpAuth);

            //Send OTP via email
            emailUtils.otpEmail(requestBody.getEmail(),"Account Verification" ,otp);

            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK,ValueConstant.EMAIL_SEND);


        }catch (Exception e){
            log.error("An error occurred: ", e.getMessage());
            e.printStackTrace();
        }

        return CloudyUtils.getResponseEntityCustom( HttpStatus.INTERNAL_SERVER_ERROR,ValueConstant.SOMETHING_WENT_WRONG);
    }

//    ----------------------login----------------
    @Override
    public ResponseEntity<ApiResponseWrapper<UserResponseBody>> login(RegisterationRequestBody requestBody) {
        log.info("Inside login");
        Customer customer = null;
        Staff staff = null;
        try{
            // check user
            User user = userUtils.getUserByUserName(requestBody.getUsername())
                    .orElseThrow(() -> new CustomSystemException(String.valueOf(HttpStatus.NOT_FOUND),ValueConstant.USER_NOT_FOUND));



            boolean isAuthenticated = passwordEncoder.matches(requestBody.getPassword(), user.getPassword());



            if (isAuthenticated){
                // Authenticate the user and load user details
//                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(requestBody.getEmail(),requestBody.getPassword());

                // Load user details through CustomerUserDetailsService = Explict call to make sure is data is loaded
                cloudyUserDetailService.loadUserByUsername(user.getUsername());
                if (cloudyUserDetailService.getUserDetails().getStatus().equals(true)){
                    String token = jwtUtil.generateToken(cloudyUserDetailService.getUserDetails().getUsername(),cloudyUserDetailService.getUserDetails().getRole() ,cloudyUserDetailService.getUserDetails().getProfilePhoto());



//                retieve related data
                    // Check if the user is a customer
                    Map<String, Object> userDetails = userDao.findUserTypeByUsername(user.getUsername());

                    String userType = (String) userDetails.get("user_type");
                    boolean isCustomer = userType.equalsIgnoreCase("customer");

                    if (isCustomer) {
                        customer = customerDao.findByUserId(user.getUserId());
                    }else {
                        staff = staffDao.findByUserId(user.getUserId());
                    }
                    UserResponseBody newResponse = new UserResponseBody(
                            token,
                            user.getProfilePhoto(),
                            user.getUserId(),
                            isCustomer ? customer.getGuid() : staff.getGuid(),
                            user.getUsername(),
                            isCustomer ? customer.getFirstName() : staff.getFirstName(),
                            isCustomer ? customer.getLastName() : staff.getLastName(),
                            user.getEmail(),
                            user.getCreatedOn(),
                            user.getUpdatedOn(),
                            user.getRole(),
                            user.getStatus(),
                            isCustomer ? customer.getAddress() : staff.getAddress(),
                            isCustomer ? customer.getDob() : staff.getDob(),
                            isCustomer ? customer.getPhoneNo() : staff.getPhoneNo(),
                            !isCustomer ? staff.getDepartment() : null, // Use null or a default value for customers
                            !isCustomer ? staff.getPosition() : null,   // Use null or a default value for customers
                            !isCustomer ? staff.getCreatedBy() : null   // Use null or a default value for customers
                    );
                    return CloudyUtils.getResponseEntityCustom(HttpStatus.OK,ValueConstant.LOGIN,newResponse);
                }else {
                    return CloudyUtils.getResponseEntityCustom(HttpStatus.NOT_FOUND,ValueConstant.INACTIVE);
                }
            }else {
                return CloudyUtils.getResponseEntityCustom(HttpStatus.BAD_REQUEST, ValueConstant.INVALID_DATA);
            }

        }catch (CustomSystemException ex){
            ex.getMessage();
            ex.printStackTrace();
            return globalExceptionHandler.handleCustomSystemException(ex);
        }catch (Exception e){
            e.printStackTrace();
        }
        return CloudyUtils.getResponseEntityCustom( HttpStatus.INTERNAL_SERVER_ERROR,ValueConstant.SOMETHING_WENT_WRONG);
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<String>> changePassword(Long id, String oldPassword,String newPassword) throws CustomSystemException {
        log.info("{} => changePassword() => Reason:: change Password ||| Username:::{}",
                UserServiceImpl,id);
        try {
            User user = userDao.findById(id).orElseThrow(() -> new CustomSystemException(HttpStatus.BAD_REQUEST, ValueConstant.USER_NOT_FOUND));

            if (passwordEncoder.matches(oldPassword,user.getPassword())){
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setUpdatedOn(LocalDateTime.now());
                userDao.save(user);
                log.info("✅ Password Update Successfully ||| username::: {}",id);
                return CloudyUtils.getResponseEntityCustom(HttpStatus.OK,"Password Update Successfully");
            }

            return CloudyUtils.getResponseEntityCustom( HttpStatus.BAD_REQUEST,ValueConstant.BAD_CREDENTIALS);

        }catch (Exception e){
            log.error("{} => changePassword() => Subject: change Password => Unexpected Error: {}",UserServiceImpl, e.getMessage());
            throw e;
        }

    }

    @Override
    public ResponseEntity<ApiResponseWrapper<String>> forgotPass( String email) throws CustomSystemException, MessagingException {
        log.info("{} => forgotPass() => Reason:: forgot Password ||| Username:::{}",
                UserServiceImpl,email);
        try {
            User user = userDao.findByEmail(email);
            if (user == null) throw new CustomSystemException(HttpStatus.BAD_REQUEST,ValueConstant.USER_NOT_FOUND);
            String otp = generateOTP();

            OtpAuth otpAuth = new OtpAuth();
            otpAuth.setOtp(otp);
            otpAuth.setOtpEmail(email);
            otpAuth.setOtpCreatedAt(LocalDateTime.now());
            otpAuth.setOtpFailedAttempts(0); //reset attempts
            otpAuth.setOtpLockedUntil(0L); //reset lockedout time
            otpAuth.setOtpExpiresAt(LocalDateTime.now().plusMinutes(5));// OTP expires in 5 minutes
            otpDao.save(otpAuth);

            //                reset link
//            String resetLink = "http://localhost:4200/#/user/resetPassoword?email=" + user.getEmail() + "&otp=" + otp;

            emailUtils.forgotEmail(
                    user.getEmail(),
                    "Reset your password -Credentials by Cloudy POS System",
                    otp
            );
            return  CloudyUtils.getResponseEntityCustom(HttpStatus.OK,"Check your email for reset instructions!");

        }catch (Exception e){
            log.error("{} => forgotPass() => Subject: forgot Password => Unexpected Error: {}",UserServiceImpl, e.getMessage());
            throw new CustomSystemException(HttpStatus.BAD_REQUEST,ValueConstant.BAD_CREDENTIALS);
        }

    }

    @Override
    public ResponseEntity<ApiResponseWrapper<String>> resetPass(String email, String pass) throws CustomSystemException {
        log.info("{} => resetPass() => Reason:: reset Password ||| Username:::{}",
                UserServiceImpl,email);
        try {
            User user = userUtils.getUserByEmail(email).orElseThrow(() -> new CustomSystemException(HttpStatus.BAD_REQUEST,ValueConstant.USER_NOT_FOUND));
            user.setPassword(passwordEncoder.encode(pass));
            user.setUpdatedOn(LocalDateTime.now());
            userDao.save(user);
            log.info("✅ Password Reset Successfully ||| username::: {}",user.getUsername());
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK,"Password reset Successfully");

        }catch (Exception e){
            log.error("{} => resetPass() => Subject: reset Password => Unexpected Error: {}",UserServiceImpl, e.getMessage());
            throw new CustomSystemException(HttpStatus.BAD_REQUEST,ValueConstant.BAD_CREDENTIALS);
        }
    }


    //    -------------------OTP
public static String generateOTP(){

    int otpLength = 4;
    SecureRandom random = new SecureRandom();
    StringBuilder otp = new StringBuilder();
    for (int i = 0; i < otpLength; i++)
        otp.append(random.nextInt(10));//Generate a random digit(0-9)
    String generatedOtp = otp.toString();
    return generatedOtp;
}

    @Override
    public ResponseEntity<ApiResponseWrapper<UserResponseBody>> verifyOtp(RegisterationRequestBody requestBody, String otp) {
        log.info("Inside Verification {}",requestBody);
        try {
            long currentTime = System.currentTimeMillis();
            User user ;
            Customer customer;

            OtpAuth otpAuthOptional = otpDao.findByOtpEmail(requestBody.getEmail())
                    .orElseThrow(() -> new CustomSystemException(HttpStatus.NOT_FOUND,ValueConstant.OTP_NOT_FOUND));

            if(checkOtpExpire(otpAuthOptional.getOtpExpiresAt(),otpAuthOptional)== true)
                return CloudyUtils.getResponseEntityCustom(HttpStatus.BAD_REQUEST,ValueConstant.OTP_EXPIRED);

            // Check if OTP matches and if not add failed attempts
            if (!otpAuthOptional.getOtp().equals(otp)){
                otpAuthOptional.setOtpFailedAttempts(otpAuthOptional.getOtpFailedAttempts()+1);
                //        Check lock
                Boolean lockAcc = lockAcc(otpAuthOptional.getOtpFailedAttempts());
                if (lockAcc){
                    otpAuthOptional.setOtpLockedUntil(currentTime);
                    otpDao.save(otpAuthOptional);
                    return CloudyUtils.getResponseEntityCustom(HttpStatus.FORBIDDEN,ValueConstant.OTP_ATTEMPTS);
                }
                otpDao.save(otpAuthOptional);
                return CloudyUtils.getResponseEntityCustom(HttpStatus.BAD_REQUEST,ValueConstant.OTP_ATTEMPT_FAIL);
            }
//save user if match
            if (otpAuthOptional.getOtp().equals(otp)){

//                Customer
                customer = Customer.builder()
                        .firstName(requestBody.getFirstName())
                        .lastName(requestBody.getLastName())
                        .guid(UUID.randomUUID().toString())
                        .username(requestBody.getUsername())
                        .email(requestBody.getEmail())
                        .password(passwordEncoder.encode(requestBody.getPassword()))
                        .role("CUSTOMER")
                        .status(true)
                        .createdOn(LocalDateTime.now())
                        .build();
                customerDao.save(customer);

                UserResponseBody newResponse = new UserResponseBody(customer.getUserId(), customer.getUsername(),customer.getEmail(),
                        customer.getCreatedOn(),customer.getStatus(),customer.getRole(),
                        customer.getFirstName(),customer.getLastName(), customer.getGuid());
                otpDao.delete(otpAuthOptional);
                return CloudyUtils.getResponseEntityCustom(HttpStatus.OK,ValueConstant.SAVE,newResponse);
            }

            return CloudyUtils.getResponseEntityCustom( HttpStatus.INTERNAL_SERVER_ERROR,ValueConstant.SOMETHING_WENT_WRONG);

        } catch (CustomSystemException ex){
            ex.getMessage();
            ex.printStackTrace();
            return globalExceptionHandler.handleCustomSystemException(ex);
        } catch (Exception e){
            log.error("An error occurred: ", e);
            e.printStackTrace();
        }

        return CloudyUtils.getResponseEntityCustom( HttpStatus.INTERNAL_SERVER_ERROR,ValueConstant.SOMETHING_WENT_WRONG);
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<String>> verifyOtpPass(String email, String otp) {
        log.info("Inside Verification {}",email);
        try {
            long currentTime = System.currentTimeMillis();

            OtpAuth otpAuthOptional = otpDao.findByOtpEmail(email)
                    .orElseThrow(() -> new CustomSystemException(HttpStatus.NOT_FOUND,ValueConstant.OTP_NOT_FOUND));

            if(checkOtpExpire(otpAuthOptional.getOtpExpiresAt(),otpAuthOptional)== true)
                return CloudyUtils.getResponseEntityCustom(HttpStatus.BAD_REQUEST,ValueConstant.OTP_EXPIRED);

            // Check if OTP matches and if not add failed attempts
            if (!otpAuthOptional.getOtp().equals(otp)){
                otpAuthOptional.setOtpFailedAttempts(otpAuthOptional.getOtpFailedAttempts()+1);
                //        Check lock
                Boolean lockAcc = lockAcc(otpAuthOptional.getOtpFailedAttempts());
                if (lockAcc){
                    otpAuthOptional.setOtpLockedUntil(currentTime);
                    otpDao.save(otpAuthOptional);
                    return CloudyUtils.getResponseEntityCustom(HttpStatus.FORBIDDEN,ValueConstant.OTP_ATTEMPTS);
                }
                otpDao.save(otpAuthOptional);
                return CloudyUtils.getResponseEntityCustom(HttpStatus.BAD_REQUEST,ValueConstant.OTP_ATTEMPT_FAIL);
            }
//save user if match
            if (otpAuthOptional.getOtp().equals(otp)){

                otpDao.delete(otpAuthOptional);
                return CloudyUtils.getResponseEntityCustom(HttpStatus.OK,ValueConstant.SAVE,"Reset Successfully");
            }

            return CloudyUtils.getResponseEntityCustom( HttpStatus.INTERNAL_SERVER_ERROR,ValueConstant.SOMETHING_WENT_WRONG);

        } catch (CustomSystemException ex){
            ex.getMessage();
            ex.printStackTrace();
            return globalExceptionHandler.handleCustomSystemException(ex);
        } catch (Exception e){
            log.error("An error occurred: ", e);
            e.printStackTrace();
        }

        return CloudyUtils.getResponseEntityCustom( HttpStatus.INTERNAL_SERVER_ERROR,ValueConstant.SOMETHING_WENT_WRONG);
    }


    private boolean checkOtpExpire(LocalDateTime otpExpireAt, OtpAuth otpAuth){
        if (LocalDateTime.now().isAfter(otpExpireAt)){
            otpDao.delete(otpAuth);
            return true;
        }
        return false;
    }

    private boolean lockAcc(Integer attempt){
        if (attempt >= 3) return true;
        return false;
    }

}
