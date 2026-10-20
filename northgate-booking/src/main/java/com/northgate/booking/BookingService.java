package com.northgate.booking;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/** In-memory booking rules for Northgate Physiotherapy (no database in the starter app). */
public class BookingService {

    /** Cancelling less than this long before the appointment counts as a late cancellation. */
    public static final Duration LATE_CANCELLATION_WINDOW = Duration.ofHours(24);

    private final Map<Long, Slot> slots = new ConcurrentHashMap<>();
    private final Map<Long, Booking> bookings = new ConcurrentHashMap<>();
    private final AtomicLong ids = new AtomicLong(1);

    public Slot addSlot(String clinic, AppointmentType type, LocalDateTime start) {
        Slot slot = new Slot(ids.getAndIncrement(), clinic, type, start);
        slots.put(slot.id(), slot);
        return slot;
    }

    /** Free slots at a clinic, earliest first. */
    public List<Slot> availableSlots(String clinic) {
        List<Slot> result = new ArrayList<>();
        for (Slot slot : slots.values()) {
            boolean taken = bookings.values().stream().anyMatch(b -> b.slotId() == slot.id());
            if (!taken && slot.clinic().equalsIgnoreCase(clinic)) {
                result.add(slot);
            }
        }
        result.sort(Comparator.comparing(Slot::start));
        return result;
    }

    /** Books a free slot for a customer. Fails if the slot doesn't exist or is already taken. */
    public synchronized Booking book(long slotId, String customerEmail) {
        Slot slot = slots.get(slotId);
        if (slot == null) {
            throw new IllegalArgumentException("No such slot: " + slotId);
        }
        if (customerEmail == null || !customerEmail.contains("@")) {
            throw new IllegalArgumentException("A valid email address is required");
        }
        boolean taken = bookings.values().stream().anyMatch(b -> b.slotId() == slotId);
        if (taken) {
            throw new IllegalStateException("Slot already booked: " + slotId);
        }
        Booking booking = new Booking(ids.getAndIncrement(), slotId, customerEmail);
        bookings.put(booking.id(), booking);
        return booking;
    }

    /** Cancels a booking and frees its slot. Returns true if it was a late cancellation. */
    public synchronized boolean cancel(long bookingId, LocalDateTime now) {
        Booking booking = Optional.ofNullable(bookings.remove(bookingId))
                .orElseThrow(() -> new IllegalArgumentException("No such booking: " + bookingId));
        Slot slot = slots.get(booking.slotId());
        return isLateCancellation(slot.start(), now);
    }

    /** True when the appointment starts less than 24 hours from now. */
    public static boolean isLateCancellation(LocalDateTime appointmentStart, LocalDateTime now) {
        return Duration.between(now, appointmentStart).compareTo(LATE_CANCELLATION_WINDOW) < 0;
    }

    public enum AppointmentType {
        PHONE_REVIEW(15), FOLLOW_UP(30), INITIAL_ASSESSMENT(60);

        private final int minutes;

        AppointmentType(int minutes) {
            this.minutes = minutes;
        }

        public int minutes() {
            return minutes;
        }
    }

    public record Slot(long id, String clinic, AppointmentType type, LocalDateTime start) {}

    public record Booking(long id, long slotId, String customerEmail) {}
}
