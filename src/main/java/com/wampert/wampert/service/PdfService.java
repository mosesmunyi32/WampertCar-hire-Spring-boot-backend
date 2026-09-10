package com.wampert.wampert.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import com.wampert.wampert.model.BookingEntity;
import com.wampert.wampert.model.CarEntity;
import com.wampert.wampert.model.UserEntity;
import com.wampert.wampert.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfService {

    private final BookingRepository bookingRepository;

    @Value("${file.upload.dir:/var/www/sites/wampertcar/uploads}")
    private String uploadDir;

    @Value("${file.base.url:https://api.warmpertcar.site/uploads}")
    private String baseUrl;

    // Generate booking receipt PDF and save locally
    public String generateAndStoreBookingReceipt(
            BookingEntity booking,
            UserEntity customer,
            CarEntity car) {

        try {
            // Step 1 - Generate PDF bytes
            byte[] pdfBytes = generateBookingReceipt(booking, customer, car);

            // Step 2 - Save to local storage
            String folderPath = uploadDir + "/receipts";
            File folderDir = new File(folderPath);
            if (!folderDir.exists()) {
                folderDir.mkdirs();
            }

            // Generate unique filename
            String filename = "receipt-" + booking.getBookingReference() + "-" + UUID.randomUUID() + ".pdf";
            Path filePath = Paths.get(folderPath, filename);
            Files.write(filePath, pdfBytes);

            // Step 3 - Generate URL
            String receiptUrl = baseUrl + "/receipts/" + filename;

            // Step 4 - Save URL to booking
            booking.setReceiptUrl(receiptUrl);
            bookingRepository.save(booking);

            log.info("Receipt generated and stored for booking: {}",
                    booking.getBookingReference());

            return receiptUrl;

        } catch (Exception e) {
            log.error("Failed to generate and store receipt: {}",
                    e.getMessage());
            throw new RuntimeException(
                    "Failed to generate receipt: " + e.getMessage());
        }
    }

    // Generate PDF bytes
    public byte[] generateBookingReceipt(
            BookingEntity booking,
            UserEntity customer,
            CarEntity car) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MM yyyy 'at' HH:mm");

        try {
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);

            document.open();

            // ===== FONTS =====
            Font titleFont = new Font(Font.HELVETICA, 22, Font.BOLD, Color.BLACK);
            Font headerFont = new Font(Font.HELVETICA, 13, Font.BOLD, Color.BLACK);
            Font normalFont = new Font(Font.HELVETICA, 11, Font.NORMAL, Color.BLACK);
            Font smallFont = new Font(Font.HELVETICA, 9, Font.NORMAL, Color.GRAY);

            // ===== HEADER =====
            Paragraph title = new Paragraph("WAMPERT CAR HIRE", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph subtitle = new Paragraph(
                    "Booking Receipt",
                    new Font(Font.HELVETICA, 14, Font.NORMAL, Color.GRAY));
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(20);
            document.add(subtitle);

            // ===== DIVIDER =====
            document.add(new Paragraph(
                    "________________________________________________"));
            document.add(Chunk.NEWLINE);

            // ===== BOOKING REFERENCE =====
            document.add(new Paragraph(
                    "Booking Reference: " + booking.getBookingReference(),
                    headerFont));
            Paragraph status = new Paragraph(
                    "Status: " + booking.getBookingStatus(),
                    normalFont);
            status.setSpacingAfter(15);
            document.add(status);

            // ===== CUSTOMER DETAILS =====
            document.add(new Paragraph("CUSTOMER DETAILS", headerFont));
            document.add(new Paragraph(
                    "Name: " + customer.getFirstName()
                            + " " + customer.getLastName(), normalFont));
            document.add(new Paragraph(
                    "Email: " + customer.getEmail(), normalFont));
            document.add(new Paragraph(
                    "Phone: " + customer.getPhoneNumber(), normalFont));
            document.add(new Paragraph(
                    "ID Number: " + customer.getIdNumber(), normalFont));

            document.add(Chunk.NEWLINE);

            // ===== CAR DETAILS =====
            document.add(new Paragraph("CAR DETAILS", headerFont));
            document.add(new Paragraph(
                    "Car: " + car.getBrand() + " " + car.getModel(),
                    normalFont));
            document.add(new Paragraph(
                    "Year: " + car.getYearOfManufacture(), normalFont));
            document.add(new Paragraph(
                    "Number Plate: " + car.getNumberPlate(), normalFont));
            document.add(new Paragraph(
                    "Transmission: " + car.getTransmission(), normalFont));
            document.add(new Paragraph(
                    "Fuel Type: " + car.getTypeOfFuel(), normalFont));

            document.add(Chunk.NEWLINE);

            // ===== BOOKING DETAILS =====
            document.add(new Paragraph("BOOKING DETAILS", headerFont));
            document.add(new Paragraph(
                    "Start Date: " + booking.getStartDate().format(formatter), normalFont));
            document.add(new Paragraph(
                    "End Date: " + booking.getEndDate().format(formatter), normalFont));
            document.add(new Paragraph(
                    "Pick Up Location: " + booking.getPickUpLocation(),
                    normalFont));
            document.add(new Paragraph(
                    "Travel Destination: " + booking.getTravelDestination(),
                    normalFont));
            document.add(new Paragraph(
                    "Number of Days: " + booking.getNumberOfDays(), normalFont));

            document.add(Chunk.NEWLINE);

            // ===== PAYMENT DETAILS =====
            document.add(new Paragraph("PAYMENT DETAILS", headerFont));
            document.add(new Paragraph(
                    "Price Per Day: KES " + booking.getPricePerDay(),
                    normalFont));
            document.add(new Paragraph(
                    "Discount: KES " + booking.getDiscount(), normalFont));

            Paragraph total = new Paragraph(
                    "TOTAL COST: KES " + booking.getBookingCost(),
                    new Font(Font.HELVETICA, 13, Font.BOLD, Color.BLACK));
            total.setSpacingBefore(5);
            document.add(total);

            // ===== FOOTER =====
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph(
                    "________________________________________________"));
            Paragraph footer = new Paragraph(
                    "Thank you for choosing Wampert Car Hire!\n" +
                            "For inquiries, contact us on WhatsApp.",
                    smallFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(10);
            document.add(footer);

            document.close();

            return outputStream.toByteArray();

        } catch (Exception e) {
            log.error("Failed to generate PDF: {}", e.getMessage());
            throw new RuntimeException(
                    "Failed to generate PDF: " + e.getMessage());
        }
    }
}
