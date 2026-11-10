package com.iamnana.Inventoryservice.service.implementation;

import com.iamnana.Inventoryservice.model.Event;
import com.iamnana.Inventoryservice.model.Venue;
import com.iamnana.Inventoryservice.repository.EventRepository;
import com.iamnana.Inventoryservice.repository.VenueRepository;
import com.iamnana.Inventoryservice.response.EventInventoryResponse;
import com.iamnana.Inventoryservice.response.VenueInventoryResponse;
import com.iamnana.Inventoryservice.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final VenueRepository venueRepository;

    @Override
    public List<EventInventoryResponse> getAllEvents() {
         List<Event> events = eventRepository.findAll();

        return events.stream()
                .map(event -> {
                    EventInventoryResponse eventInventoryResponse = new EventInventoryResponse();

                    eventInventoryResponse.setEventId(event.getEventId());
                    eventInventoryResponse.setEvent(event.getName());
                    eventInventoryResponse.setCapacity(event.getLeftCapacity());
                    eventInventoryResponse.setVenue(event.getVenue());
                    eventInventoryResponse.setTicketPrice(event.getTicketPrice());

                    return eventInventoryResponse;
                })
                .toList();

    }

    @Override
    public VenueInventoryResponse getVenueInformation(Long venueId) {
        Venue venue = venueRepository.findById(venueId).orElse(null);

        return modelMapper.map(venue, VenueInventoryResponse.class);
    }

    @Override
    public EventInventoryResponse getEventInventory(Long eventId) {
        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) {
            throw new RuntimeException("Event not found");
        }

        EventInventoryResponse eventInventoryResponse = new EventInventoryResponse();

        eventInventoryResponse.setEventId(eventId);
        eventInventoryResponse.setEvent(event.getName());
        eventInventoryResponse.setCapacity(event.getLeftCapacity());
        eventInventoryResponse.setVenue(event.getVenue());
        eventInventoryResponse.setTicketPrice(event.getTicketPrice());

        return eventInventoryResponse;
    }

    @Override
    public void updateEventCapacity(Long eventId, Long ticketsBooked) {
        Event event = eventRepository.findById(eventId).orElse(null);
        event.setLeftCapacity(event.getLeftCapacity() -  ticketsBooked);
        eventRepository.saveAndFlush(event);
        log.info("Event capacity updated for event id: {} with tickets booked: {}", eventId, ticketsBooked);
    }
}
