package com.iamnana.orderservice.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class InventoryServiceClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${inventory.service.url}")
    private String inventoryServiceUrl;

    public ResponseEntity<Void> updateEventCapacity(Long eventId, Long ticketCount) {
        try {
            restTemplate.put(inventoryServiceUrl + "/event/" + eventId + "/capacity/" + ticketCount, null);
        } catch (HttpClientErrorException e) {
            // 4xx (unknown event, not enough capacity) cannot succeed on retry
            log.error("Inventory rejected capacity update for event {}: {}", eventId, e.getResponseBodyAsString());
            throw e;
        } catch (RestClientException e) {
            log.error("Inventory service unreachable while updating event {}", eventId, e);
            throw e;
        }

        return ResponseEntity.ok().build();
    }
}
