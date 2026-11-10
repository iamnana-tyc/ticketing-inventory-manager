package com.iamnana.Inventoryservice.service;

import com.iamnana.Inventoryservice.response.EventInventoryResponse;
import com.iamnana.Inventoryservice.response.VenueInventoryResponse;

import java.util.List;

public interface EventService {
    List<EventInventoryResponse> getAllEvents();

    VenueInventoryResponse getVenueInformation(Long venueId);

    EventInventoryResponse getEventInventory(Long eventId);

    void updateEventCapacity(Long eventId, Long ticketsBooked);
}
