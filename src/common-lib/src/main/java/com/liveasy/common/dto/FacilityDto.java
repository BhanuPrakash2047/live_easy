
package com.liveasy.common.dto;

import java.time.LocalDateTime;

public class FacilityDto {
    private String loadingPoint;
    private String unloadingPoint;
    private LocalDateTime loadingDate;
    private LocalDateTime unloadingDate;

    // Constructors
    public FacilityDto() {
    }

    // Getters and Setters
    public String getLoadingPoint() {
        return loadingPoint;
    }

    public void setLoadingPoint(String loadingPoint) {
        this.loadingPoint = loadingPoint;
    }

    public String getUnloadingPoint() {
        return unloadingPoint;
    }

    public void setUnloadingPoint(String unloadingPoint) {
        this.unloadingPoint = unloadingPoint;
    }

    public LocalDateTime getLoadingDate() {
        return loadingDate;
    }

    public void setLoadingDate(LocalDateTime loadingDate) {
        this.loadingDate = loadingDate;
    }

    public LocalDateTime getUnloadingDate() {
        return unloadingDate;
    }

    public void setUnloadingDate(LocalDateTime unloadingDate) {
        this.unloadingDate = unloadingDate;
    }
}
