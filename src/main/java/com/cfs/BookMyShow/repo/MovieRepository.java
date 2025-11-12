package com.cfs.BookMyShow.repo;

import com.cfs.BookMyShow.model.Booking;
import com.cfs.BookMyShow.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie,Long>{

    List<Movie> findByLanguage(String language);

    List<Movie> findByTitleContaining(String title);

    List<Movie> findByGenre(String genre);


}
