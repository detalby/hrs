package com.example.hrs.service;

import com.example.hrs.model.BookingDto;
import com.example.hrs.model.BookingException;
import com.example.hrs.model.BookingFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implementation of {@link BookingIntegrationService} that integrates with the external hrs booking API.
 * This service uses a {@link WebClient} for making non-blocking HTTP requests to the API.
 */

@Service
@RequiredArgsConstructor
public class BookingIntegrationServiceImpl implements BookingIntegrationService {


    private final WebClient webClient;

    @Value("${hrs.client.id}")
    private String hotelId;

    /**
     * Creates a new booking for the specified hotel.
     *
     * @param bookingDto the booking details to be sent to the API
     * @return a {@link Mono} emitting the created {@link BookingDto} if the operation is successful
     * @throws BookingException if the API call fails
     */
    @Override
    public Mono<BookingDto> create(BookingDto bookingDto) {
        String url = "/{hotelId}/bookings";
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(url)
                        .build(hotelId))
                .bodyValue(bookingDto)
                .retrieve()
                .bodyToMono(BookingDto.class)
                .doOnError(e -> {
                    throw new BookingException("Failed to create booking: " + e.getMessage());
                });
    }

    /**
     * Retrieves a booking by its unique identifier.
     *
     * @param bookingId the unique identifier of the booking to fetch
     * @return a {@link Mono} emitting the {@link BookingDto} if the booking is found
     * @throws BookingException if the API call fails
     */
    @Override
    public Mono<BookingDto> getById(String bookingId) {
        String url = "/{hotelId}/bookings/{id}";
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(url)
                        .build(hotelId, bookingId))
                .retrieve()
                .bodyToMono(BookingDto.class)
                .doOnError(e -> {
                    throw new BookingException("Failed to fetch booking: " + e.getMessage());
                });
    }

    /**
     * Cancels an existing booking by its unique identifier.
     *
     * @param bookingId the unique identifier of the booking to cancel
     * @return a {@link Mono} signaling when the cancellation is complete
     * @throws BookingException if the API call fails
     */
    @Override
    public Mono<Void> cancel(String bookingId) {
        String url = "/{hotelId}/bookings/{id}/cancel";
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(url)
                        .build(hotelId, bookingId))
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(e -> {
                    throw new BookingException("Failed to cancel booking: " + e.getMessage());
                });
    }

    /**
     * Finds bookings matching the specified filter criteria.
     *
     * @param filter the filter criteria for searching bookings
     * @return a {@link Flux} emitting matching {@link BookingDto} objects
     * @throws BookingException if the API call fails
     */
    @Override
    public Flux<BookingDto> find(BookingFilter filter) {
        String url = "/{hotelId}/bookings";
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(url)
                        .queryParam("userId", filter.getUserId())
                        .queryParam("status", filter.getBookingStatus())
                        .queryParam("created", filter.getCreated())
                        .queryParam("apartmentId", filter.getApartmentId())
                        .queryParam("page", filter.getPage())
                        .queryParam("limit", filter.getLimit())
                        .build(hotelId))
                .retrieve()
                .bodyToFlux(BookingDto.class)
                .doOnError(e -> {
                    throw new BookingException("Failed to find bookings: " + e.getMessage());
                });
    }
}
