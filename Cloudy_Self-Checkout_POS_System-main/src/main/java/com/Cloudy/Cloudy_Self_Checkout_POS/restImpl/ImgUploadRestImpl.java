package com.Cloudy.Cloudy_Self_Checkout_POS.restImpl;

import com.Cloudy.Cloudy_Self_Checkout_POS.constants.ValueConstant;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.FIleUploadResponse;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.ImageDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.rest.ImgUploadRest;
import com.Cloudy.Cloudy_Self_Checkout_POS.service.ImgUploadService;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.CloudyUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ImgUploadRestImpl implements ImgUploadRest {

    private final ImgUploadService imgUploadService;

    @Override
    public ResponseEntity<ApiResponseWrapper<FIleUploadResponse>> uploadImage(UserDetails userDetails, MultipartFile[] files,
                                                                                    String entity, Long id, List<Integer> priorities) {
        log.info("{} => {} =>  Subject: uploading Image ||| username::: {} ||| entity ::: {}", "ImgUploadRestImpl","uploadImage",
                userDetails.getUsername(),entity);
        try{
            return imgUploadService.uploadImg(userDetails.getUsername(),files,entity,id,priorities);

        }catch (Exception e){
            log.error("{}=>{}=> Error:::: {}", "ImgUploadRestImpl","uploadImage()",e.getMessage());
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG );
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<String>> deleteImg(UserDetails userDetails, String entityType, Long id, String imgUrl) {
        log.info("{} => {} =>  Subject: deleting Image ||| username::: {} ||| entity ::: {}", "ImgUploadRestImpl","deleteImg()",
                userDetails.getUsername(),entityType);
        try{
            return imgUploadService.deleteImg(userDetails.getUsername(),entityType,id,imgUrl);

        }catch (Exception e){
            log.error("{}=>{}=> Error:::: {}", "ImgUploadRestImpl","deleteImg()",e.getMessage());
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG );
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<List<ImageDTO>>> getImages( String entity, Long id) {
        log.info("{} => {} =>  Subject: getting Images  ||| entity ::: {}", "ImgUploadRestImpl","getImages()",entity);
        try{
            return imgUploadService.getImages(entity,id);

        }catch (Exception e){
            log.error("{}=>{}=> Error:::: {}", "ImgUploadRestImpl","getImages()",e.getMessage());
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG );
        }
    }

    @Override
    public ResponseEntity<Resource> getImageFile(UserDetails userDetails,String entity, Long id) {
        try {
            return imgUploadService.retrieveImg(userDetails.getUsername(),entity, id);

        } catch (Exception e) {
            log.error("{}=>{}=> Error:::: {}", "ImgUploadRestImpl", "deleteImg()", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<String>> updateMainImage(UserDetails userDetails, String entity, Long id, String imgUrl) {
        try {
            return imgUploadService.updateMainImage(userDetails.getUsername(),entity,id,imgUrl);

        } catch (Exception e) {
            log.error("{}=>{}=> Error:::: {}", "ImgUploadRestImpl", "updateMainImage()", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
