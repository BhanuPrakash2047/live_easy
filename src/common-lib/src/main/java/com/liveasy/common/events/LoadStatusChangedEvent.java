
package com.liveasy.common.events;

import java.time.LocalDateTime;
import java.util.UUID;

public class LoadStatusChangedEvent {
    private UUID loadId;
    private String status;
    private LocalDateTime timestamp;

    public LoadStatusChangedEvent() {
    }

    public LoadStatusChangedEvent(UUID loadId, String status) {
        this.loadId = loadId;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getLoadId() {
        return loadId;
    }

    public void setLoadId(UUID loadId) {
        this.loadId = loadId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
