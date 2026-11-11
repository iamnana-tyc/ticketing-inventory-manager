package com.iamnana.bookingservice;

import com.iamnana.bookingservice.client.InventoryService;
import com.iamnana.bookingservice.event.BookingEvent;
import com.iamnana.bookingservice.exception.InsufficientInventoryException;
import com.iamnana.bookingservice.exception.ResourceNotFoundException;
import com.iamnana.bookingservice.model.Customer;
import com.iamnana.bookingservice.repository.CustomerRepository;
import com.iamnana.bookingservice.request.BookingRequest;
import com.iamnana.bookingservice.response.BookingResponse;
import com.iamnana.bookingservice.response.InventoryResponse;
import com.iamnana.bookingservice.service.implementation.BookingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private InventoryService inventoryService;
    @Mock
    private KafkaTemplate<String, BookingEvent> kafkaTemplate;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private final Customer customer = Customer.builder().id(1L).name("Thomas Badu").build();

    private InventoryResponse inventory(long capacity) {
        return InventoryResponse.builder()
                .eventId(10L)
                .capacity(capacity)
                .ticketPrice(new BigDecimal("25.50"))
                .build();
    }

    @Test
    void createBookingPublishesEventWithTotalPriceWhenEnoughTickets() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(inventoryService.getInventory(10L)).thenReturn(inventory(5));

        BookingResponse response = bookingService.createBooking(new BookingRequest(1L, 10L, 2L));

        assertThat(response.getTotalPrice()).isEqualByComparingTo("51.00");
        assertThat(response.getTicketCount()).isEqualTo(2L);

        ArgumentCaptor<BookingEvent> captor = ArgumentCaptor.forClass(BookingEvent.class);
        verify(kafkaTemplate).send(org.mockito.ArgumentMatchers.eq("booking"), captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(1L);
        assertThat(captor.getValue().getEventId()).isEqualTo(10L);
        assertThat(captor.getValue().getTotalPrice()).isEqualByComparingTo("51.00");
    }

    @Test
    void createBookingAllowsBookingExactlyTheRemainingCapacity() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(inventoryService.getInventory(10L)).thenReturn(inventory(2));

        BookingResponse response = bookingService.createBooking(new BookingRequest(1L, 10L, 2L));

        assertThat(response.getTicketCount()).isEqualTo(2L);
    }

    @Test
    void createBookingThrowsWhenCustomerDoesNotExist() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.createBooking(new BookingRequest(99L, 10L, 1L)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(kafkaTemplate, never()).send(anyString(), any(BookingEvent.class));
    }

    @Test
    void createBookingThrowsAndPublishesNothingWhenNotEnoughTickets() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(inventoryService.getInventory(10L)).thenReturn(inventory(1));

        assertThatThrownBy(() -> bookingService.createBooking(new BookingRequest(1L, 10L, 2L)))
                .isInstanceOf(InsufficientInventoryException.class)
                .hasMessageContaining("only 1 are available");

        verify(kafkaTemplate, never()).send(anyString(), any(BookingEvent.class));
    }
}
