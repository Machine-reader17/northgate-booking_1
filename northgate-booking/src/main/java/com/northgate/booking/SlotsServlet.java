package com.northgate.booking;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/** GET /api/slots?clinic=Leeds returns the free appointment slots as JSON. */
@WebServlet("/api/slots")
public class SlotsServlet extends HttpServlet {

    private final BookingService service = new BookingService();

    @Override
    public void init() throws ServletException {
        // Demo data: the next three days of slots at two clinics.
        LocalDate today = LocalDate.now();
        for (int day = 1; day <= 3; day++) {
            for (String clinic : List.of("Leeds", "York")) {
                service.addSlot(clinic, BookingService.AppointmentType.FOLLOW_UP,
                        LocalDateTime.of(today.plusDays(day), LocalTime.of(9, 0)));
                service.addSlot(clinic, BookingService.AppointmentType.INITIAL_ASSESSMENT,
                        LocalDateTime.of(today.plusDays(day), LocalTime.of(14, 0)));
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String clinic = req.getParameter("clinic") == null ? "Leeds" : req.getParameter("clinic");
        String json = service.availableSlots(clinic).stream()
                .map(s -> String.format("{\"id\":%d,\"clinic\":\"%s\",\"type\":\"%s\",\"minutes\":%d,\"start\":\"%s\"}",
                        s.id(), s.clinic(), s.type(), s.type().minutes(), s.start()))
                .collect(Collectors.joining(",", "[", "]"));
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json);
    }
}
