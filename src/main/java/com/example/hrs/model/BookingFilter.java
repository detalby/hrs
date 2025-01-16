package com.example.hrs.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Data
public class BookingFilter {
    private String apartmentId;
    private String userId;
    private String bookingStatus;
    private LocalDate created;
    private Long page;
    private Long limit;
}
