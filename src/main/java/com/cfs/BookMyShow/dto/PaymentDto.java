    package com.cfs.BookMyShow.dto;


    import com.cfs.BookMyShow.Enum.BookingStatus;
    import jakarta.persistence.Column;
    import jakarta.persistence.EnumType;
    import jakarta.persistence.Enumerated;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.time.LocalDateTime;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class PaymentDto {

        private Long id;

        private String transactionId;
        private String paymentMethod;

        private Double amount;


        private LocalDateTime paymentTime;

        @Enumerated(EnumType.STRING)
        private BookingStatus status;

    }
