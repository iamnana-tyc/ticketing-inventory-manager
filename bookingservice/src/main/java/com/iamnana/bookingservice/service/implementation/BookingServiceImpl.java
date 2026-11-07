package com.iamnana.bookingservice.service.implementation;

import com.iamnana.bookingservice.client.InventoryService;
import com.iamnana.bookingservice.event.BookingEvent;
import com.iamnana.bookingservice.model.Customer;
import com.iamnana.bookingservice.repository.CustomerRepository;
import com.iamnana.bookingservice.request.BookingRequest;
import com.iamnana.bookingservice.response.BookingResponse;
import com.iamnana.bookingservice.response.InventoryResponse;
import com.iamnana.bookingservice.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final CustomerRepository customerRepository;
    private final InventoryService inventoryService;
    private final KafkaTemplate<String, BookingEvent> kafkaTemplate;


    @Override
    public BookingResponse createBooking(BookingRequest request) {
        // checking that user exist
        Customer customer = customerRepository.findById(request.getUserId())
                .orElse(null);
        if (customer == null) {
            throw new RuntimeException("User not found");
        }

        // checking if there is enough inventory
        InventoryResponse inventoryResponse = inventoryService.getInventory(request.getEventId());
        log.info("Inventory Response: {}", inventoryResponse);

        // checking if enough inventory
        if (inventoryResponse.getCapacity() < request.getTicketCount()){
            throw new RuntimeException("Inventory less than requested capacity");
        }

        // create booking
        final BookingEvent bookingEvent = createBookingEvent(request, customer, inventoryResponse);

        // send a booking event to Kafka topic
        kafkaTemplate.send("booking", bookingEvent);
        log.info("Booking sent to Kafka: {}", bookingEvent);

        return BookingResponse.builder()
                .userId(bookingEvent.getUserId())
                .eventId(bookingEvent.getEventId())
                .ticketCount(bookingEvent.getTicketCount())
                .totalPrice(bookingEvent.getTotalPrice())
                .build();
    }

    private BookingEvent createBookingEvent(BookingRequest request,
                                            Customer customer,
                                            InventoryResponse inventoryResponse) {
        return BookingEvent.builder()
                .userId(customer.getId())
                .eventId(request.getEventId())
                .ticketCount(request.getTicketCount())
                .totalPrice(inventoryResponse.getTicketPrice().multiply(BigDecimal.valueOf(request.getTicketCount())))
                .build();
    }
}
