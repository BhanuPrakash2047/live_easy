
package com.liveasy.common.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class LoadDto {
    private UUID id;
    private String shipperId;
    private FacilityDto facility;
    private String productType;
    private String truckType;
    private int noOfTrucks;
    private double weight;
    private String comment;
    private LocalDateTime datePosted;
    private LoadStatus status;

    public enum LoadStatus {
        POSTED, BOOKED, CANCELLED
    }

    // Constructors
    public LoadDto() {
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getShipperId() {
        return shipperId;
    }

    public void setShipperId(String shipperId) {
        this.shipperId = shipperId;
    }

    public FacilityDto getFacility() {
        return facility;
    }

    public void setFacility(FacilityDto facility) {
        this.facility = facility;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getTruckType() {
        return truckType;
    }

    public void setTruckType(String truckType) {
        this.truckType = truckType;
    }

    public int getNoOfTrucks() {
        return noOfTrucks;
    }

    public void setNoOfTrucks(int noOfTrucks) {
        this.noOfTrucks = noOfTrucks;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(LocalDateTime datePosted) {
        this.datePosted = datePosted;
    }

    public LoadStatus getStatus() {
        return status;
    }

    public void setStatus(LoadStatus status) {
        this.status = status;
    }
}
