package com.cfs.BookMyShow.dto;


import com.cfs.BookMyShow.model.Payment;
import com.cfs.BookMyShow.model.Show;
import com.cfs.BookMyShow.model.ShowSeat;
import com.cfs.BookMyShow.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {

    private Long id;

    private String bookingNumber;

    private LocalDateTime bookingTime;

    private UserDto user;

    private ShowDto show;

    private String status;

    private Double totalPrice;

    private List<ShowSeatDto> showSeats;

    private PaymentDto payment;
}
