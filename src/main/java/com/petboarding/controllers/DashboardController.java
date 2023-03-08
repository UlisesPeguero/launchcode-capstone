package com.petboarding.controllers;

import com.petboarding.controllers.utils.CalendarEvent;
import com.petboarding.controllers.utils.CalendarEventUtils;
import com.petboarding.models.Reservation;
import com.petboarding.models.Stay;
import com.petboarding.models.app.Module;
import com.petboarding.models.data.ConfigurationRepository;
import com.petboarding.models.data.ReservationRepository;
import com.petboarding.models.data.StayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.function.Function;

@Controller
public class DashboardController extends AppBaseController {
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private StayRepository stayRepository;
    @Autowired
    private ConfigurationRepository configurationRepository;

    @GetMapping
    public String showIndex() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String showDashboardHome(Model model) {
        Function<Stay, String> getStayTitle = stay ->
                "#" + stay.getReservation().getConfirmation() + " | " +
                        stay.getKennel().getName() + " | " +
                        stay.getReservation().getPet().getPetName();
        Function<Reservation, String> getTitle = reservation ->
                "#" + reservation.getConfirmation() + " | " +
                        reservation.getPet().getOwner().getFullName() + " | " +
                        reservation.getPet().getPetName();
        List<CalendarEvent> events = CalendarEventUtils.parseEventsFromReservations(
                reservationRepository.findByStayIsNull(),
                getTitle,
                configurationRepository.findByName("RESERVATION_COLOR").getValue());
        List<CalendarEvent> stayEvents = CalendarEventUtils.parseEventsFromStays(
                stayRepository.findAll(),
                getStayTitle,
                configurationRepository.findByName("STAY_COLOR").getValue());

        events.addAll(stayEvents);
        model.addAttribute("events", events);
        return "index";

    }

    @GetMapping("/testError")
    public String showError(Model model) {
        model.addAttribute("errorMessage", "Something went wrong somewhere.");
        return "index";
    }

    @GetMapping("/testInfo")
    public String showInfo(Model model) {
        model.addAttribute("infoMessage", "Something has information for you or something.");
        addLocation("2nd level/3rd level", model);
        return "index";
    }

    @ModelAttribute
    public void addActiveModule(Model model) {
        setActiveModule("home", model);
    }

}
