
package com.liveasy.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.liveasy.booking.client.LoadServiceClient;
import com.liveasy.booking.exception.BookingException;
import com.liveasy.booking.exception.BookingNotFoundException;
import com.liveasy.booking.model.Booking;
import com.liveasy.booking.repository.BookingRepository;
import com.liveasy.common.dto.LoadDto;
import com.liveasy.common.events.LoadStatusChangedEvent;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private LoadServiceClient loadServiceClient;
    
    @Autowired
    private KafkaTemplate<String, LoadStatusChangedEvent> kafkaTemplate;
    
    private static final String TOPIC = "booking-events";

    @Cacheable(value = "bookings")
    public List<Booking> getAllBookings() {
        logger.info("Fetching all bookings");
        return bookingRepository.findAll();
    }

    @Cacheable(value = "bookings", key = "#id")
    public Booking getBookingById(UUID id) {
        logger.info("Fetching booking with ID: {}", id);
        return bookingRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Booking not found with ID: {}", id);
                    return new BookingNotFoundException("Booking not found with ID: " + id);
                });
    }

    public List<Booking> getBookingsByLoadId(UUID loadId) {
        logger.info("Fetching bookings for load ID: {}", loadId);
        return bookingRepository.findByLoadId(loadId);
    }

    public List<Booking> getBookingsByTransporterId(String transporterId) {
        logger.info("Fetching bookings for transporter ID: {}", transporterId);
        return bookingRepository.findByTransporterId(transporterId);
    }

    @CacheEvict(value = "bookings", allEntries = true)
    public Booking createBooking(Booking booking) {
        try {
            // Verify load exists and check its status
            ResponseEntity<LoadDto> loadResponse = loadServiceClient.getLoadById(booking.getLoadId());
            
            if (loadResponse == null || loadResponse.getBody() == null) {
                logger.error("Load not found with ID: {}", booking.getLoadId());
                throw new BookingException("Load not found with ID: " + booking.getLoadId());
            }
            
            LoadDto load = loadResponse.getBody();
            
            // Check if load is already CANCELLED
            if (load.getStatus() == LoadDto.LoadStatus.CANCELLED) {
                logger.error("Cannot create booking for cancelled load with ID: {}", booking.getLoadId());
                throw new BookingException("Cannot create booking for cancelled load");
            }
            
            // Create booking
            Booking savedBooking = bookingRepository.save(booking);
            
            // Update load status to BOOKED
            loadServiceClient.updateLoadStatus(booking.getLoadId(), "BOOKED");
            
            // Send Kafka event
            kafkaTemplate.send(TOPIC, new LoadStatusChangedEvent(booking.getLoadId(), "BOOKED"));
            
            logger.info("Created booking with ID: {}", savedBooking.getId());
            return savedBooking;
            
        } catch (Exception e) {
            logger.error("Error creating booking: {}", e.getMessage());
            throw new BookingException("Error creating booking: " + e.getMessage());
        }
    }

    @CacheEvict(value = "bookings", key = "#id")
    public Booking updateBooking(UUID id, Booking bookingDetails) {
        Booking booking = getBookingById(id);
        
        booking.setProposedRate(bookingDetails.getProposedRate());
        booking.setComment(bookingDetails.getComment());
        
        // Only update status if it's changing
        if (bookingDetails.getStatus() != null && bookingDetails.getStatus() != booking.getStatus()) {
            booking.setStatus(bookingDetails.getStatus());
            
            // If booking is ACCEPTED, update load status
            if (bookingDetails.getStatus() == Booking.BookingStatus.ACCEPTED) {
                loadServiceClient.updateLoadStatus(booking.getLoadId(), "BOOKED");
                
                // Send Kafka event
                kafkaTemplate.send(TOPIC, new LoadStatusChangedEvent(booking.getLoadId(), "BOOKED"));
                logger.info("Updated load status to BOOKED for load ID: {}", booking.getLoadId());
            }
        }
        
        Booking updatedBooking = bookingRepository.save(booking);
        logger.info("Updated booking with ID: {}", id);
        
        return updatedBooking;
    }

    @CacheEvict(value = "bookings", key = "#id")
    public void deleteBooking(UUID id) {
        Booking booking = getBookingById(id);
        
        // Update load status to CANCELLED when booking is deleted
        loadServiceClient.updateLoadStatus(booking.getLoadId(), "CANCELLED");
        
        // Send Kafka event
        kafkaTemplate.send(TOPIC, new LoadStatusChangedEvent(booking.getLoadId(), "CANCELLED"));
        
        bookingRepository.delete(booking);
        logger.info("Deleted booking with ID: {}", id);
    }
}
