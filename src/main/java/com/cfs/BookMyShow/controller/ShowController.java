package com.cfs.BookMyShow.controller;

import com.cfs.BookMyShow.dto.ShowDto;
import com.cfs.BookMyShow.service.ShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shows")
public class ShowController {

    @Autowired
    private ShowService showService;

    // 1️⃣ Get shows by movie id (required by movie.html)
    @GetMapping
    public List<ShowDto> getShowsByMovie(@RequestParam Long movieId) {
        return showService.getShowByMovieId(movieId);
    }

    // 2️⃣ Get single show by id (required by seats.html)
    @GetMapping("/{id}")
    public ShowDto getShowById(@PathVariable Long id) {
        return showService.getShowById(id);
    }
}
