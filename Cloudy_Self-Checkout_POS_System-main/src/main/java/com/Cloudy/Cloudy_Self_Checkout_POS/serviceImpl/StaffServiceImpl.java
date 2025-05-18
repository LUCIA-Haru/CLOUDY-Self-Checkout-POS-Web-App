package com.Cloudy.Cloudy_Self_Checkout_POS.serviceImpl;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Brand;
import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Staff;
import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.User;
import com.Cloudy.Cloudy_Self_Checkout_POS.constants.ValueConstant;
import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.dao.CustomerDao;
import com.Cloudy.Cloudy_Self_Checkout_POS.dao.StaffDao;
import com.Cloudy.Cloudy_Self_Checkout_POS.dao.UserDao;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.RegisterationRequestBody;
import com.Cloudy.Cloudy_Self_Checkout_POS.service.StaffService;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.CloudyUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.UserUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.CustomerWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.PaginatedResponse;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ResponseBody.UserResponseBody;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.StaffWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service

public class StaffServiceImpl implements StaffService {
    @Autowired
    CustomerDao customerDao;

    @Autowired
    UserDao userDao;

    @Autowired
    StaffDao staffDao;

    @Autowired
    UserUtils userUtils;

    @Autowired
    PasswordEncoder passwordEncoder;

    private static final String SERVICE_NAME = "StaffServiceImpl";


    @Override
    public ResponseEntity<ApiResponseWrapper<List<CustomerWrapper>>> getAllCustomers(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<CustomerWrapper> customers = customerDao.getAllCustomers();
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.ACTION_SUCCESS,customers);
        }catch (Exception e){
            e.printStackTrace();
        }
        return CloudyUtils.getResponseEntityCustom( HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<PaginatedResponse<StaffWrapper>>> getAllStaff(String username, String filterValue, int page, int size) throws CustomSystemException {
        log.info("{} => getAllStaff() => Subject: retrieving all staff ||| username: {}", SERVICE_NAME, username);

        try {
            // Validate user
            userUtils.getUserByUserName(username);
            Page<Staff> staffPage = null;

            Pageable pageable = PageRequest.of(page,size);

            // Fetch brands based on filterValue
            if ("false".equalsIgnoreCase(filterValue)) {
                staffPage = staffDao.findByInactive(pageable);
            } else {
                staffPage = staffDao.findAll(pageable);
            }

            // Map entities to DTOs
            List<StaffWrapper> staffWrappers = staffPage.getContent().stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());

            // Build paginated response
            PaginatedResponse<StaffWrapper> paginatedResponse = new PaginatedResponse<>(
                    staffWrappers,
                    staffPage.getTotalElements(),
                    staffPage.getTotalPages(),
                    staffPage.getNumber(),
                    staffPage.getSize()
            );

            log.info("✅ {} => getAllStaff() => Subject: retrieving all staff || username: {}", SERVICE_NAME, username);
            return CloudyUtils.getPaginatedResponseEntityCustom(HttpStatus.OK, ValueConstant.RETRIEVE, paginatedResponse);

        } catch (Exception e) {
            log.error("{} => getAllStaff() => Subject: retrieving all staff => Unexpected Error: ", SERVICE_NAME, e);
          throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR,ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<StaffWrapper>> getStaffById(String username) {
        log.info("{} => getStaffById() => Subject: retrieving staff by ID {} ||| username: {}", SERVICE_NAME, username);

        try {
            // Validate user
           User user =  userUtils.getUserByUsernameOptional(username);

            // Fetch staff by ID
            Staff staff = staffDao.findById(user.getUserId())
                    .orElseThrow(() -> new CustomSystemException(HttpStatus.NOT_FOUND, ValueConstant.ID_NOT_FOUND));

            // Map entity to DTO
            StaffWrapper staffWrapper = mapToDTO(staff);

            log.info("✅ {} => getStaffById() => Subject: retrieving staff by ID {} || username: {}", SERVICE_NAME, username);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.RETRIEVE, staffWrapper);

        } catch (CustomSystemException e) {
            log.error("{} => getStaffById() => Subject: retrieving staff by ID {} => Custom Error: {}", SERVICE_NAME,  e.getMessage());
            return CloudyUtils.getResponseEntityCustom(HttpStatus.BAD_REQUEST, e.getMessage());

        } catch (Exception e) {
            log.error("{} => getStaffById() => Subject: retrieving staff by ID {} => Unexpected Error: ", SERVICE_NAME,  e);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<StaffWrapper>> updateStaff(String username, Long id, StaffWrapper staffWrapper) {
        log.info("{} => updateStaff() => Subject: updating staff with ID {} ||| username: {}", SERVICE_NAME, id, username);

        try {
            // Validate user
         User user =   userUtils.getUserByUsernameOptional(username);
            Staff existingStaff = null;

            // Fetch existing staff by ID
            if (id != null) {
               existingStaff = staffDao.findById(id)
                        .orElseThrow(() -> new CustomSystemException(HttpStatus.NOT_FOUND, ValueConstant.ID_NOT_FOUND));
            }else{
                existingStaff = staffDao.findByUserId(user.getUserId());
            }
            // Update fields
            existingStaff.setFirstName(staffWrapper.getFirstName() != null ? staffWrapper.getFirstName() : existingStaff.getFirstName());
            existingStaff.setLastName(staffWrapper.getLastName() != null ? staffWrapper.getLastName() : existingStaff.getLastName());
            existingStaff.setEmail(staffWrapper.getEmail() != null ? staffWrapper.getEmail() : existingStaff.getEmail());
            existingStaff.setAddress(staffWrapper.getAddress() != null ? staffWrapper.getAddress() : existingStaff.getAddress());
            existingStaff.setDob(staffWrapper.getDob() != null ? staffWrapper.getDob() : existingStaff.getDob());
            existingStaff.setPhoneNo(staffWrapper.getPhoneNo() != null ? staffWrapper.getPhoneNo() : existingStaff.getPhoneNo());
            existingStaff.setDepartment(staffWrapper.getDepartment() != null ? staffWrapper.getDepartment() : existingStaff.getDepartment());
            existingStaff.setPosition(staffWrapper.getPosition() != null ? staffWrapper.getPosition() : existingStaff.getPosition());
            existingStaff.setStatus(staffWrapper.getStatus() != null ? staffWrapper.getStatus() : existingStaff.getStatus());

            existingStaff.setUpdatedOn(LocalDateTime.now());

            // Save updated staff
            Staff updatedStaff = staffDao.save(existingStaff);

            // Map entity to DTO
            StaffWrapper updatedStaffWrapper = mapToDTO(updatedStaff);

            log.info("✅ {} => updateStaff() => Subject: updating staff with ID {} || username: {}", SERVICE_NAME, id, username);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.UPDATE, updatedStaffWrapper);

        } catch (CustomSystemException e) {
            log.error("{} => updateStaff() => Subject: updating staff with ID {} => Custom Error: {}", SERVICE_NAME, id, e.getMessage());
            return CloudyUtils.getResponseEntityCustom(HttpStatus.BAD_REQUEST, e.getMessage());

        } catch (Exception e) {
            log.error("{} => updateStaff() => Subject: updating staff with ID {} => Unexpected Error: ", SERVICE_NAME, id, e);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<String>> deleteStaff(String username, Long id) {
        log.info("{} => deleteStaff() => Subject: deleting staff with ID {} ||| username: {}", SERVICE_NAME, id, username);

        try {
            // Validate user
            userUtils.getUserByUserName(username);

            // Fetch staff by ID
            Staff staff = staffDao.findById(id)
                    .orElseThrow(() -> new CustomSystemException(HttpStatus.NOT_FOUND, ValueConstant.ID_NOT_FOUND));

            // Delete staff
            staffDao.deleteById(staff.getUserId());

            log.info("✅ {} => deleteStaff() => Subject: deleting staff with ID {} || username: {}", SERVICE_NAME, id, username);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.DELETE, ValueConstant.ACTION_SUCCESS);

        } catch (CustomSystemException e) {
            log.error("{} => deleteStaff() => Subject: deleting staff with ID {} => Custom Error: {}", SERVICE_NAME, id, e.getMessage());
            return CloudyUtils.getResponseEntityCustom(HttpStatus.BAD_REQUEST, e.getMessage());

        } catch (Exception e) {
            log.error("{} => deleteStaff() => Subject: deleting staff with ID {} => Unexpected Error: ", SERVICE_NAME, id, e);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<UserResponseBody>> staffSignUp(RegisterationRequestBody requestBody, String username) {
        log.info("Inside SignUP {}",requestBody);
        try {
            // Check if user already exists
            if (userUtils.getUserByEmail(requestBody.getEmail()).isPresent())
                return CloudyUtils.getResponseEntityCustom(HttpStatus.BAD_REQUEST,ValueConstant.USEER_EXIST);

            User createBy= userUtils.getUserByUsernameOptional(username);


            Staff staff = Staff.builder()
                    .username(requestBody.getUsername())
                    .email(requestBody.getEmail())
                    .password(passwordEncoder.encode(requestBody.getPassword()))
                    .firstName(requestBody.getFirstName())
                    .lastName(requestBody.getLastName())
                    .guid(UUID.randomUUID().toString())
                    .createdBy(createBy.getUsername())
                    .department(requestBody.getDepartment())
                    .address(requestBody.getAddress())
                    .phoneNo(requestBody.getPhoneNo())
                    .dob(requestBody.getDob())
                    .status(true)
                    .role(requestBody.getRole().toUpperCase())
                    .position(requestBody.getPosition())
                    .createdOn(LocalDateTime.now())
                    .build();

            staffDao.save(staff);

            UserResponseBody newResponse = new UserResponseBody(
                    staff.getUserId(),
                    staff.getGuid(),
                    staff.getUsername(),
                    staff.getFirstName(),
                    staff.getLastName(),
                    staff.getEmail(),
                    staff.getCreatedOn(),
                    staff.getRole(),
                    staff.getStatus(),
                    staff.getDepartment(),
                    staff.getPosition(),
                    staff.getCreatedBy(),
                    staff.getAddress(),
                    staff.getDob(),
                    staff.getPhoneNo()
            );
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK,ValueConstant.SAVE,newResponse);

        }catch (Exception e){
            log.error("An error occurred: ", e.getMessage());
            e.printStackTrace();
        }

        return CloudyUtils.getResponseEntityCustom( HttpStatus.INTERNAL_SERVER_ERROR,ValueConstant.SOMETHING_WENT_WRONG);
    }


    //    /////////////////////////////////////////////////////////
// Helper method to map Staff entity to StaffWrapper DTO
private StaffWrapper mapToDTO(Staff staff) {
    return StaffWrapper.builder()
            .profilePhoto(staff.getProfilePhoto())
            .user_id(staff.getUserId())
            .guid(staff.getGuid())
            .username(staff.getUsername())
            .firstName(staff.getFirstName())
            .lastName(staff.getLastName())
            .email(staff.getEmail())
            .address(staff.getAddress())
            .dob(staff.getDob())
            .phoneNo(staff.getPhoneNo())
            .department(staff.getDepartment())
            .position(staff.getPosition())
            .CreatedOn(staff.getCreatedOn())
            .createdBy(staff.getCreatedBy())
            .updatedOn(staff.getUpdatedOn())
            .status(staff.getStatus())
            .role(staff.getRole())
            .build();
}

}
