package com.cfs.BookMyShow.repo;

import com.cfs.BookMyShow.model.Show;
import com.cfs.BookMyShow.model.ShowSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowSeatRepository extends JpaRepository<ShowSeat,Long>{

    List<ShowSeat> findByShowId(Long showId);
    List<ShowSeat> findByShowIdAndStatus(Long showId,String status);

}
