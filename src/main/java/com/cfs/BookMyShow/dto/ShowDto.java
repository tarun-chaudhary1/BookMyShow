package com.cfs.BookMyShow.dto;


import com.cfs.BookMyShow.model.Booking;
import com.cfs.BookMyShow.model.Movie;
import com.cfs.BookMyShow.model.Screen;
import com.cfs.BookMyShow.model.ShowSeat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShowDto {

    private Long id;


    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private MovieDto movie;

    private ScreenDto screen;

    private List<ShowSeatDto> availableSeats;

}
