package com.iamnana.orderservice;

import com.iamnana.bookingservice.event.BookingEvent;
import com.iamnana.orderservice.client.InventoryServiceClient;
import com.iamnana.orderservice.entity.Order;
import com.iamnana.orderservice.repository.OrderRepository;
import com.iamnana.orderservice.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private InventoryServiceClient inventoryServiceClient;

    @InjectMocks
    private OrderService orderService;

    private final BookingEvent bookingEvent =
            new BookingEvent(1L, 10L, 3L, new BigDecimal("90.00"));

    @Test
    void orderEventSavesOrderThenReducesInventory() {
        orderService.orderEvent(bookingEvent);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        InOrder inOrder = inOrder(orderRepository, inventoryServiceClient);
        inOrder.verify(orderRepository).saveAndFlush(captor.capture());
        inOrder.verify(inventoryServiceClient).updateEventCapacity(10L, 3L);

        Order saved = captor.getValue();
        assertThat(saved.getCustomerId()).isEqualTo(1L);
        assertThat(saved.getEventId()).isEqualTo(10L);
        assertThat(saved.getTicketCount()).isEqualTo(3L);
        assertThat(saved.getTotalPrice()).isEqualByComparingTo("90.00");
    }

    @Test
    void orderEventPropagatesInventoryFailureSoTheTransactionRollsBackAndKafkaRetries() {
        doThrow(new RestClientException("inventory down"))
                .when(inventoryServiceClient).updateEventCapacity(10L, 3L);

        assertThatThrownBy(() -> orderService.orderEvent(bookingEvent))
                .isInstanceOf(RestClientException.class);

        verify(orderRepository).saveAndFlush(org.mockito.ArgumentMatchers.any(Order.class));
    }
}
