
package com.liveasy.load.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.liveasy.load.exception.LoadNotFoundException;
import com.liveasy.load.model.Load;
import com.liveasy.load.repository.LoadRepository;
import com.liveasy.common.events.LoadStatusChangedEvent;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class LoadService {

    private static final Logger logger = LoggerFactory.getLogger(LoadService.class);
    
    @Autowired
    private LoadRepository loadRepository;
    
    @Autowired
    private KafkaTemplate<String, LoadStatusChangedEvent> kafkaTemplate;
    
    private static final String TOPIC = "load-status-changes";

    @Cacheable(value = "loads")
    public List<Load> getAllLoads() {
        logger.info("Fetching all loads");
        return loadRepository.findAll();
    }

    @Cacheable(value = "loads", key = "#id")
    public Load getLoadById(UUID id) {
        logger.info("Fetching load with ID: {}", id);
        return loadRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Load not found with ID: {}", id);
                    return new LoadNotFoundException("Load not found with ID: " + id);
                });
    }

    public List<Load> getLoadsByShipperId(String shipperId) {
        logger.info("Fetching loads for shipper ID: {}", shipperId);
        return loadRepository.findByShipperId(shipperId);
    }

    public List<Load> getLoadsByTruckType(String truckType) {
        logger.info("Fetching loads for truck type: {}", truckType);
        return loadRepository.findByTruckType(truckType);
    }

    @CacheEvict(value = "loads", allEntries = true)
    public Load createLoad(Load load) {
        load.setStatus(Load.LoadStatus.POSTED);
        Load savedLoad = loadRepository.save(load);
        logger.info("Created load with ID: {}", savedLoad.getId());
        
        // Send Kafka event for load creation
        kafkaTemplate.send(TOPIC, new LoadStatusChangedEvent(savedLoad.getId(), savedLoad.getStatus().toString()));
        
        return savedLoad;
    }

    @CacheEvict(value = "loads", key = "#id")
    public Load updateLoad(UUID id, Load loadDetails) {
        Load load = getLoadById(id);
        
        load.setFacility(loadDetails.getFacility());
        load.setProductType(loadDetails.getProductType());
        load.setTruckType(loadDetails.getTruckType());
        load.setNoOfTrucks(loadDetails.getNoOfTrucks());
        load.setWeight(loadDetails.getWeight());
        load.setComment(loadDetails.getComment());
        
        Load updatedLoad = loadRepository.save(load);
        logger.info("Updated load with ID: {}", id);
        
        return updatedLoad;
    }

    @CacheEvict(value = "loads", key = "#id")
    public void deleteLoad(UUID id) {
        Load load = getLoadById(id);
        loadRepository.delete(load);
        logger.info("Deleted load with ID: {}", id);
        
        // Send Kafka event for load deletion
        kafkaTemplate.send(TOPIC, new LoadStatusChangedEvent(id, "DELETED"));
    }

    @CacheEvict(value = "loads", key = "#id")
    public Load updateLoadStatus(UUID id, Load.LoadStatus status) {
        Load load = getLoadById(id);
        load.setStatus(status);
        Load updatedLoad = loadRepository.save(load);
        logger.info("Updated load status to {} for ID: {}", status, id);
        
        // Send Kafka event for status change
        kafkaTemplate.send(TOPIC, new LoadStatusChangedEvent(id, status.toString()));
        
        return updatedLoad;
    }
}
