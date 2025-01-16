package com.example.hrs.service;

import com.example.hrs.model.BookingDto;
import com.example.hrs.model.BookingFilter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookingIntegrationService {

    Mono<BookingDto> create(BookingDto bookingDto);

    Mono<BookingDto> getById(String bookingId);

    Mono<Void> cancel(String bookingId);

    Flux<BookingDto> find(BookingFilter filter);
}
