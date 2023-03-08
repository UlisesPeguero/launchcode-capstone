package com.petboarding.controllers.utils;

import com.petboarding.models.Reservation;
import com.petboarding.models.Stay;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CalendarEventUtils {

    static private <T> List<CalendarEvent> parseEventsList(
            List<T> rows,
            Function<T, Reservation> getReservation, // point to reservation object
            Function<T, String> getTitle, // get title to display
            String url, // url to display detail + id
            String color) { // url for color highlighting the event

        List<CalendarEvent> eventsList = new ArrayList<>();
        for (T row : rows) {
            Reservation reservation = getReservation.apply(row);
            CalendarEvent event = new CalendarEvent(
                    reservation.getId(),
                    reservation.getStartDateTime(),
                    reservation.getEndDateTime(),
                    getTitle.apply(row));
            if (url != null)
                event.setUrl(url + reservation.getId());
            if (color != null)
                event.setColor(color);
            eventsList.add(event);
        }
        return eventsList;
    }

    static public List<CalendarEvent> parseEventsFromReservations(
            List<Reservation> reservations,
            Function<Reservation, String> getTitle, String color) {
        return parseEventsList(
                reservations,
                reservation -> reservation,
                getTitle,
                "/reservations/detail?reservationId=",
                color

        );
    }

    static public List<CalendarEvent> parseEventsFromStays(List<Stay> stays, Function<Stay, String> getTitle,
            String color) {
        Function<Stay, Reservation> getReservation = stay -> stay.getReservation();
        // Function<Stay, String> getTitle = stay ->
        // stay.getReservation().getComments();

        return parseEventsList(
                stays,
                getReservation,
                getTitle,
                "/stays/update/",
                color);
    }
}
