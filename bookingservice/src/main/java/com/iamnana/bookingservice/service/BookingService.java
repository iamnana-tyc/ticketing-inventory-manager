package com.iamnana.bookingservice.service;

import com.iamnana.bookingservice.request.BookingRequest;
import com.iamnana.bookingservice.response.BookingResponse;

public interface BookingService {
    BookingResponse createBooking(BookingRequest request);
}
