package com.cfs.BookMyShow.dto;


import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatDto {

    private Long id;
    private String seatNumber;

    private String seatType;

    private Double  basePrice;


}
