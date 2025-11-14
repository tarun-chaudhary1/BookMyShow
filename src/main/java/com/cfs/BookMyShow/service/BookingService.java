package com.cfs.BookMyShow.service;


import com.cfs.BookMyShow.Enum.BookingStatus;
import com.cfs.BookMyShow.dto.*;
import com.cfs.BookMyShow.exception.ResourceNotFoundException;
import com.cfs.BookMyShow.exception.SeatUnavailableException;
import com.cfs.BookMyShow.model.*;
import com.cfs.BookMyShow.repo.BookingRepository;
import com.cfs.BookMyShow.repo.ShowRepository;
import com.cfs.BookMyShow.repo.ShowSeatRepository;
import com.cfs.BookMyShow.repo.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.print.Book;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private ShowSeatRepository showSeatRepository;
    @Autowired
    private BookingRepository bookingRepository;

    @Transactional
    public BookingDto createBooking (BookingRequestDto bookingRequest){

        User user=userRepository.findById(bookingRequest.getUserId())
                .orElseThrow(()-> new ResourceNotFoundException("User Not Found"));

        Show show=showRepository.findById(bookingRequest.getShowId())
                .orElseThrow(()->new ResourceNotFoundException("Show not found"));

        List<ShowSeat> selectedSeats=showSeatRepository.findAllById(bookingRequest.getSeatIds());

        for(ShowSeat seat:selectedSeats)
        {
            if(!"AVAILABLE".equals(seat.getStatus())){
                throw new SeatUnavailableException("Seat is not available at:"+seat.getSeat().getSeatNumber());
            }
            seat.setStatus("LOCKED");
        }
        showSeatRepository.saveAll(selectedSeats);

        Double totalAmount=selectedSeats.stream()
                .mapToDouble(ShowSeat::getPrice)
                .sum();
        Payment payment=new Payment();
        payment.setAmount(totalAmount);
        payment.setPaymentTime(LocalDateTime.now());
        payment.setPaymentMethod(bookingRequest.getPaymentMethod());
        payment.setStatus(BookingStatus.SUCCESS);
        payment.setTransactionId(UUID.randomUUID().toString());


        Booking booking= new Booking();
        booking.setUser(user);
        booking.setShow(show);
        booking.setTotalAmount(totalAmount);
        booking.setBookingTime(LocalDateTime.now());
        booking.setBookingNumber(UUID.randomUUID().toString());
        booking.setStatus("CONFIRMED");
//        booking.setShowSeat(selectedSeats);
        booking.setPayment(payment);

        Booking saveBooking=bookingRepository.save(booking);

        selectedSeats.forEach(seat->{
            seat.setStatus("BOOKED");
            seat.setBooking(saveBooking);
        });
        showSeatRepository.saveAll(selectedSeats);

        return mapToBookingDto(saveBooking,selectedSeats);

    }

    public BookingDto getBookingById(Long id)
    {
        Booking booking=bookingRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Booking Not Found"));
        List<ShowSeat> seats=showSeatRepository.findAll()
                .stream()
                .filter(seat->seat.getBooking()!=null&&seat.getBooking().getId().equals(booking.getId()))
                .collect(Collectors.toList());
        return mapToBookingDto(booking,seats);
    }
    public BookingDto getBookingByNumber(String bookingNumber)
        {
            Booking booking=bookingRepository.findByBookingNumber(bookingNumber)
                    .orElseThrow(()-> new ResourceNotFoundException("Booking Not Found"));
            List<ShowSeat> seats=showSeatRepository.findAll()
                    .stream()
                    .filter(seat->seat.getBooking()!=null&&seat.getBooking().getId().equals(booking.getId()))
                    .collect(Collectors.toList());
            return mapToBookingDto(booking,seats);
        }
        public List<BookingDto> getBookingByUserId(Long userId)
        {
            List<Booking> bookings=bookingRepository.findByUserId(userId);

            return bookings.stream()
                    .map(booking -> {
                        List<ShowSeat> showSeats=showSeatRepository.findAll()
                                .stream()
                                .filter(seat->seat.getBooking()!=null&&seat.getBooking().getId().equals(booking.getId()))
                                .collect(Collectors.toList());
                        return mapToBookingDto(booking,showSeats);

                    }).collect(Collectors.toList());

        }

        @Transactional
        public BookingDto cancelBooking(Long id){

            Booking booking=bookingRepository.findById(id)
                    .orElseThrow(()->new ResourceNotFoundException("Booking not found"));

            booking.setStatus("CANCELLED");
            List<ShowSeat> seats=showSeatRepository.findAll()
                    .stream()
                    .filter(seat->seat.getBooking()!=null&&seat.getBooking().getId().equals(booking.getId()))
                    .collect(Collectors.toList());

            seats.forEach(seat->{
                seat.setStatus("AVAILABLE");
                seat.setBooking(null);
            });

            if(booking.getPayment()!=null){

                booking.getPayment().setStatus(BookingStatus.REFUND);
            }
            Booking updateBooking=bookingRepository.save(booking);
            showSeatRepository.saveAll(seats);
            return mapToBookingDto(updateBooking,seats);

        }

    private BookingDto mapToBookingDto(Booking booking,List<ShowSeat> seats){
        BookingDto bookingDto=new BookingDto();

        bookingDto.setId(booking.getId());
        bookingDto.setBookingNumber(booking.getBookingNumber());
        bookingDto.setBookingTime(booking.getBookingTime());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setTotalPrice(booking.getTotalAmount());
        UserDto userDto=new UserDto();
        userDto.setId(booking.getUser().getId());
        userDto.setName(booking.getUser().getName());
        userDto.setEmail(booking.getUser().getEmail());
        userDto.setPhoneNumber(booking.getUser().getPhoneNumber());
        bookingDto.setUser(userDto);

        ShowDto showDto=new ShowDto();
        showDto.setId(booking.getShow().getId());
        showDto.setStartTime(booking.getShow().getStartTime());
        showDto.setEndTime(booking.getShow().getEndTime());


        MovieDto movieDto=new MovieDto();
        movieDto.setId(booking.getShow().getMovie().getId());
        movieDto.setDescription(booking.getShow().getMovie().getDescription());
        movieDto.setGenre(booking.getShow().getMovie().getGenre());
        movieDto.setLanguage(booking.getShow().getMovie().getLanguage());
        movieDto.setTitle(booking.getShow().getMovie().getTitle());
        movieDto.setDurationMins(booking.getShow().getMovie().getDurationMins());
        movieDto.setReleaseDate(booking.getShow().getMovie().getReleaseDate());
        movieDto.setPosterUrl(booking.getShow().getMovie().getPosterUrl());

        showDto.setMovie(movieDto);

        ScreenDto screenDto=new ScreenDto();
        screenDto.setId(booking.getShow().getScreen().getId());
        screenDto.setName(booking.getShow().getScreen().getName());
        screenDto.setTotalSeats(booking.getShow().getScreen().getTotalSeats());


        TheaterDto theaterDto=new TheaterDto();
        theaterDto.setId(booking.getShow().getScreen().getTheater().getId());
        theaterDto.setName(booking.getShow().getScreen().getTheater().getName());
        theaterDto.setCity(booking.getShow().getScreen().getTheater().getCity());
        theaterDto.setAddress(booking.getShow().getScreen().getTheater().getAddress());
        theaterDto.setTotalScreen(booking.getShow().getScreen().getTheater().getTotalScreen());

        screenDto.setTheater(theaterDto);
        showDto.setScreen(screenDto);
        bookingDto.setShow(showDto);

       List<ShowSeatDto>seatDtos= seats.stream()
                .map(seat->{
                    ShowSeatDto showSeatDto= new ShowSeatDto();
                    showSeatDto.setId(seat.getId());
                    showSeatDto.setStatus(seat.getStatus());
                    showSeatDto.setPrice(seat.getPrice());

                    SeatDto seatDto=new SeatDto();
                    seatDto.setId(seat.getSeat().getId());
                    seatDto.setSeatNumber(seat.getSeat().getSeatNumber());
                    seatDto.setSeatType(seat.getSeat().getSeatType());
                    seatDto.setBasePrice(seat.getSeat().getBasePrice());
                    showSeatDto.setSeat(seatDto);
                    return showSeatDto;
                })
                .collect(Collectors.toList());
        bookingDto.setSeats(seatDtos);

        if(booking.getPayment()!=null){

            PaymentDto paymentDto=new PaymentDto();
            paymentDto.setId(booking.getPayment().getId());
            paymentDto.setAmount(booking.getPayment().getAmount());
            paymentDto.setStatus(booking.getPayment().getStatus());
            paymentDto.setPaymentMethod(booking.getPayment().getPaymentMethod());
            paymentDto.setTransactionId(booking.getPayment().getTransactionId());
            paymentDto.setPaymentTime(booking.getPayment().getPaymentTime());
            bookingDto.setPayment(paymentDto);
        }

        return bookingDto;
    }
}
