package com.example.hrs.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private String id;
    private String userId;
    private String apartmentId;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private String status;
}
