package com.cfs.BookMyShow.service;

import com.cfs.BookMyShow.dto.MovieDto;
import com.cfs.BookMyShow.exception.ResourceNotFoundException;
import com.cfs.BookMyShow.model.Movie;
import com.cfs.BookMyShow.model.Show;
import com.cfs.BookMyShow.repo.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieService {


    @Autowired
    private MovieRepository movieRepository;


    public MovieDto createMovie(MovieDto movieDto){

        Movie movie=mapToEntity(movieDto);

        Movie saveMovie=movieRepository.save(movie);

        return mapToDto(saveMovie);

    }

    public MovieDto getMovieById(Long id){

        Movie movie=movieRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Movie not found"));

        return mapToDto(movie);

    }

    public List<MovieDto> getAllMoives()
    {
        List<Movie> movies=movieRepository.findAll();

        return movies.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }


    public List<MovieDto> getMovieByLanguage(String language)
    {
        List<Movie> movies=movieRepository.findByLanguage(language);

        return movies.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    public List<MovieDto> getMovieByGenre(String genre)
        {
            List<Movie> movies=movieRepository.findByGenre(genre);

            return movies.stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList());
        }
    public List<MovieDto> searchMovie(String title)
            {
                List<Movie> movies=movieRepository.findByTitleContaining(title);

                return movies.stream()
                        .map(this::mapToDto)
                        .collect(Collectors.toList());
            }


    public MovieDto updateMovie(Long id,MovieDto movieDto){

        Movie movie=movieRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Movie Not found"));

        movie.setId(movieDto.getId());
        movie.setDescription(movieDto.getDescription());
        movie.setGenre(movieDto.getGenre());
        movie.setTitle(movieDto.getTitle());
        movie.setReleaseDate(movieDto.getReleaseDate());
        movie.setDurationMins(movieDto.getDurationMins());
        movie.setPosterUrl(movieDto.getPosterUrl());
        movie.setLanguage(movieDto.getLanguage());

        Movie saveMovie=movieRepository.save(movie);

        return mapToDto(saveMovie);
    }

    public void deleteMovie(Long id)
    {
        Movie movie=movieRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Movie Not found"));

        movieRepository.delete(movie);
    }

    private Movie mapToEntity(MovieDto movieDto)
    {
        Movie movie=new Movie();

        movie.setId(movieDto.getId());
        movie.setDescription(movieDto.getDescription());
        movie.setGenre(movieDto.getGenre());
        movie.setTitle(movieDto.getTitle());
        movie.setReleaseDate(movieDto.getReleaseDate());
        movie.setDurationMins(movieDto.getDurationMins());
        movie.setPosterUrl(movieDto.getPosterUrl());
        movie.setLanguage(movieDto.getLanguage());

        return movie;
    }

    private MovieDto mapToDto(Movie movie)
    {
        MovieDto movieDto=new MovieDto();

        movieDto.setId(movie.getId());
        movieDto.setGenre(movie.getGenre());
        movieDto.setLanguage(movie.getLanguage());
        movieDto.setTitle(movie.getTitle());
        movieDto.setReleaseDate(movie.getReleaseDate());
        movieDto.setDurationMins(movie.getDurationMins());
        movieDto.setPosterUrl(movie.getPosterUrl());
        movieDto.setDescription(movie.getDescription());

        return movieDto;

    }
}
