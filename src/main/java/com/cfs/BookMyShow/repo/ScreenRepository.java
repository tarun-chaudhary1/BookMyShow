package com.cfs.BookMyShow.repo;

import com.cfs.BookMyShow.model.Booking;
import com.cfs.BookMyShow.model.Screen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScreenRepository extends JpaRepository<Screen,Long>{

    List<Screen> findByTheaterId(Long theaterId);
}
