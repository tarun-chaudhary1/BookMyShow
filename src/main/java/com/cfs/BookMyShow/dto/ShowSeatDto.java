package com.cfs.BookMyShow.dto;


import com.cfs.BookMyShow.model.Booking;
import com.cfs.BookMyShow.model.Seat;
import com.cfs.BookMyShow.model.Show;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShowSeatDto {

    private Long id;

    private SeatDto seat;

    private String status;

    private Double price;

}
