package com.wampart.wampart.service;


import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.wampart.wampart.config.TwilioConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WhatsAppService {
    private final TwilioConfig twilioConfig;
    @Value("${twilio.admin-whatsapp-number}")
    private String adminWhatsAppNumber;

    public void sendWhatsAppMessage(String toPhoneNumber, String message) {
        try {
            Message.creator(
                    new PhoneNumber("whatsapp: " + toPhoneNumber ),
                    new PhoneNumber(twilioConfig.getWhatsappNumber()),
                    message
            ).create();

            log.info("whatsApp Message sent successfully to: " + toPhoneNumber);

        } catch (Exception e) {
            log.error("Failed to send whatsApp message to: " + toPhoneNumber, e.getMessage());
            throw new RuntimeException("Failed to send whatsApp message: "+ e.getMessage());
        }
    }

    //==============Notification messages ============
    public void notifyAdminNewUser(String firstName, String phoneNumber, String email, String lastName) {
        String message =
                "🚗 *WAMPART CAR HIRE*\n\n" +
                "👤 *New User Registered!*\n\n" +
                "Name: " + firstName + " " + lastName + "\n" +
                "Email: " + email + "\n" +
                "Phone: " + phoneNumber + "\n\n" +
                "Please login to verify their documents.";
        sendWhatsAppMessage(adminWhatsAppNumber, message);
    }

    // 2. Notify admin when a new booking is created
    public void notifyAdminNewBooking(String bookingReference,
                                      String customerName, String carDetails,
                                      String startDate, String endDate,
                                      Double bookingCost) {
        String message =
                "🚗 *WAMPART CAR HIRE*\n\n" +
                        "📅 *New Booking Received!*\n\n" +
                        "Reference: " + bookingReference + "\n" +
                        "Customer: " + customerName + "\n" +
                        "Car: " + carDetails + "\n" +
                        "Start Date: " + startDate + "\n" +
                        "End Date: " + endDate + "\n" +
                        "Total Cost: KES " + bookingCost + "\n\n" +
                        "Please login to confirm the booking.";

        sendWhatsAppMessage(adminWhatsAppNumber, message);
    }

    // 3. Notify the customer when a booking is confirmed
    public void notifyCustomerBookingConfirmed(String customerPhone,
                                               String customerName, String bookingReference,
                                               String carDetails, String startDate,
                                               String endDate, Double bookingCost,
                                               String receiptUrl) {
        String message =
                "🚗 *WAMPART CAR HIRE*\n\n" +
                        "✅ *Booking Confirmed!*\n\n" +
                        "Hello " + customerName + "!\n\n" +
                        "Your booking has been confirmed.\n\n" +
                        "Reference: " + bookingReference + "\n" +
                        "Car: " + carDetails + "\n" +
                        "Start Date: " + startDate + "\n" +
                        "End Date: " + endDate + "\n" +
                        "Total Cost: KES " + bookingCost + "\n\n" +
                        "📄 Your Receipt: " + receiptUrl + "\n\n" +
                        "Thank you for choosing Wampart Car Hire!";

        sendWhatsAppMessage(customerPhone, message);
    }
}



