package com.iamnana.Inventoryservice.controller;

import com.iamnana.Inventoryservice.response.EventInventoryResponse;
import com.iamnana.Inventoryservice.response.VenueInventoryResponse;
import com.iamnana.Inventoryservice.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/inventory")
public class InventoryController {
    private final EventService eventService;

    @GetMapping("/events")
    public ResponseEntity<List<EventInventoryResponse>> getAllEvents(){
        List<EventInventoryResponse> events = eventService.getAllEvents();

        return ResponseEntity.ok(events);
    }

    @GetMapping("/venue/{venueId}")
    public ResponseEntity<VenueInventoryResponse> getVenueInformation(
            @PathVariable Long venueId){
        VenueInventoryResponse details = eventService.getVenueInformation(venueId);

        return ResponseEntity.ok(details);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<EventInventoryResponse> getEventInventory(@PathVariable Long eventId){
        EventInventoryResponse events = eventService.getEventInventory(eventId);

        return ResponseEntity.ok(events);
    }

    @PutMapping("/event/{eventId}/capacity/{capacity}")
    public ResponseEntity<Void> updateEventCapacity(@PathVariable("eventId") Long eventId,
                                                    @PathVariable("capacity") Long ticketsBooked){
        eventService.updateEventCapacity(eventId, ticketsBooked);

        return ResponseEntity.ok().build();
    }
}
