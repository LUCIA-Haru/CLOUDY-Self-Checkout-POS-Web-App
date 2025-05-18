package com.Cloudy.Cloudy_Self_Checkout_POS.utils;

import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.CartItemDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.CheckoutDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PdfUtils {

    @Value("${pdf.storage.path:./pdfs}")
    private String pdfStoragePath;

    private static final String URL_PREFIX = "/pdfs/";
    private static final String MAPPING_FILE = "user_pdfs.json";

    // In-memory map to store username-to-PDF mappings
    private static final Map<String, List<String>> userPdfs = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Object fileLock = new Object(); // For thread-safe file operations

    @PostConstruct
    public void init() {
        // Load mappings from JSON file on application startup
        try {
            Path mappingFilePath = Paths.get(pdfStoragePath, MAPPING_FILE);
            if (Files.exists(mappingFilePath)) {
                Map<String, List<String>> loadedMappings = objectMapper.readValue(
                        mappingFilePath.toFile(),
                        objectMapper.getTypeFactory().constructMapType(Map.class, String.class, List.class)
                );
                userPdfs.putAll(loadedMappings);
                log.info("Loaded {} user PDF mappings from {}", userPdfs.size(), mappingFilePath);
            } else {
                log.info("No existing PDF mappings found at {}. Starting with empty map.", mappingFilePath);
            }
        } catch (Exception e) {
            log.error("Error loading PDF mappings from JSON file: ", e);
        }
    }

    private void saveMappingsToFile() {
        // Save userPdfs map to JSON file
        try {
            synchronized (fileLock) {
                Path mappingFilePath = Paths.get(pdfStoragePath, MAPPING_FILE);
                // Ensure the storage directory exists
                Files.createDirectories(mappingFilePath.getParent());
                objectMapper.writeValue(mappingFilePath.toFile(), userPdfs);
                log.info("Saved PDF mappings to {}", mappingFilePath);
            }
        } catch (Exception e) {
            log.error("Error saving PDF mappings to JSON file: ", e);
        }
    }

    public String generateVoucherPDF(CheckoutDTO checkoutData) throws CustomSystemException {
        log.info("PdfService => generateVoucherPDF() with checkoutData: {}", checkoutData);
        if (checkoutData == null) {
            throw new CustomSystemException(HttpStatus.BAD_REQUEST.toString(), "Checkout data is null");
        }
        if (checkoutData.getCartItems() == null || checkoutData.getCartItems().isEmpty()) {
            throw new CustomSystemException(HttpStatus.BAD_REQUEST.toString(), "Cart items are missing");
        }

        Document document = null;
        PdfDocument pdf = null;
        PdfWriter writer = null;
        String filename = null;

        try {
            // Ensure the storage directory exists
            Path uploadPath = Paths.get(pdfStoragePath).toAbsolutePath().normalize();
            if (!Files.exists(uploadPath)) {
                log.info("Creating directory: {}", uploadPath);
                Files.createDirectories(uploadPath);
            }

            // Get the username (default to "anonymous" if missing)
            String username = checkoutData.getUsername() != null ? checkoutData.getUsername() : "anonymous";

            // Generate a unique filename: voucher-<username>-<purchaseId or timestamp>.pdf
            String uniqueIdentifier = checkoutData.getPurchaseId() != null
                    ? checkoutData.getPurchaseId().toString()
                    : LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            filename = String.format("voucher-%s-%s.pdf", username, uniqueIdentifier);
            String filePath = uploadPath.resolve(filename).toString();
            log.info("Writing PDF to: {}", filePath);

            // Create PDF (unchanged PDF generation logic)
            writer = new PdfWriter(filePath);
            pdf = new PdfDocument(writer);
            document = new Document(pdf);

            // Title
            document.add(new Paragraph("Purchase Voucher")
                    .setFontSize(22)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER));

            // Customer
            document.add(new Paragraph("Customer: " + username)
                    .setFontSize(16)
                    .setBold()
                    .setMarginTop(10)
                    .setMarginBottom(20));

            // Purchase Details (abridged for brevity; use your existing logic)
            document.add(new Paragraph("Purchase Details")
                    .setFontSize(16)
                    .setBold()
                    .setMarginBottom(10));

            Table purchaseTable = new Table(new float[]{2, 2, 1, 1, 1});
            purchaseTable.addHeaderCell(new Cell().add(new Paragraph("Item Name").setBold()));
            purchaseTable.addHeaderCell(new Cell().add(new Paragraph("Barcode").setBold()));
            purchaseTable.addHeaderCell(new Cell().add(new Paragraph("Price").setBold()));
            purchaseTable.addHeaderCell(new Cell().add(new Paragraph("Quantity").setBold()));
            purchaseTable.addHeaderCell(new Cell().add(new Paragraph("Total").setBold()));

            for (CartItemDTO item : checkoutData.getCartItems()) {
                purchaseTable.addCell(item.getName() != null ? item.getName() : "Unknown Item");
                purchaseTable.addCell(item.getBarcode() != null ? item.getBarcode() : "N/A");
                Double discountedPrice = item.getDiscountedPrice() != null ? item.getDiscountedPrice() : 0.0;
                purchaseTable.addCell(String.format("$%.2f", discountedPrice));
                Integer quantity = item.getQuantity() != 0 ? item.getQuantity() : 0;
                purchaseTable.addCell(String.valueOf(quantity));
                purchaseTable.addCell(String.format("$%.2f", discountedPrice * quantity));
            }
            document.add(purchaseTable);

            // Summary, Transaction Details, etc. (use your existing logic)

            // Close the document
            document.close();
            log.info("PDF document closed successfully");

            // Verify the file size
            Path writtenFile = Paths.get(filePath);
            if (!Files.exists(writtenFile)) {
                log.error("PDF file was not created at: {}", filePath);
                throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "PDF file was not created");
            }
            log.info("PDF file created successfully, size: {} bytes", Files.size(writtenFile));

            // Store the PDF association with the user in memory
            userPdfs.computeIfAbsent(username, k -> new ArrayList<>()).add(filename);

            // Save the updated mappings to the JSON file
            saveMappingsToFile();

            // Return the URL to access the PDF
            return URL_PREFIX + filename;

        } catch (Exception e) {
            log.error("Error generating PDF: ", e);
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage());
        } finally {
            // Ensure resources are closed
            try {
                if (document != null) document.close();
                if (pdf != null) pdf.close();
                if (writer != null) writer.close();
            } catch (Exception e) {
                log.error("Error closing PDF resources: ", e);
            }
        }
    }

    public List<String> getAllPdfs(String username) throws CustomSystemException {
        log.info("PdfService => getAllPdfs() for username: {}", username);
        try {
            // Retrieve PDFs from the in-memory map (populated from JSON file on startup)
            List<String> userFiles = userPdfs.getOrDefault(username, new ArrayList<>());
            // Filter out files that no longer exist
            List<String> existingFiles = userFiles.stream()
                    .filter(filename -> Files.exists(Paths.get(pdfStoragePath).resolve(filename)))
                    .map(filename -> URL_PREFIX + filename)
                    .collect(Collectors.toList());
            return existingFiles;
        } catch (Exception e) {
            log.error("Error retrieving all PDFs: ", e);
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage());
        }
    }

    public Path getPdf(String username, String filename) throws CustomSystemException {
        log.info("PdfService => getPdf() for username: {}, filename: {}", username, filename);
        try {
            // Optional: Verify if the PDF belongs to the user (uncomment if needed)
            /*
            List<String> userFiles = userPdfs.getOrDefault(username, new ArrayList<>());
            if (!userFiles.contains(filename)) {
                throw new CustomSystemException(HttpStatus.FORBIDDEN.toString(), "You do not have access to this PDF");
            }
            */

            Path filePath = Paths.get(pdfStoragePath).resolve(filename).toAbsolutePath().normalize();
            if (!Files.exists(filePath)) {
                throw new CustomSystemException(HttpStatus.BAD_REQUEST.toString(), "PDF not found: " + filename);
            }

            return filePath;
        } catch (Exception e) {
            log.error("Error retrieving PDF: ", e);
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage());
        }
    }
}