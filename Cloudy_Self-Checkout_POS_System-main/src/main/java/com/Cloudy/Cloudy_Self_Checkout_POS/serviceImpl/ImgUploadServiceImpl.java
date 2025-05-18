package com.Cloudy.Cloudy_Self_Checkout_POS.serviceImpl;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Product;
import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.ProductImages;
import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.User;
import com.Cloudy.Cloudy_Self_Checkout_POS.constants.ValueConstant;
import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.dao.ProductDao;
import com.Cloudy.Cloudy_Self_Checkout_POS.dao.ProductImagesDAO;
import com.Cloudy.Cloudy_Self_Checkout_POS.dao.UserDao;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.FIleUploadResponse;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.ImageDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.service.ImgUploadService;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.CloudyUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.UploadImgUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.UserUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImgUploadServiceImpl implements ImgUploadService {
    private final UserUtils userUtils;
    private final ProductDao productDao;
    private final UserDao userDao;
    private final ProductImagesDAO productImagesDAO;

    @Override
    public ResponseEntity<ApiResponseWrapper<FIleUploadResponse>> uploadImg(
            String username, MultipartFile[] files, String entity, Long id, List<Integer> priorities) {
        log.info("{} => {} => Subject: uploading Images ||| username::: {} ||| entity ::: {} ||| id::: {}",
                "ImgUploadServiceImpl", "uploadImg()", username, entity, id);
        try {
            User user = userUtils.getUserByUsernameOptional(username);
            String normalizedEntity = entity.toUpperCase();
            FIleUploadResponse response = new FIleUploadResponse();
            response.setEntity(entity);
            response.setEntityId(id);

            if (normalizedEntity.equals("PRODUCT")) {
                Product product = productDao.findById(id).orElseThrow(
                        () -> new CustomSystemException(HttpStatus.BAD_REQUEST, ValueConstant.Product_ERROR_002)
                );

                if (priorities != null && priorities.size() != files.length) {
                    throw new CustomSystemException(HttpStatus.BAD_REQUEST,
                            "Number of priorities must match number of files");
                }
                // Get current max priority from existing images
                int maxExistingPriority = product.getImages().stream()
                        .mapToInt(ProductImages::getPriority)
                        .max()
                        .orElse(-1); // -1 if no images yet

                // Check if there's an existing main image
                boolean hasMainImage = product.getImages().stream().anyMatch(ProductImages::isMain);


                for (int i = 0; i < files.length; i++) {
                    String fileUrl = UploadImgUtils.saveFile(files[i], entity.toLowerCase());
                    ProductImages image = new ProductImages();
                    image.setProduct(product);
                    image.setImgUrl(fileUrl);
                    int newPriority = priorities != null
                            ? priorities.get(i)
                            : maxExistingPriority + 1 + i;
                    image.setPriority(newPriority);
                    // Set isMain = true for the first image if no main exists, false otherwise
                    image.setMain(!hasMainImage && i == 0);
                    product.getImages().add(image);

                    ImageDTO imgDto = ImageDTO.builder()
                            .imgUrl(image.getImgUrl())
                            .priority(image.getPriority())
                            .isMain(image.isMain())
                            .build();
                    response.getImageDTOs().add(imgDto);

                    // Update hasMainImage after setting the first image as main
                    if (image.isMain()) {
                        hasMainImage = true;
                    }
                }
                productDao.save(product);

            } else if (normalizedEntity.equals("CUSTOMER") || normalizedEntity.equals("EMPLOYEE")) {
                if (files.length > 1) {
                    throw new CustomSystemException(HttpStatus.BAD_REQUEST, "Only one file allowed for user");
                }
                String fileUrl = UploadImgUtils.saveFile(files[0], entity.toLowerCase());
                User targetUser = userDao.findById(id).orElseThrow(
                        () -> new CustomSystemException(HttpStatus.NOT_FOUND, "User not found")
                );
                if (!targetUser.getUsername().equals(username)) {
                    throw new CustomSystemException(HttpStatus.FORBIDDEN, "Cannot modify another user's profile photo");
                }
                targetUser.setProfilePhoto(fileUrl);
                userDao.save(targetUser);

                ImageDTO imgDto = ImageDTO.builder()
                        .imgUrl(fileUrl)
                        .priority(0)
                        .isMain(true) // Always main for user
                        .build();
                response.getImageDTOs().add(imgDto);

            } else {
                throw new CustomSystemException(HttpStatus.BAD_REQUEST, "Unsupported entity type: " + entity);
            }

            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, "Images uploaded successfully", response);

        } catch (Exception e) {
            log.error("{}=>{}=> Error:::: {}", "ImgUploadServiceImpl", "uploadImg()", e.getMessage());
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<String>> deleteImg(
            String username, String entityType, Long id, String imgUrl) {
        log.info("{} => {} => Subject: deleting Image ||| username::: {} ||| entity ::: {} ||| id::: {}",
                "ImgUploadServiceImpl", "deleteImg()", username, entityType, id);
        try {
            User user = userUtils.getUserByUsernameOptional(username);
            String normalizedEntity = entityType.toUpperCase();

            if (normalizedEntity.equals("PRODUCT")) {
                Product product = productDao.findById(id).orElseThrow(
                        () -> new CustomSystemException(HttpStatus.BAD_REQUEST, ValueConstant.Product_ERROR_002)
                );
                ProductImages imageToDelete = product.getImages().stream()
                        .filter(img -> img.getImgUrl().equals(imgUrl))
                        .findFirst()
                        .orElseThrow(() -> new CustomSystemException(HttpStatus.BAD_REQUEST, "Image not found"));

                boolean wasMainImage = imageToDelete.isMain();
                product.getImages().remove(imageToDelete);
                String filename = imgUrl.substring(imgUrl.lastIndexOf("/") + 1);
                UploadImgUtils.deleteSingleFile(entityType.toLowerCase(), filename);

                // If deleted image was main, set another as main
                if (wasMainImage && !product.getImages().isEmpty()) {
                    ProductImages nextImage = product.getImages().stream()
                            .min(Comparator.comparingInt(ProductImages::getPriority))
                            .get();
                    nextImage.setMain(true);
                }

                productDao.save(product);
            } else if (normalizedEntity.equals("CUSTOMER") || normalizedEntity.equals("EMPLOYEE")) {
                User targetUser = userUtils.getUserByUsernameOptional(username);

                if (!targetUser.getUsername().equals(username)) {
                    throw new CustomSystemException(HttpStatus.FORBIDDEN, "Cannot delete another user's profile photo");
                }
                if (targetUser.getProfilePhoto() == null || !targetUser.getProfilePhoto().equals(imgUrl)) {
                    throw new CustomSystemException(HttpStatus.BAD_REQUEST, "Image not found for user");
                }
                String filename = imgUrl.substring(imgUrl.lastIndexOf("/") + 1);
                UploadImgUtils.deleteSingleFile(entityType.toLowerCase(), filename);
                targetUser.setProfilePhoto(null);
                userDao.save(targetUser);
            } else {
                throw new CustomSystemException(HttpStatus.BAD_REQUEST, "Unsupported entity type: " + entityType);
            }

            log.info("✅ {} => {} => Subject: delete image successfully || username::{} ||| imgUrl ::: {} ||| entityType:::{}",
                    "ImgUploadServiceImpl", "deleteImg()", username, imgUrl, entityType);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, "Image deleted successfully", imgUrl);

        } catch (Exception e) {
            log.error("{}=>{}=> Error:::: {}", "ImgUploadServiceImpl", "deleteImg()", e.getMessage());
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<Resource> retrieveImg(String username, String entity, Long id) {
        log.info("{} => {} => Subject: retrieveImg ||| username::: {} ||| entity ::: {} ||| id::: {}",
                "ImgUploadServiceImpl", "retrieveImg()", username, entity, id);
        try {
            String normalizedEntity = entity.toUpperCase();
            User user = userUtils.getUserByUsernameOptional(username);
            String imgUrl = null;

            if (normalizedEntity.equals("PRODUCT")) {
                Product product = productDao.findById(id).orElseThrow(
                        () -> new CustomSystemException(HttpStatus.BAD_REQUEST, ValueConstant.Product_ERROR_002)
                );
                // Get the main image (isMain = true) or throw if none exist
                ProductImages mainImage = product.getImages().stream()
                        .filter(ProductImages::isMain)
                        .findFirst()
                        .orElseThrow(() -> new CustomSystemException(HttpStatus.BAD_REQUEST, "No main image found for product"));
                imgUrl = mainImage.getImgUrl();
            } else if (normalizedEntity.equals("CUSTOMER") || normalizedEntity.equals("EMPLOYEE")) {
                User targetUser = userUtils.getUserByUsernameOptional(username);
                if (!targetUser.getUsername().equals(username)) {
                    throw new CustomSystemException(HttpStatus.FORBIDDEN, "Cannot access another user's image");
                }
                imgUrl = targetUser.getProfilePhoto();
                if (imgUrl == null) {
                    throw new CustomSystemException(HttpStatus.BAD_REQUEST, "No profile photo found for user");
                }
            } else {
                throw new CustomSystemException(HttpStatus.BAD_REQUEST, "Unsupported entity type: " + entity);
            }

            Resource resource = UploadImgUtils.retrieveFile(imgUrl);
            String contentType = getContentType(imgUrl);
            String filename = resource.getFilename();

            log.info("✅ {} => {} => Subject: retrieveImg successfully || username::{} ||| filename ::: {} ||| entityType:::{}",
                    "ImgUploadServiceImpl", "retrieveImg()", username, filename, entity);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (CustomSystemException e) {
            log.error("{}=>{}=> Error:::: {}", "ImgUploadServiceImpl", "retrieveImg()", e.getMessage());
            return ResponseEntity.status(Integer.parseInt(e.getErrorCode())).build(); // Fixed error code handling
        } catch (Exception e) {
            log.error("{}=>{}=> Error:::: {}", "ImgUploadServiceImpl", "retrieveImg()", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<List<ImageDTO>>> getImages( String entity, Long id) {
        log.info("{} => {} => Subject: getImages ||| entity ::: {} ||| id::: {}",
                "ImgUploadServiceImpl", "getImages()",  entity, id);
        try {
            String normalizedEntity = entity.toUpperCase();

            List<ImageDTO> imageDTOs = new ArrayList<>();

            if (normalizedEntity.equals("PRODUCT")) {
                Product product = productDao.findById(id).orElseThrow(
                        () -> new CustomSystemException(HttpStatus.BAD_REQUEST, ValueConstant.Product_ERROR_002)
                );
                product.getImages().stream()
                        .sorted(Comparator.comparingInt(ProductImages::getPriority))
                        .forEach(img -> imageDTOs.add(new ImageDTO(
                                img.getImgUrl(),
                                img.getPriority(),
                                img.isMain() // Use isMain directly
                        )));
            } else if (normalizedEntity.equals("CUSTOMER") || normalizedEntity.equals("EMPLOYEE")) {
                User targetUser = userDao.findById(id).orElseThrow(()-> new CustomSystemException(HttpStatus.BAD_REQUEST,ValueConstant.BAD_CREDENTIALS));
//                if (!targetUser.getUsername().equals(username)) {
//                    throw new CustomSystemException(HttpStatus.FORBIDDEN, "Cannot access another user's images");
//                }
                String imgUrl = targetUser.getProfilePhoto();
                if (imgUrl != null) {
                    imageDTOs.add(new ImageDTO(
                            imgUrl,
                            0,
                            true // Single user photo is always main
                    ));
                }
            } else {
                throw new CustomSystemException(HttpStatus.BAD_REQUEST, "Unsupported entity type: " + entity);
            }

            if (imageDTOs.isEmpty()) {
                return CloudyUtils.getResponseEntityCustom(HttpStatus.BAD_REQUEST, "No images found for " + entity);
            }

            log.info("✅ {} => {} => Subject: getImages successfully ||  entityType:::{}",
                    "ImgUploadServiceImpl", "getImages()", entity);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, "Images retrieved successfully", imageDTOs);

        } catch (Exception e) {
            log.error("{}=>{}=> Error:::: {}", "ImgUploadServiceImpl", "getImages()", e.getMessage());
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<String>> updateMainImage(String username, String entity, Long id, String imgUrl) {
        log.info("{} => {} => Subject: updating main image ||| username::: {} ||| entity ::: {} ||| id::: {} ||| imgUrl::: {}",
                "ImgUploadServiceImpl", "updateMainImage()", username, entity, id, imgUrl);
        try {
            User user = userUtils.getUserByUsernameOptional(username);
            String normalizedEntity = entity.toUpperCase();

            if (normalizedEntity.equals("PRODUCT")) {
                Product product = productDao.findById(id).orElseThrow(
                        () -> new CustomSystemException(HttpStatus.BAD_REQUEST, ValueConstant.Product_ERROR_002)
                );

                // Find the image to set as main
                ProductImages newMainImage = product.getImages().stream()
                        .filter(img -> img.getImgUrl().equals(imgUrl))
                        .findFirst()
                        .orElseThrow(() -> new CustomSystemException(HttpStatus.BAD_REQUEST, "Image not found"));

                // Reset all images to isMain = false
                product.getImages().forEach(img -> img.setMain(false));

                // Set the selected image as main
                newMainImage.setMain(true);

                productDao.save(product);

                log.info("✅ {} => {} => Subject: main image updated successfully || username::{} ||| imgUrl ::: {} ||| entityType:::{}",
                        "ImgUploadServiceImpl", "updateMainImage()", username, imgUrl, entity);
                return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, "Main image updated successfully", imgUrl);

            } else if (normalizedEntity.equals("CUSTOMER") || normalizedEntity.equals("EMPLOYEE")) {
                // For USER, since there's only one profile photo, no update is needed
                User targetUser = userUtils.getUserByUsernameOptional(username);
                if (!targetUser.getUsername().equals(username)) {
                    throw new CustomSystemException(HttpStatus.FORBIDDEN, "Cannot modify another user's profile photo");
                }
                if (targetUser.getProfilePhoto() == null || !targetUser.getProfilePhoto().equals(imgUrl)) {
                    throw new CustomSystemException(HttpStatus.BAD_REQUEST, "Image not found for user");
                }
                // No action needed since there's only one image, and it's inherently "main"
                log.info("✅ {} => {} => Subject: main image updated (no change needed) || username::{} ||| imgUrl ::: {} ||| entityType:::{}",
                        "ImgUploadServiceImpl", "updateMainImage()", username, imgUrl, entity);
                return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, "Profile photo is already the main image", imgUrl);

            } else {
                throw new CustomSystemException(HttpStatus.BAD_REQUEST, "Unsupported entity type: " + entity);
            }

        } catch (Exception e) {
            log.error("{}=>{}=> Error:::: {}", "ImgUploadServiceImpl", "updateMainImage()", e.getMessage());
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    private String getContentType(String imgUrl) {
        if (imgUrl.endsWith(".png")) {
            return "image/png";
        } else if (imgUrl.endsWith(".jpg") || imgUrl.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (imgUrl.endsWith(".gif")) {
            return "image/gif";
        }
        return "application/octet-stream"; // Fallback
    }
}