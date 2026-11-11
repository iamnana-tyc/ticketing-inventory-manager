package com.iamnana.Inventoryservice;

import com.iamnana.Inventoryservice.exception.InsufficientCapacityException;
import com.iamnana.Inventoryservice.exception.ResourceNotFoundException;
import com.iamnana.Inventoryservice.model.Event;
import com.iamnana.Inventoryservice.model.Venue;
import com.iamnana.Inventoryservice.repository.EventRepository;
import com.iamnana.Inventoryservice.repository.VenueRepository;
import com.iamnana.Inventoryservice.response.EventInventoryResponse;
import com.iamnana.Inventoryservice.service.implementation.EventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private VenueRepository venueRepository;

    private EventServiceImpl eventService;

    private Event event;

    @BeforeEach
    void setUp() {
        eventService = new EventServiceImpl(eventRepository, new ModelMapper(), venueRepository);
        Venue venue = new Venue(1L, "Olympiastadion", 70000L, "Berlin");
        event = new Event(10L, "Concert", 100L, 40L, venue, new BigDecimal("30.00"));
    }

    @Test
    void getEventInventoryReturnsRemainingCapacityAndPrice() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));

        EventInventoryResponse response = eventService.getEventInventory(10L);

        assertThat(response.getEvent()).isEqualTo("Concert");
        assertThat(response.getCapacity()).isEqualTo(40L);
        assertThat(response.getTicketPrice()).isEqualByComparingTo("30.00");
    }

    @Test
    void getEventInventoryThrowsWhenEventUnknown() {
        when(eventRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.getEventInventory(404L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Event with id 404 not found");
    }

    @Test
    void getAllEventsMapsEveryEvent() {
        when(eventRepository.findAll()).thenReturn(List.of(event));

        List<EventInventoryResponse> responses = eventService.getAllEvents();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getEventId()).isEqualTo(10L);
    }

    @Test
    void getVenueInformationThrowsWhenVenueUnknown() {
        when(venueRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.getVenueInformation(5L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Venue with id 5 not found");
    }

    @Test
    void updateEventCapacityDecrementsRemainingCapacity() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));

        eventService.updateEventCapacity(10L, 15L);

        assertThat(event.getLeftCapacity()).isEqualTo(25L);
        verify(eventRepository).saveAndFlush(event);
    }

    @Test
    void updateEventCapacityAllowsSellingTheLastTickets() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));

        eventService.updateEventCapacity(10L, 40L);

        assertThat(event.getLeftCapacity()).isZero();
    }

    @Test
    void updateEventCapacity_throwsAndKeepsCapacity_whenOversold() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventService.updateEventCapacity(10L, 41L))
                .isInstanceOf(InsufficientCapacityException.class);

        assertThat(event.getLeftCapacity()).isEqualTo(40L);
        verify(eventRepository, never()).saveAndFlush(any());
    }
}
