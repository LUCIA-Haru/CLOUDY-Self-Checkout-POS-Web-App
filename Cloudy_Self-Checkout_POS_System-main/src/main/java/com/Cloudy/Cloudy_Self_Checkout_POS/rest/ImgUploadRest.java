package com.Cloudy.Cloudy_Self_Checkout_POS.rest;

import com.Cloudy.Cloudy_Self_Checkout_POS.dto.FIleUploadResponse;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.ImageDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping(path = "/url")
public interface ImgUploadRest {

    @PostMapping("/{entity}/{id}")
    @PreAuthorize("hasAnyAuthority('STAFF', 'MANAGER', 'ADMIN', 'CUSTOMER')")
    ResponseEntity<ApiResponseWrapper<FIleUploadResponse>> uploadImage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("files") MultipartFile[] files,
            @PathVariable String entity,
            @PathVariable(required = false) Long id,
            @RequestParam(value = "priorities", required = false) List<Integer> priorities);


    @DeleteMapping("/{entity}/{id}")
    @PreAuthorize("hasAnyAuthority('STAFF', 'MANAGER', 'ADMIN', 'CUSTOMER')")
    ResponseEntity<ApiResponseWrapper<String>> deleteImg(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("entity") String entity,
            @PathVariable(required = false) Long id,
            @RequestParam(value = "imgUrl", required = false) String imgUrl);

    @GetMapping("/fetch/{id}/{entity}")
    ResponseEntity<ApiResponseWrapper<List<ImageDTO>>> getImages(
            @PathVariable String entity,
            @PathVariable(required = false) Long id);

//    @GetMapping("/file/by-url/{imgUrl}")
//    ResponseEntity<Resource> getImageByUrl(@PathVariable String imgUrl);

    @GetMapping("/{entity}/file/{id}")
    public ResponseEntity<Resource> getImageFile(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String entity,
            @PathVariable(required = false) Long id
    );

    @PutMapping("/{entity}/{id}/main")
    @PreAuthorize("hasAnyAuthority('STAFF', 'MANAGER', 'ADMIN')")
    ResponseEntity<ApiResponseWrapper<String>> updateMainImage(
                                                                @AuthenticationPrincipal UserDetails userDetails,
                                                                @PathVariable String entity,
                                                                @PathVariable Long id,
                                                                @RequestParam("imgUrl") String imgUrl);
}
