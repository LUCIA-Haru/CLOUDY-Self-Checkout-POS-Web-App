package com.Cloudy.Cloudy_Self_Checkout_POS.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FIleUploadResponse {
    private Long entityId;
    private String entity;
    private String fileName;
    private String filePath; // The URL of the uploaded file (e.g., "/uploads/customer/uuid3.jpg")
    private List<ImageDTO> imageDTOs = new ArrayList<>();

}
