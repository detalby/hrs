package com.example.hrs.controller;

import com.example.hrs.model.BookingDto;
import com.example.hrs.model.BookingFilter;
import com.example.hrs.service.BookingIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "api/booking", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class BookingController {


    private final BookingIntegrationService bookingIntegrationService;

    @PostMapping
    public Mono<BookingDto> createBooking(@RequestBody BookingDto bookingDto) {
        return bookingIntegrationService.create(bookingDto);
    }

    @GetMapping("/{id}")
    public Mono<BookingDto> getBookingById(@PathVariable String id) {
        return bookingIntegrationService.getById(id);
    }

    @PostMapping("/{id}/cancel")
    public Mono<Void> cancelBooking(@PathVariable String id) {
        return bookingIntegrationService.cancel(id);
    }

    @GetMapping
    public Flux<BookingDto> findBookings(@ModelAttribute BookingFilter filter) {
        return bookingIntegrationService.find(filter);
    }
}
