package com.iamnana.orderservice.service;

import com.iamnana.bookingservice.event.BookingEvent;
import com.iamnana.orderservice.client.InventoryServiceClient;
import com.iamnana.orderservice.entity.Order;
import com.iamnana.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryServiceClient inventoryServiceClient;

    // Transactional: if the inventory update fails, the order insert is rolled back,
    // so a retry or dead-letter does not leave an order without its inventory change.
    @KafkaListener(topics = "booking", groupId = "order-service")
    @Transactional
    public void orderEvent(BookingEvent bookingEvent){
        log.info("Received order event {}", bookingEvent);

        // creating an order object for DB
        Order order = createOrder(bookingEvent);
        orderRepository.saveAndFlush(order);

        // update the inventory
        inventoryServiceClient.updateEventCapacity(order.getEventId(), order.getTicketCount());
        log.info("Inventory updated for the event: {}, less tickets: {}", order.getEventId(), order.getTicketCount());

    }

    private Order createOrder(BookingEvent bookingEvent) {
        return Order.builder()
                .customerId(bookingEvent.getUserId())
                .eventId(bookingEvent.getEventId())
                .ticketCount(bookingEvent.getTicketCount())
                .totalPrice(bookingEvent.getTotalPrice())
                .build();
    }
}
