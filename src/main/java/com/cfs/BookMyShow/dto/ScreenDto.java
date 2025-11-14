package com.cfs.BookMyShow.dto;


import com.cfs.BookMyShow.model.Show;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScreenDto {

    private Long id;

    private String name;

    private Integer totalSeats;

    private TheaterDto theater;

}
