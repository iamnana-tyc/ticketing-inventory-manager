package com.iamnana.bookingservice.exception;

public class InsufficientInventoryException extends RuntimeException {
    public InsufficientInventoryException(Long eventId, Long requested, Long available) {
        super("Requested " + requested + " tickets for event " + eventId + " but only " + available + " are available");
    }
}
