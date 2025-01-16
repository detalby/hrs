package com.example.hrs.service;

import com.example.hrs.model.BookingDto;
import com.example.hrs.model.BookingException;
import com.example.hrs.model.BookingFilter;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

class BookingIntegrationServiceImplTest {

    private MockWebServer mockWebServer;
    private BookingIntegrationServiceImpl bookingIntegrationService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        String baseUrl = mockWebServer.url("/").toString();
        WebClient webClient = WebClient.builder().baseUrl(baseUrl).build();
        bookingIntegrationService = new BookingIntegrationServiceImpl(webClient);
        ReflectionTestUtils.setField(bookingIntegrationService, "hotelId", "hotel123");
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testCreate() throws InterruptedException {
        // Prepare the mock response from the server
        BookingDto bookingRequest = new BookingDto();
        bookingRequest.setId("booking123");
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"id\":\"booking123\"}") // JSON response body
                .addHeader("Content-Type", "application/json")
                .setResponseCode(200)); // HTTP 200 OK

        // Call the create method in the service
        Mono<BookingDto> result = bookingIntegrationService.create(bookingRequest);

        // Verify the result using StepVerifier
        StepVerifier.create(result)
                .expectNextMatches(booking -> booking.getId().equals("booking123"))
                .verifyComplete();

        // Verify the request sent to the MockWebServer
        okhttp3.mockwebserver.RecordedRequest request = mockWebServer.takeRequest();
        assert request.getMethod().equals("POST"); // Check HTTP method
        assert request.getPath().equals("/hotel123/bookings"); // Check request path
        System.out.println(request.getPath());
    }

    @Test
    void testGetById() throws InterruptedException {
        // Prepare the mock response from the server
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"id\":\"booking123\"}") // JSON response body
                .addHeader("Content-Type", "application/json")
                .setResponseCode(200)); // HTTP 200 OK

        // Call the getById method in the service
        Mono<BookingDto> result = bookingIntegrationService.getById("booking123");

        // Verify the result using StepVerifier
        StepVerifier.create(result)
                .expectNextMatches(booking -> booking.getId().equals("booking123"))
                .verifyComplete();

        // Verify the request sent to the MockWebServer
        okhttp3.mockwebserver.RecordedRequest request = mockWebServer.takeRequest();
        assert request.getMethod().equals("GET"); // Check HTTP method
        assert request.getPath().equals("/hotel123/bookings/booking123"); // Check request path
    }

    @Test
    void testCancel() throws InterruptedException {
        // Prepare the mock response from the server
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)); // HTTP 200 OK

        // Call the cancel method in the service
        Mono<Void> result = bookingIntegrationService.cancel("booking123");

        // Verify the result using StepVerifier
        StepVerifier.create(result)
                .verifyComplete();

        // Verify the request sent to the MockWebServer
        okhttp3.mockwebserver.RecordedRequest request = mockWebServer.takeRequest();
        assert request.getMethod().equals("POST"); // Check HTTP method
        assert request.getPath().equals("/hotel123/bookings/booking123/cancel"); // Check request path
    }

    @Test
    void testFind() throws InterruptedException {
        // Prepare the mock response from the server
        String responseBody = "[{\"id\":\"booking123\"}, {\"id\":\"booking456\"}]"; // JSON response body
        mockWebServer.enqueue(new MockResponse()
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json")
                .setResponseCode(200)); // HTTP 200 OK

        // Prepare the filter for searching bookings
        BookingFilter filter = new BookingFilter();
        filter.setUserId("user123");

        // Call the find method in the service
        reactor.core.publisher.Flux<BookingDto> result = bookingIntegrationService.find(filter);

        // Verify the result using StepVerifier
        StepVerifier.create(result)
                .expectNextMatches(booking -> booking.getId().equals("booking123"))
                .expectNextMatches(booking -> booking.getId().equals("booking456"))
                .verifyComplete();

        // Verify the request sent to the MockWebServer
        okhttp3.mockwebserver.RecordedRequest request = mockWebServer.takeRequest();
        assert request.getMethod().equals("GET"); // Check HTTP method
        assert request.getPath().contains("/hotel123/bookings"); // Check request path
    }

    @Test
    void testCreateThrowsException() throws InterruptedException {
        // Prepare the mock response from the server to simulate an error
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500) // HTTP 500 Internal Server Error
                .setBody("Internal Server Error"));

        // Call the create method in the service
        Mono<BookingDto> result = bookingIntegrationService.create(new BookingDto());

        // Verify that the service throws BookingException
        StepVerifier.create(result)
                .expectError(BookingException.class)
                .verify();

        // Verify the request sent to the MockWebServer
        okhttp3.mockwebserver.RecordedRequest request = mockWebServer.takeRequest();
        assert request.getMethod().equals("POST"); // Check HTTP method
        assert request.getPath().equals("/hotel123/bookings"); // Check request path
    }

}