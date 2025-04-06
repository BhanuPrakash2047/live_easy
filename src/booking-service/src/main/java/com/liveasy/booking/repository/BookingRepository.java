
package com.liveasy.booking.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.liveasy.booking.model.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    List<Booking> findByLoadId(UUID loadId);
    List<Booking> findByTransporterId(String transporterId);
}
