package com.Cloudy.Cloudy_Self_Checkout_POS.serviceImpl;


import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.*;
import com.Cloudy.Cloudy_Self_Checkout_POS.constants.ValueConstant;
import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.dao.*;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.DiscountDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.DiscountedProductDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.ImageDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.ProductDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.service.ProductService;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.CloudyUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.UserUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ListWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.PaginatedResponse;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    @Autowired
    UserUtils userUtils;

    private final  CategoryDao categoryDao;
    private final  StaffDao staffDao;
    private final   ProductDao productDao;
    private final DiscountDAO discountDAO;
    private  final BrandDAO brandDAO;
    private  final SupplierTransactionDAO supplierTransactionDAO;

    private static final String ProductServiceImpl = "ProductServiceImpl";
    private static final String SERVICE_NAME = "ProductServiceImpl";


    @Override
    public ResponseEntity<ApiResponseWrapper<ProductDTO>> addProduct(String username, ProductDTO requestBody) {
        try{
            Staff staff;
            User user = userUtils.getUserByUsernameOptional(username);


                staff = staffDao.findByUserId(user.getUserId());

                if (staff == null){
                    log.warn("Staff not found for userId: {}", user.getUserId());
                    return CloudyUtils.getResponseEntityCustom(HttpStatus.BAD_REQUEST,ValueConstant.STAFF_NOT_FOUND);
                }

    // Fetch the existing Category entity
                    Category category = categoryDao.findById(requestBody.getCategoryId())
                            .orElseThrow(() -> new CustomSystemException(HttpStatus.BAD_REQUEST,"Category Not Found"));

                    Brand brand = brandDAO.findById(requestBody.getBrandId())
                            .orElseThrow(() -> new CustomSystemException(HttpStatus.BAD_REQUEST,"Brand Not Found"));

            // Fetch or create the SupplierTransaction
            SupplierTransaction supplierTransaction = null;
            if (requestBody.getTransactionId() != null) { // Check if a transaction ID is provided
                supplierTransaction = supplierTransactionDAO.findById(requestBody.getTransactionId())
                        .orElseThrow(() -> new CustomSystemException(HttpStatus.BAD_REQUEST, "Supplier Transaction Not Found"));
            }


            // Generate product GUID
                String productGuid = UUID.randomUUID().toString();
                // Generate barcode using the product GUID
                String barcode = generateEAN13BarcodeFromGUID(productGuid);

                Product product = Product.builder()
                        .productGuid(productGuid)
                        .productName(requestBody.getProductName())
                        .productDesc(requestBody.getProductDesc())
                        .category(category)
                        .currency("$")
                        .brand(brand)
                        .price(requestBody.getPrice())
                        .stockUnit(requestBody.getStockUnit())
                        .hasDiscount(requestBody.getHasDiscount())
                        .expDate(requestBody.getExpDate())
                        .manuDate(requestBody.getManuDate())
                        .rating(requestBody.getRating())
                        .createdOn(LocalDateTime.now())
                        .createdBy(staff.getUsername())
                        .barcode(barcode)
                        .isActive(true)
                        .build();
                Product savedProduct = productDao.save(product);

               // Update the supplier transaction with the new product
            if (supplierTransaction != null) {
                supplierTransaction.getProducts().add(savedProduct); // Add the product to the transaction
                supplierTransactionDAO.save(supplierTransaction); // Save the updated transaction
            }


            ProductDTO responseBody = mapToDTO(savedProduct);

                return  CloudyUtils.getResponseEntityCustom(HttpStatus.OK,ValueConstant.ADD,responseBody);


        } catch (Exception e) {
            e.printStackTrace();

        }
        return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<ProductDTO>> getProductById(String username, Long productId) {
        log.info("{} => getProductById() => Subject: retrieving product by ID {} ||| username: {}", SERVICE_NAME, productId, username);

        try {
            // Validate user
            userUtils.getUserByUserName(username);

            // Fetch product by ID
            Product product = productDao.findById(productId)
                    .orElseThrow(() -> new CustomSystemException(HttpStatus.NOT_FOUND, ValueConstant.ID_NOT_FOUND));

            // Map entity to DTO
            ProductDTO productDTO = mapToDTO(product);

            log.info("✅ {} => getProductById() => Subject: retrieving product by ID {} || username: {}", SERVICE_NAME, productId, username);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.RETRIEVE, productDTO);

        } catch (CustomSystemException e) {
            log.error("{} => getProductById() => Subject: retrieving product by ID {} => Custom Error: {}", SERVICE_NAME, productId, e.getMessage());
            return CloudyUtils.getResponseEntityCustom(HttpStatus.BAD_REQUEST, e.getMessage());

        } catch (Exception e) {
            log.error("{} => getProductById() => Subject: retrieving product by ID {} => Unexpected Error: ", SERVICE_NAME, productId, e);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<ProductDTO>> updateProduct(String username, Long productId, ProductDTO requestBody) {
        log.info("{} => updateProduct() => Subject: updating product with ID {} ||| username: {}", SERVICE_NAME, productId, username);

        try {
            // Validate user
            userUtils.getUserByUserName(username);

            // Fetch existing product by ID
            Product existingProduct = productDao.findById(productId)
                    .orElseThrow(() -> new CustomSystemException(HttpStatus.NOT_FOUND, ValueConstant.ID_NOT_FOUND));

            // Update basic fields
            existingProduct.setProductName(requestBody.getProductName() != null ? requestBody.getProductName() : existingProduct.getProductName());
            existingProduct.setProductDesc(requestBody.getProductDesc() != null ? requestBody.getProductDesc() : existingProduct.getProductDesc());
            existingProduct.setPrice(requestBody.getPrice() != null ? requestBody.getPrice() : existingProduct.getPrice());
            existingProduct.setStockUnit(requestBody.getStockUnit() != null ? requestBody.getStockUnit() : existingProduct.getStockUnit());
            existingProduct.setHasDiscount(requestBody.getHasDiscount() != null ? requestBody.getHasDiscount() : existingProduct.isHasDiscount());
            existingProduct.setExpDate(requestBody.getExpDate() != null ? requestBody.getExpDate() : existingProduct.getExpDate());
            existingProduct.setManuDate(requestBody.getManuDate() != null ? requestBody.getManuDate() : existingProduct.getManuDate());
            existingProduct.setRating(requestBody.getRating() != null ? requestBody.getRating() : existingProduct.getRating());
            existingProduct.setUpdatedOn(LocalDateTime.now());
            existingProduct.setUpdatedBy(username);

            // Update category if provided
            if (requestBody.getCategoryId() != null) {
                Category category = categoryDao.findById(requestBody.getCategoryId())
                        .orElseThrow(() -> new CustomSystemException(HttpStatus.BAD_REQUEST, "Category Not Found"));
                existingProduct.setCategory(category);
            }

            // Update brand if provided
            if (requestBody.getBrandId() != null) {
                Brand brand = brandDAO.findById(requestBody.getBrandId())
                        .orElseThrow(() -> new CustomSystemException(HttpStatus.BAD_REQUEST, "Brand Not Found"));
                existingProduct.setBrand(brand);
            }

            // Only update supplier transaction if explicitly provided
            if (requestBody.getTransactionId() != null) {
                SupplierTransaction supplierTransaction = supplierTransactionDAO.findById(requestBody.getTransactionId())
                        .orElseThrow(() -> new CustomSystemException(HttpStatus.BAD_REQUEST, "Supplier Transaction Not Found"));
                existingProduct.setSupplierTransaction(supplierTransaction);
            }

            // Save updated product
            Product updatedProduct = productDao.save(existingProduct);

            // Map entity to DTO
            ProductDTO updatedProductDTO = mapToDTO(updatedProduct);

            log.info("✅ {} => updateProduct() => Subject: updating product with ID {} || username: {}", SERVICE_NAME, productId, username);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.UPDATE, updatedProductDTO);

        } catch (CustomSystemException e) {
            log.error("{} => updateProduct() => Subject: updating product with ID {} => Custom Error: {}", SERVICE_NAME, productId, e.getMessage());
            return CloudyUtils.getResponseEntityCustom(HttpStatus.BAD_REQUEST, e.getMessage());

        } catch (Exception e) {
            log.error("{} => updateProduct() => Subject: updating product with ID {} => Unexpected Error: ", SERVICE_NAME, productId, e);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<String>> deleteProduct(String username, Long productId) {
        log.info("{} => deleteProduct() => Subject: deleting product with ID {} ||| username: {}", SERVICE_NAME, productId, username);

        try {
            // Validate user
            userUtils.getUserByUserName(username);

            // Fetch product by ID
            Product product = productDao.findById(productId)
                    .orElseThrow(() -> new CustomSystemException(HttpStatus.NOT_FOUND, ValueConstant.ID_NOT_FOUND));

            // Delete product
            productDao.deleteById(product.getProductId());

            log.info("✅ {} => deleteProduct() => Subject: deleting product with ID {} || username: {}", SERVICE_NAME, productId, username);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.DELETE, ValueConstant.ACTION_SUCCESS);

        } catch (CustomSystemException e) {
            log.error("{} => deleteProduct() => Subject: deleting product with ID {} => Custom Error: {}", SERVICE_NAME, productId, e.getMessage());
            return CloudyUtils.getResponseEntityCustom(HttpStatus.BAD_REQUEST, e.getMessage());

        } catch (Exception e) {
            log.error("{} => deleteProduct() => Subject: deleting product with ID {} => Unexpected Error: ", SERVICE_NAME, productId, e);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<ListWrapper<DiscountedProductDTO>>> getsProductsOnDiscount() {
        log.info("{} => getsProductsOnDiscount() => Subject: retrieving all products on discount", SERVICE_NAME);

        try {
            LocalDate today = LocalDate.now();
            List<Product> products = productDao.findProductsOnDiscount(today);

            // Map products to DTOs with discount details
            List<DiscountedProductDTO> discountedProducts = new ArrayList<>();
            for (Product product : products) {
                for (Discount discount : product.getDiscounts()) {
                    if (discount.getStartDate().isBefore(today) && discount.getEndDate().isAfter(today)) {
                        DiscountedProductDTO dto = DiscountedProductDTO.builder()
                                .productId(product.getProductId())
                                .productName(product.getProductName())
                                .barcode(product.getBarcode())
                                .price(product.getPrice())
                                .currency(product.getCurrency())
                                .description(product.getProductDesc())
                                .discountValue(discount.getDiscountValue())
                                .isPercentage(discount.isPercentage())
                                .discountStartDate(discount.getStartDate())
                                .discountEndDate(discount.getEndDate())
                                .stockUnits(product.getStockUnit())
                                .imgUrls(product.getImages().stream()
                                        .map(img -> ImageDTO.builder()
                                                .imgUrl(img.getImgUrl())
                                                .priority(img.getPriority())
                                                .isMain(img.isMain())
                                                .build())
                                        .collect(Collectors.toList())) // assuming this returns List<ImageDTO>
                                .build();

                        discountedProducts.add(dto);
                    }
                }
            }

            // Wrap the response
            ListWrapper<DiscountedProductDTO> responseWrapper = new ListWrapper<>(discountedProducts, discountedProducts.size());

            log.info("✅ {} => getsProductsOnDiscount() => Successfully retrieved products on discount", SERVICE_NAME);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.RETRIEVE, responseWrapper);

        } catch (Exception e) {
            log.error("{} => getsProductsOnDiscount() => Subject: retrieving all products on discount => Unexpected Error: ", SERVICE_NAME, e);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }



    @Override
    public ResponseEntity<ApiResponseWrapper<PaginatedResponse<ProductDTO>>> getAllProducts(String filterValue, int page, int size) throws CustomSystemException {
        log.info("{} => getAllProducts() => Subject: retrieving all products ", SERVICE_NAME);

        try {


            // Validate pagination parameters
            if (page < 0 || size <= 0) {
                throw new IllegalArgumentException("Invalid pagination parameters");
            }

            // Create pageable object
            Pageable pageable = PageRequest.of(page, size);

            // Fetch all products
            Page<Product> productPage = productDao.findAll(pageable);

            // Map entities to DTOs
            List<ProductDTO> productDTOs = productPage.getContent().stream()
                    .map(this::safeMapToDTO) // Map each Product to ProductDTO
                    .filter(Objects::nonNull) // Filter out any null DTOs
                    .collect(Collectors.toList());

            // Build paginated response
            PaginatedResponse<ProductDTO> paginatedResponse = new PaginatedResponse<>(
                    productDTOs,
                    productPage.getTotalElements(),
                    productPage.getTotalPages(),
                    productPage.getNumber(),
                    productPage.getSize()
            );

            log.info("✅ {} => getAllProducts() => Subject: retrieving all products }", SERVICE_NAME);
            return CloudyUtils.getPaginatedResponseEntityCustom(HttpStatus.OK, ValueConstant.RETRIEVE, paginatedResponse);

        } catch (IllegalArgumentException e) {
            log.error("{} => getAllProducts() => Subject: retrieving all products => Invalid Input: {}", SERVICE_NAME, e.getMessage());
            throw new CustomSystemException(HttpStatus.BAD_REQUEST, ValueConstant.BAD_CREDENTIALS);

        } catch (Exception e) {
            log.error("{} => getAllProducts() => Subject: retrieving all products => Unexpected Error: ", SERVICE_NAME, e);
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResponseEntity<ApiResponseWrapper<ProductDTO>> findByBarcode(String barcode) {
        try{
            Product product = productDao.findByBarcode(barcode)
                    .orElseThrow(()-> new CustomSystemException(HttpStatus.BAD_REQUEST,ValueConstant.ID_NOT_FOUND));

            ProductDTO responseBody = mapToDTO(product);
            return  CloudyUtils.getResponseEntityCustom(HttpStatus.OK,ValueConstant.RETRIEVE,responseBody);

        }catch (Exception e){
            e.printStackTrace();
        }


        return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<Boolean>> validateStock(String barcode, Integer quantity) {
        try{
            // Validate input parameters
            if (StringUtils.isBlank(barcode)) {
                log.warn("Barcode is null or empty");
                return CloudyUtils.getResponseEntityCustom(HttpStatus.BAD_REQUEST, "Barcode cannot be null or empty");
            }

            if (quantity == null || quantity <= 0) {
                log.warn("Invalid quantity: {}", quantity);
                return CloudyUtils.getResponseEntityCustom(HttpStatus.BAD_REQUEST, "Quantity must be greater than zero");
            }

            log.info("Inside validaeStock function: barcode = {} quantity = {}", barcode,quantity);
            Product product = productDao.findByBarcode(barcode)
                    .orElseThrow(()-> new CustomSystemException(HttpStatus.BAD_REQUEST,ValueConstant.ID_NOT_FOUND));
            boolean isAvailable = product.getStockUnit() >= quantity;
            log.debug("Stock validation result | available={} stockUnit={} requiredQuantity={}",
                    isAvailable, product.getStockUnit(), quantity);

            return  CloudyUtils.getResponseEntityCustom(HttpStatus.OK, String.valueOf(isAvailable));

        } catch (CustomSystemException e) {
            // Handle custom exceptions (e.g., barcode not found)
            log.error("Custom error during stock validation | barcode={} error='{}'", barcode, e.getMessage());
            return CloudyUtils.getResponseEntityCustom(HttpStatus.BAD_REQUEST, e.getMessage());

        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////MapToDTO
    private ProductDTO mapToDTO(Product product) throws CustomSystemException {
        try {
            ProductDTO dto = new ProductDTO();
            dto.setProductId(product.getProductId());
            dto.setProductGuid(product.getProductGuid());
            dto.setProductName(product.getProductName());

            // Safely map category
            if (product.getCategory() != null) {
                dto.setCategoryId(product.getCategory().getCategoryId());
                dto.setCategory(product.getCategory().getCategoryName());
            }

            dto.setProductDesc(product.getProductDesc());

            // Safely map brand
            if (product.getBrand() != null) {
                dto.setBrandId(product.getBrand().getId());
                dto.setBrand(product.getBrand().getName());
            }

            if (product.getImages() != null && !product.getImages().isEmpty()) {
                List<ImageDTO> imageDTOs = product.getImages().stream()
                        .map(image -> ImageDTO.builder()
                                .imgUrl(image.getImgUrl())
                                .priority(image.getPriority())
                                .isMain(image.isMain())
                                .build())
                        .collect(Collectors.toList());
                dto.setImgUrls(imageDTOs);
            }

            dto.setPrice(product.getPrice());
            dto.setCurrency(product.getCurrency());
            dto.setStockUnit(product.getStockUnit());
            dto.setHasDiscount(product.isHasDiscount());
            dto.setExpDate(product.getExpDate());
            dto.setManuDate(product.getManuDate());
            dto.setRating(product.getRating());
            dto.setCreatedOn(product.getCreatedOn());
            dto.setCreatedBy(product.getCreatedBy());
            dto.setUpdatedOn(product.getUpdatedOn());
            dto.setUpdatedBy(product.getUpdatedBy());
            dto.setBarcode(product.getBarcode());

            // Safely map supplier transaction
            if (product.getSupplierTransaction() != null) {
                dto.setTransactionId(product.getSupplierTransaction().getTransactionId());
            }

            // Map discount if applicable
            if (product.isHasDiscount()) {
                Discount discount = retrieveDiscount(product.getProductId());
                if (discount != null) {
                    DiscountDTO discountDTO = DiscountDTO.builder()
                            .discountId(discount.getDiscountId())
                            .guid(discount.getGuid())
                            .discountValue(discount.getDiscountValue())
                            .isPercentage(discount.isPercentage())
                            .startDate(discount.getStartDate())
                            .endDate(discount.getEndDate())
                            .build();
                    dto.setDiscount(discountDTO);
                }
            }

            return dto;

        } catch (CustomSystemException e) {
            log.error("{} => mapToDTO() => Error mapping product with ID {}: {}", SERVICE_NAME, product.getProductId(), e.getMessage());
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR,ValueConstant.BAD_CREDENTIALS);
        } catch (Exception e) {
            log.error("{} => mapToDTO() => Unexpected error mapping product with ID {}: {}", SERVICE_NAME, product.getProductId(), e.getMessage());
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, "Error mapping product");
        }
    }

    private ProductDTO safeMapToDTO(Product product) {
        try {
            return mapToDTO(product);
        } catch (CustomSystemException e) {
            log.error("Error mapping product with ID {}: {}", product.getProductId(), e.getMessage());
            return null; // Skip this product
        }
    }

//    check discount -> retrieve
    private Discount retrieveDiscount(Long productId) throws CustomSystemException {
        try {
            Discount discount = discountDAO.findByProductId(productId).orElseThrow(
                    () -> new CustomSystemException(HttpStatus.BAD_REQUEST, ValueConstant.ID_NOT_FOUND)
            );
            log.info("{} => {} => Reason : Retrieving Discount by ProductId ::: {} ", ProductServiceImpl, "retrieveDiscount", productId);

            return discount;
        }catch (CustomSystemException e) {
            // Handle custom exceptions (e.g., barcode not found)
            log.error("{} => {} => Retrieving Discount by ProductId ::: {}, error ::{}", ProductServiceImpl, "retrieveDiscount", productId,e.getMessage());
            throw e;

        }

    }

//    generate barcode
private String generateEAN13BarcodeFromGUID(String guid) {
    StringBuilder barcode = new StringBuilder();

    // Use the first 12 characters of the GUID as the barcode base
    for (int i = 0; i < 12; i++) {
        char ch = guid.charAt(i);
        int digit = Character.isDigit(ch) ? Character.getNumericValue(ch) : ch % 10;
        barcode.append(digit);
    }

    // Calculate checksum (13th digit)
    int checksum = calculateEAN13Checksum(barcode.toString());
    barcode.append(checksum);

    return barcode.toString();
}
    private int calculateEAN13Checksum(String barcode) {
        int sum = 0;
        for (int i = 0; i < barcode.length(); i++) {
            int digit = Character.getNumericValue(barcode.charAt(i));
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        return (10 - (sum % 10)) % 10;
    }


}
