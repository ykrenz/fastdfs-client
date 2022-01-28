package com.ykren.fastdfs.event;

public class ProgressEvent {
    private final long bytes;
    private final ProgressEventType eventType;

    public ProgressEvent(ProgressEventType eventType) {
        this(eventType, 0);
    }

    public ProgressEvent(ProgressEventType eventType, long bytes) {
        if (eventType == null) {
            throw new IllegalArgumentException("eventType must not be null.");
        }
        if (bytes < 0) {
            throw new IllegalArgumentException("bytes transferred must be non-negative");
        }
        this.eventType = eventType;
        this.bytes = bytes;
    }

    public long getBytes() {
        return bytes;
    }

    public ProgressEventType getEventType() {
        return eventType;
    }

    @Override
    public String toString() {
        return eventType + ", bytes: " + bytes;
    }
}