package com.github.cstettler.dddttc.rental.infrastructure.web;

import com.github.cstettler.dddttc.rental.application.BikeService;
import com.github.cstettler.dddttc.rental.application.BookingService;
import com.github.cstettler.dddttc.rental.domain.bike.Bike;
import com.github.cstettler.dddttc.rental.domain.bike.BikeAlreadyBookedException;
import com.github.cstettler.dddttc.rental.domain.bike.BikeNotExistingException;
import com.github.cstettler.dddttc.rental.domain.bike.NumberPlate;
import com.github.cstettler.dddttc.rental.domain.booking.Booking;
import com.github.cstettler.dddttc.rental.domain.booking.BookingAlreadyCompletedException;
import com.github.cstettler.dddttc.rental.domain.booking.BookingId;
import com.github.cstettler.dddttc.rental.domain.user.UserId;
import com.github.cstettler.dddttc.rental.domain.user.UserNotExistingException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static com.github.cstettler.dddttc.rental.domain.bike.NumberPlate.numberPlate;
import static com.github.cstettler.dddttc.rental.domain.booking.BookingId.bookingId;
import static com.github.cstettler.dddttc.rental.domain.user.UserId.userId;
import static com.github.cstettler.dddttc.rental.infrastructure.web.RentalController.BikeViewModel.toBikeViewModel;
import static com.github.cstettler.dddttc.rental.infrastructure.web.RentalController.BikeViewModel.toBikeViewModels;
import static com.github.cstettler.dddttc.rental.infrastructure.web.RentalController.BookingViewModel.toBookingViewModels;
import static com.github.cstettler.dddttc.support.infrastructure.web.ModelAndViewBuilder.modelAndView;
import static com.github.cstettler.dddttc.support.infrastructure.web.ModelAndViewBuilder.redirectTo;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@Controller
@RequestMapping("/rental")
class RentalController {

    private final BookingService bookingService;
    private final BikeService bikeService;

    RentalController(BookingService bookingService, BikeService bikeService) {
        this.bookingService = bookingService;
        this.bikeService = bikeService;
    }

    @GetMapping("/bikes")
    ModelAndView listBikes() {
        List<Bike> bikes = this.bikeService.listBikes().stream()
                .sorted(comparing((bike) -> bike.numberPlate().value()))
                .collect(toList());

        return modelAndView("list-bikes")
                .property("bikes", toBikeViewModels(bikes))
                .build();
    }

    @GetMapping("/bookings")
    ModelAndView listBookings() {
        List<Booking> bookings = this.bookingService.listBookings().stream()
                .sorted(comparing((booking) -> booking.id().value()))
                .collect(toList());

        return modelAndView("list-bookings")
                .property("bookings", toBookingViewModels(bookings))
                .build();
    }

    @GetMapping("/bookings/new")
    ModelAndView displayBookingForm(@RequestParam("number-plate") String numberPlateValue) {
        NumberPlate numberPlate = numberPlate(numberPlateValue);

        try {
            Bike bike = this.bikeService.getBike(numberPlate);

            return modelAndView("book-bike")
                    .property("bike", toBikeViewModel(bike))
                    .build();
        } catch (BikeNotExistingException e) {
            return modelAndView("error")
                    .error(e)
                    .build();
        }
    }

    @PostMapping("/bookings")
    ModelAndView bookBike(@RequestParam("number-plate") String numberPlateValue, @RequestParam("user-id") String userIdValue) {
        NumberPlate numberPlate = numberPlate(numberPlateValue);
        UserId userId = userId(userIdValue);

        try {
            this.bookingService.bookBike(numberPlate, userId);

            return redirectTo("/rental/bookings");
        } catch (BikeNotExistingException | UserNotExistingException | BikeAlreadyBookedException e) {
            return modelAndView("error")
                    .error(e)
                    .build();
        }
    }

    @PutMapping("/bookings")
    ModelAndView returnBike(@RequestParam("booking-id") String bookingIdValue) {
        BookingId bookingId = bookingId(bookingIdValue);

        try {
            this.bookingService.returnBike(bookingId);

            return redirectTo("/rental/bookings");
        } catch (BookingAlreadyCompletedException e) {
            return modelAndView("error")
                    .error(e)
                    .build();
        }
    }


    static class BikeViewModel {

        public final String numberPlate;
        public final boolean available;

        private BikeViewModel(Bike bike) {
            this.numberPlate = bike.numberPlate().value();
            this.available = bike.available();
        }

        static BikeViewModel toBikeViewModel(Bike bike) {
            return new BikeViewModel(bike);
        }

        static List<BikeViewModel> toBikeViewModels(List<Bike> bikes) {
            return bikes.stream()
                    .map((bike) -> toBikeViewModel(bike))
                    .collect(toList());
        }

    }


    static class BookingViewModel {

        public final String id;
        public final String numberPlate;
        public final String userId;
        public final boolean completed;

        private BookingViewModel(Booking booking) {
            this.id = booking.id().value();
            this.numberPlate = booking.numberPlate().value();
            this.userId = booking.userId().value();
            this.completed = booking.completed();
        }

        static BookingViewModel toBookingViewModel(Booking booking) {
            return new BookingViewModel(booking);
        }

        static List<BookingViewModel> toBookingViewModels(List<Booking> bookings) {
            return bookings.stream()
                    .map((booking) -> toBookingViewModel(booking))
                    .collect(toList());
        }

    }

}

