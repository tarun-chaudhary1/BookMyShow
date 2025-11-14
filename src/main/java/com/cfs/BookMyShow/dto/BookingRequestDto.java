package com.cfs.BookMyShow.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDto {

    private Long userId;

    private Long showId;

    private List<Long> seatIds;

    private String paymentMethod;
}
