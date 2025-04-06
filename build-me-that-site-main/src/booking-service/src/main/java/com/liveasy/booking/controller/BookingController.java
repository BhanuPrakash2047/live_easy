
package com.liveasy.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.liveasy.booking.exception.BookingException;
import com.liveasy.booking.exception.BookingNotFoundException;
import com.liveasy.booking.model.Booking;
import com.liveasy.booking.service.BookingService;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/booking")
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);
    
    @Autowired
    private BookingService bookingService;

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings(
            @RequestParam(required = false) UUID loadId,
            @RequestParam(required = false) String transporterId) {
        
        try {
            List<Booking> bookings;
            
            if (loadId != null) {
                logger.info("Fetching bookings for load ID: {}", loadId);
                bookings = bookingService.getBookingsByLoadId(loadId);
            } else if (transporterId != null) {
                logger.info("Fetching bookings for transporter ID: {}", transporterId);
                bookings = bookingService.getBookingsByTransporterId(transporterId);
            } else {
                logger.info("Fetching all bookings");
                bookings = bookingService.getAllBookings();
            }
            
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            logger.error("Error fetching bookings: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<?> getBookingById(@PathVariable UUID bookingId) {
        try {
            Booking booking = bookingService.getBookingById(bookingId);
            return ResponseEntity.ok(booking);
        } catch (BookingNotFoundException e) {
            logger.error("Booking not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error fetching booking: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching booking: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Booking booking, @RequestHeader("userId") String userId) {
        try {
            booking.setTransporterId(userId);
            Booking createdBooking = bookingService.createBooking(booking);
            logger.info("Booking created with ID: {}", createdBooking.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBooking);
        } catch (BookingException e) {
            logger.error("Error creating booking: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error creating booking: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating booking: " + e.getMessage());
        }
    }

    @PutMapping("/{bookingId}")
    public ResponseEntity<?> updateBooking(@PathVariable UUID bookingId, @RequestBody Booking bookingDetails,
            @RequestHeader("userId") String userId, @RequestHeader("role") String role) {
        
        try {
            Booking existingBooking = bookingService.getBookingById(bookingId);
            
            // Check if user is the transporter who created the booking, the shipper who owns the load, or an admin
            if (!existingBooking.getTransporterId().equals(userId) && !role.equals("ADMIN")) {
                logger.warn("Unauthorized access attempt to update booking ID: {} by user: {}", bookingId, userId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to update this booking");
            }
            
            Booking updatedBooking = bookingService.updateBooking(bookingId, bookingDetails);
            logger.info("Booking updated with ID: {}", bookingId);
            return ResponseEntity.ok(updatedBooking);
        } catch (BookingNotFoundException e) {
            logger.error("Booking not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating booking: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating booking: " + e.getMessage());
        }
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<?> deleteBooking(@PathVariable UUID bookingId,
            @RequestHeader("userId") String userId, @RequestHeader("role") String role) {
        
        try {
            Booking existingBooking = bookingService.getBookingById(bookingId);
            
            // Check if user is the transporter who created the booking or an admin
            if (!existingBooking.getTransporterId().equals(userId) && !role.equals("ADMIN")) {
                logger.warn("Unauthorized access attempt to delete booking ID: {} by user: {}", bookingId, userId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete this booking");
            }
            
            bookingService.deleteBooking(bookingId);
            logger.info("Booking deleted with ID: {}", bookingId);
            return ResponseEntity.ok("Booking deleted successfully");
        } catch (BookingNotFoundException e) {
            logger.error("Booking not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error deleting booking: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting booking: " + e.getMessage());
        }
    }
}
