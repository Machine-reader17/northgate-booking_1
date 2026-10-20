package com.northgate.booking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.northgate.booking.BookingService.AppointmentType;
import com.northgate.booking.BookingService.Booking;
import com.northgate.booking.BookingService.Slot;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookingServiceTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 3, 2, 9, 0);
    private BookingService service;
    private Slot leedsMorning;

    @BeforeEach
    void setUp() {
        service = new BookingService();
        leedsMorning = service.addSlot("Leeds", AppointmentType.FOLLOW_UP, NOW.plusDays(2));
        service.addSlot("Leeds", AppointmentType.PHONE_REVIEW, NOW.plusDays(1));
        service.addSlot("York", AppointmentType.INITIAL_ASSESSMENT, NOW.plusDays(1));
    }

    @Test
    void listsFreeSlotsForAClinicEarliestFirst() {
        var slots = service.availableSlots("leeds");
        assertEquals(2, slots.size());
        assertTrue(slots.get(0).start().isBefore(slots.get(1).start()));
    }

    @Test
    void bookedSlotIsNoLongerAvailable() {
        service.book(leedsMorning.id(), "pat@example.com");
        assertEquals(1, service.availableSlots("Leeds").size());
    }

    @Test
    void cannotDoubleBookASlot() {
        service.book(leedsMorning.id(), "pat@example.com");
        assertThrows(IllegalStateException.class, () -> service.book(leedsMorning.id(), "sam@example.com"));
    }

    @Test
    void rejectsInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () -> service.book(leedsMorning.id(), "not-an-email"));
    }

    @Test
    void cancellingFreesTheSlot() {
        Booking booking = service.book(leedsMorning.id(), "pat@example.com");
        service.cancel(booking.id(), NOW);
        assertEquals(2, service.availableSlots("Leeds").size());
    }

    @Test
    void cancellationMoreThan24HoursAheadIsNotLate() {
        assertFalse(BookingService.isLateCancellation(NOW.plusHours(30), NOW));
    }

    @Test
    void cancellationInsideTheWindowIsLate() {
        assertTrue(BookingService.isLateCancellation(NOW.plusHours(3), NOW));
    }

    @Test
    void exactly24HoursAheadIsNotLate() {
        assertFalse(BookingService.isLateCancellation(NOW.plusHours(24), NOW));
    }
}
