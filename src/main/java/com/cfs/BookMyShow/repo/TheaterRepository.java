package com.cfs.BookMyShow.repo;

import com.cfs.BookMyShow.model.ShowSeat;
import com.cfs.BookMyShow.model.Theater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TheaterRepository extends JpaRepository<Theater,Long>{

    List<Theater> findByShowId(String city);

}
