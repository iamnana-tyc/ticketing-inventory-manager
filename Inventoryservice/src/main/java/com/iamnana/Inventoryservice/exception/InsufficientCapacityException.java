package com.iamnana.Inventoryservice.exception;

public class InsufficientCapacityException extends RuntimeException {
    public InsufficientCapacityException(Long eventId, Long requested, Long available) {
        super("Event " + eventId + " has " + available + " tickets left, but " + requested + " were requested");
    }
}
