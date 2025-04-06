
package com.liveasy.load.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.liveasy.load.exception.LoadNotFoundException;
import com.liveasy.load.model.Load;
import com.liveasy.load.service.LoadService;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/load")
public class LoadController {

    private static final Logger logger = LoggerFactory.getLogger(LoadController.class);
    
    @Autowired
    private LoadService loadService;

    @GetMapping
    public ResponseEntity<List<Load>> getAllLoads(
            @RequestParam(required = false) String shipperId,
            @RequestParam(required = false) String truckType) {
        
        try {
            List<Load> loads;
            
            if (shipperId != null) {
                logger.info("Fetching loads for shipper ID: {}", shipperId);
                loads = loadService.getLoadsByShipperId(shipperId);
            } else if (truckType != null) {
                logger.info("Fetching loads for truck type: {}", truckType);
                loads = loadService.getLoadsByTruckType(truckType);
            } else {
                logger.info("Fetching all loads");
                loads = loadService.getAllLoads();
            }
            
            return ResponseEntity.ok(loads);
        } catch (Exception e) {
            logger.error("Error fetching loads: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{loadId}")
    public ResponseEntity<?> getLoadById(@PathVariable UUID loadId) {
        try {
            Load load = loadService.getLoadById(loadId);
            return ResponseEntity.ok(load);
        } catch (LoadNotFoundException e) {
            logger.error("Load not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error fetching load: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching load: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createLoad(@RequestBody Load load, @RequestHeader("userId") String userId) {
        try {
            load.setShipperId(userId);
            Load createdLoad = loadService.createLoad(load);
            logger.info("Load created with ID: {}", createdLoad.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdLoad);
        } catch (Exception e) {
            logger.error("Error creating load: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error creating load: " + e.getMessage());
        }
    }

    @PutMapping("/{loadId}")
    public ResponseEntity<?> updateLoad(@PathVariable UUID loadId, @RequestBody Load loadDetails, 
            @RequestHeader("userId") String userId, @RequestHeader("role") String role) {
        
        try {
            Load existingLoad = loadService.getLoadById(loadId);
            
            // Check if user is the shipper who created the load or an admin
            if (!existingLoad.getShipperId().equals(userId) && !role.equals("ADMIN")) {
                logger.warn("Unauthorized access attempt to update load ID: {} by user: {}", loadId, userId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to update this load");
            }
            
            Load updatedLoad = loadService.updateLoad(loadId, loadDetails);
            logger.info("Load updated with ID: {}", loadId);
            return ResponseEntity.ok(updatedLoad);
        } catch (LoadNotFoundException e) {
            logger.error("Load not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating load: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating load: " + e.getMessage());
        }
    }

    @DeleteMapping("/{loadId}")
    public ResponseEntity<?> deleteLoad(@PathVariable UUID loadId, 
            @RequestHeader("userId") String userId, @RequestHeader("role") String role) {
        
        try {
            Load existingLoad = loadService.getLoadById(loadId);
            
            // Check if user is the shipper who created the load or an admin
            if (!existingLoad.getShipperId().equals(userId) && !role.equals("ADMIN")) {
                logger.warn("Unauthorized access attempt to delete load ID: {} by user: {}", loadId, userId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete this load");
            }
            
            loadService.deleteLoad(loadId);
            logger.info("Load deleted with ID: {}", loadId);
            return ResponseEntity.ok("Load deleted successfully");
        } catch (LoadNotFoundException e) {
            logger.error("Load not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error deleting load: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting load: " + e.getMessage());
        }
    }
}
