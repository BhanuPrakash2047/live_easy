
package com.liveasy.booking.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.liveasy.common.dto.LoadDto;

import java.util.UUID;

@FeignClient(name = "load-service")
public interface LoadServiceClient {
    
    @GetMapping("/api/load/{loadId}")
    ResponseEntity<LoadDto> getLoadById(@PathVariable UUID loadId);
    
    @PutMapping("/api/load/{loadId}/status")
    ResponseEntity<LoadDto> updateLoadStatus(@PathVariable UUID loadId, @RequestBody String status);
}
