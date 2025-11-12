package com.cfs.BookMyShow.model;


import com.cfs.BookMyShow.Enum.BookingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false,unique = true)
    private String transactionId;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private LocalDateTime paymentTime;


    @Column(nullable = false)
    private String paymentMethod;

    // ✅ use enum here
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @OneToOne(mappedBy = "payment")
    private Booking booking;

}
