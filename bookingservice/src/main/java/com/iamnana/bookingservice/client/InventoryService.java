package com.iamnana.bookingservice.client;

import com.iamnana.bookingservice.exception.InventoryServiceUnavailableException;
import com.iamnana.bookingservice.exception.ResourceNotFoundException;
import com.iamnana.bookingservice.response.InventoryResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class InventoryService {
    @Value("${inventory.service.url}")
    private String inventoryServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public InventoryResponse getInventory(Long eventId) {
        try {
            InventoryResponse response = restTemplate.getForObject(
                    inventoryServiceUrl + "/event/" + eventId, InventoryResponse.class);
            if (response == null) {
                throw new InventoryServiceUnavailableException("Empty response from inventory service", null);
            }
            return response;
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Event with id " + eventId + " not found");
        } catch (RestClientException e) {
            throw new InventoryServiceUnavailableException("Failed to fetch inventory for event " + eventId, e);
        }
    }
}
