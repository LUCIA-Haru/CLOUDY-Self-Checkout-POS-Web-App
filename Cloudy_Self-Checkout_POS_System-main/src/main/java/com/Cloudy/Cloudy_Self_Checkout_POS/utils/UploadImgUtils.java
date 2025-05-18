package com.Cloudy.Cloudy_Self_Checkout_POS.utils;

import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UploadImgUtils {
    private static final String BASE_UPLOAD_DIR = "src/main/resources/uploads/";
    private static final String URL_PREFIX = "/uploads/"; //URL prefix for serving files

    private static final Set<String> ALLOWED_ENTITY_TYPES = new HashSet<>(
            Arrays.asList(
                    "employee","customer","product"
            )
    );

    private static final Set<String>  ALLOWED_EXTENSIONS = new HashSet<>(
            Arrays.asList(
                    ".jpg",".jpeg",".png",".gif"
            )
    );

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; //5MB

    public static String saveFile(MultipartFile file,String entityType) throws  CustomSystemException {
      try {
          validateUpload(file,entityType);
          String entityUploadDir = BASE_UPLOAD_DIR + entityType.toLowerCase()+"/";
          Path uploadPath = Paths.get(entityUploadDir).toAbsolutePath().normalize();

          if (!Files.exists(uploadPath)){
              Files.createDirectories(uploadPath);
          }

          String originalFileName = file.getOriginalFilename();
          String fileExtension = getFileExtension(originalFileName);

// Generate a shortened 10-character UUID
          String fullUuid = UUID.randomUUID().toString().replaceAll("-",""); // e.g., "550e8400-e29b-41d4-a716-446655440000"
          String uniqueFilename = fullUuid.substring(0, 10) + fileExtension; // e.g., "550e8400-e.jpg"

//          due to shortened 10 characters ...case-> uniques will be lost
          Path filePath = uploadPath.resolve(uniqueFilename);
          while (Files.exists(filePath)) {
              fullUuid = UUID.randomUUID().toString();
              uniqueFilename = fullUuid.substring(0, 10) + fileExtension;
              filePath = uploadPath.resolve(uniqueFilename);
          }
          Files.write(filePath,file.getBytes());

          // Return relative path for use in the application
          return URL_PREFIX + entityType.toLowerCase() + "/" + uniqueFilename;
      } catch (Exception e) {
          throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR.toString(),e.getCause());
      }


    }

    private static void validateUpload(MultipartFile file, String entityType) throws CustomSystemException {
      try {
          if (file.isEmpty())
              throw new IOException("Cannot upload empty files");

          if (!ALLOWED_ENTITY_TYPES.contains(entityType.toLowerCase()))
              throw new IOException("Invalid entity type:" + entityType);

          if (file.getSize() > MAX_FILE_SIZE)
              throw new IOException("File size exceeds maximnum limit of " + (MAX_FILE_SIZE / (1024 * 1024)) + "MB");

          String extension = getFileExtension(file.getOriginalFilename());
          if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
              throw new IOException("Invalid file type. Allowed types: " + ALLOWED_EXTENSIONS);
          }
      } catch (IOException e) {
          throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "An error occurred while reading the file",e.getMessage());
      }

    }

    private static String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".jpg";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    public static URI getFilePath(String entityType, String filename) {
        return Paths.get(BASE_UPLOAD_DIR + entityType.toLowerCase(), filename).toAbsolutePath().toUri();
    }

//    delete file in a specific entity folder
    public static void deleteSingleFile(String entityType, String filename) throws CustomSystemException {
        try{
            if (!ALLOWED_ENTITY_TYPES.contains(entityType.toLowerCase()))
                throw new IOException("Invalid entity type:" + entityType);

            Path filePath = Paths.get(getFilePath(entityType,filename));

            if (Files.exists(filePath) && Files.isRegularFile(filePath))
                Files.delete(filePath);
        } catch (IOException e) {
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "An error occurred while reading the file",e.getMessage());
        }
    }
//retrieve Img
    public static Resource retrieveFile(String imgUrl) throws CustomSystemException{
        try{
            // Validate imgUrl format (e.g., "/uploads/employee/unique-id.jpg")
            if (imgUrl == null || !imgUrl.startsWith(URL_PREFIX)) {
                throw new IOException("Invalid image URL format: " + imgUrl);
            }
            // Extract entity type and filename from imgUrl
            String relativePath = imgUrl.replace(URL_PREFIX, ""); // e.g., "employee/unique-id.jpg"
            String[] parts = relativePath.split("/", 2);
            if (parts.length != 2) {
                throw new IOException("Invalid image URL structure: " + imgUrl);
            }

            String entityType = parts[0].toLowerCase();
            String filename = parts[1];

            // Validate entity type
            if (!ALLOWED_ENTITY_TYPES.contains(entityType)) {
                throw new IOException("Invalid entity type in URL: " + entityType);
            }

            // Construct the full file path
            Path filePath = Paths.get(BASE_UPLOAD_DIR, entityType, filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            // Check if the file exists and is readable
            if (!resource.exists() || !resource.isReadable()) {
                throw new IOException("Image not found or not readable at path: " + filePath);
            }

            return resource;
        } catch (Exception e) {
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "An error occurred while reading the file",e.getMessage());
        }
    }
}
