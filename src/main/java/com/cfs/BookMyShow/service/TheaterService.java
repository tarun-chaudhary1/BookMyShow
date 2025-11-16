package com.cfs.BookMyShow.service;

import com.cfs.BookMyShow.dto.ScreenDto;
import com.cfs.BookMyShow.dto.TheaterDto;
import com.cfs.BookMyShow.exception.ResourceNotFoundException;
import com.cfs.BookMyShow.model.Theater;
import com.cfs.BookMyShow.repo.TheaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TheaterService {


    @Autowired
    private TheaterRepository theaterRepository;

    public TheaterDto createTheater(TheaterDto theaterDto)
    {

        Theater theater=mapToEntity(theaterDto);
        Theater saveTheater=theaterRepository.save(theater);
        return mapToDto(saveTheater);

    }

    public TheaterDto getTheaterById(Long id){

        Theater theater=theaterRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Theater not found"));

        return mapToDto(theater);
    }

    public List<TheaterDto> getAllTheater()
    {
        List<Theater> theaters=theaterRepository.findAll();

        return theaters.stream()
                .map(this::mapToDto).collect(Collectors.toList());
    }
    public List<TheaterDto> getAllByCity(String city)
    {
        List<Theater> theaters=theaterRepository.findByCity(city);

        return theaters.stream()
                .map(this::mapToDto).collect(Collectors.toList());
    }

    public TheaterDto updateTheater(Long id,TheaterDto theaterDto)
    {
        Theater theater=theaterRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Theater not found"));

        theater.setId(theaterDto.getId());
        theater.setName(theaterDto.getName());
        theater.setAddress(theaterDto.getAddress());
        theater.setTotalScreen(theaterDto.getTotalScreen());
        theater.setCity(theaterDto.getCity());

        Theater saveTheater=theaterRepository.save(theater);
        return mapToDto(saveTheater);

    }

    public void deleteTheater(Long id)
    {
        Theater theater=theaterRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Theater not found"));

        theaterRepository.delete(theater);
    }
    private Theater mapToEntity(TheaterDto theaterDto){

        Theater theater=new Theater();

        theater.setId(theaterDto.getId());
        theater.setCity(theaterDto.getCity());
        theater.setAddress(theaterDto.getAddress());
        theater.setName(theaterDto.getName());
        theater.setTotalScreen(theaterDto.getTotalScreen());
        return theater;

    }

    private TheaterDto mapToDto(Theater theater){

        TheaterDto theaterDto =new TheaterDto();
        theaterDto.setId(theater.getId());
        theaterDto.setName(theater.getName());
        theaterDto.setCity(theater.getCity());
        theaterDto.setAddress(theater.getAddress());
        theaterDto.setTotalScreen(theater.getTotalScreen());

        return theaterDto;
    }

}
