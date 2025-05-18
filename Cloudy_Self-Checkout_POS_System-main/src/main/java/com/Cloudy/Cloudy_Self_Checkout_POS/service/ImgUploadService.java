package com.Cloudy.Cloudy_Self_Checkout_POS.service;

import com.Cloudy.Cloudy_Self_Checkout_POS.dto.FIleUploadResponse;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.ImageDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImgUploadService {
    ResponseEntity<ApiResponseWrapper<FIleUploadResponse>> uploadImg(String username,  MultipartFile[] files,
                                                                     String entity, Long id, List<Integer> priorities);
    ResponseEntity<ApiResponseWrapper<String>> deleteImg(String username,String entityType, Long id, String imgUrl);
    ResponseEntity<Resource> retrieveImg(String username,String entity,Long id);
    ResponseEntity<ApiResponseWrapper<List<ImageDTO>>> getImages( String entity, Long id);
    ResponseEntity<ApiResponseWrapper<String>> updateMainImage(
                                                                String username, String entity, Long id, String imgUrl);
}
