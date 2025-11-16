package com.cfs.BookMyShow.service;


import com.cfs.BookMyShow.dto.*;
import com.cfs.BookMyShow.exception.ResourceNotFoundException;
import com.cfs.BookMyShow.model.*;
import com.cfs.BookMyShow.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShowService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private ShowSeatRepository showSeatRepository;


    public ShowDto crateShow(ShowDto showDto)
    {
        Show show= new Show();

        Movie movie=movieRepository.findById(showDto.getMovie().getId())
                .orElseThrow(()-> new ResourceNotFoundException("Movie not found") );

        Screen screen=screenRepository.findById(showDto.getScreen().getId())
                .orElseThrow(()-> new ResourceNotFoundException("Screen not found") );


        show.setMovie(movie);
        show.setScreen(screen);
        show.setId(showDto.getId());
        show.setStartTime(showDto.getStartTime());

        show.setEndTime(showDto.getEndTime());

        Show saveShow=showRepository.save(show);

        List<ShowSeat> availableSeats=showSeatRepository.findByShowIdAndStatus(saveShow.getId(),"AVAILABLE");

        return mapToDto(saveShow,availableSeats);

    }

    public ShowDto getShowById(Long id)

    {
        Show show=showRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Show not found"));

        List<ShowSeat> availableSeats=showSeatRepository.findByShowIdAndStatus(show.getId(),"AVAILABLE");

        return mapToDto(show,availableSeats);
    }

    public List<ShowDto> getAllShows()
    {
        List<Show> shows=showRepository.findAll();
        return shows
            .stream()
            .map(show->{
                List<ShowSeat> availableSeats=showSeatRepository.findByShowIdAndStatus(show.getId(),"AVAILABLE");

                return mapToDto(show,availableSeats);
            }).collect(Collectors.toList());
    }
     public List<ShowDto> getShowByMovieId(Long movieId)
        {
            List<Show> shows=showRepository.findByMovieId(movieId);
            return shows
                .stream()
                .map(show->{
                    List<ShowSeat> availableSeats=showSeatRepository.findByShowIdAndStatus(show.getId(),"AVAILABLE");

                    return mapToDto(show,availableSeats);
                }).collect(Collectors.toList());
        }
        public List<ShowDto> getShowByMovieIdAndCity(Long movieId,String city)
        {
            List<Show> shows=showRepository.findByMovie_IdAndScreen_Theater_City(movieId,city);
            return shows
                .stream()
                .map(show->{
                    List<ShowSeat> availableSeats=showSeatRepository.findByShowIdAndStatus(show.getId(),"AVAILABLE");

                    return mapToDto(show,availableSeats);
                }).collect(Collectors.toList());
        }
        public List<ShowDto> getShowByDateRange(LocalDateTime startDate,LocalDateTime endDate)
                {
                    List<Show> shows=showRepository.findByStartTimeBetween(startDate,endDate);
                    return shows
                        .stream()
                        .map(show->{
                            List<ShowSeat> availableSeats=showSeatRepository.findByShowIdAndStatus(show.getId(),"AVAILABLE");

                            return mapToDto(show,availableSeats);
                        }).collect(Collectors.toList());
                }



    private ShowDto mapToDto(Show show,List<ShowSeat> availableSeats)
    {
        ShowDto showDto=new ShowDto();

        showDto.setId(show.getId());
        showDto.setStartTime(show.getStartTime());
        showDto.setEndTime(show.getEndTime());

        MovieDto movieDto=new MovieDto();
        movieDto.setId(show.getMovie().getId());
        movieDto.setTitle(show.getMovie().getTitle());
        movieDto.setDescription(show.getMovie().getDescription());
        movieDto.setPosterUrl(show.getMovie().getPosterUrl());
        movieDto.setLanguage(show.getMovie().getLanguage());
        movieDto.setGenre(show.getMovie().getGenre());
        movieDto.setDurationMins(show.getMovie().getDurationMins());
        movieDto.setReleaseDate(show.getMovie().getReleaseDate());

        showDto.setMovie(movieDto);


        TheaterDto theaterDto=new TheaterDto();
        theaterDto.setId(show.getScreen().getTheater().getId());
        theaterDto.setName(show.getScreen().getTheater().getName());
        theaterDto.setCity(show.getScreen().getTheater().getCity());
        theaterDto.setAddress(show.getScreen().getTheater().getAddress());
        theaterDto.setTotalScreen(show.getScreen().getTheater().getTotalScreen());

        showDto.setScreen(new ScreenDto(

                show.getScreen().getId(),
                show.getScreen().getName(),
                show.getScreen().getTotalSeats(),
                theaterDto

        ));

        List<ShowSeatDto> seats=availableSeats
                .stream()
                        .map(seat->{
                            ShowSeatDto showSeatDto=new ShowSeatDto();
                            showSeatDto.setId(seat.getId());
                            showSeatDto.setStatus(seat.getStatus());
                            showSeatDto.setPrice(seat.getPrice());

                            SeatDto seatDto=new SeatDto();

                            seatDto.setId(seat.getSeat().getId());
                            seatDto.setSeatType(seat.getSeat().getSeatType());
                            seatDto.setSeatNumber(seat.getSeat().getSeatNumber());
                            seatDto.setBasePrice(seat.getSeat().getBasePrice());

                            showSeatDto.setSeat(seatDto);

                            return showSeatDto;

                        }).collect(Collectors.toList());


        showDto.setAvailableSeats(seats);
        return showDto;
    }
}
