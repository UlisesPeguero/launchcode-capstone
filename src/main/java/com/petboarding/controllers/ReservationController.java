package com.petboarding.controllers;


import com.petboarding.controllers.utils.CalendarEvent;
import com.petboarding.controllers.utils.CalendarEventUtils;
import com.petboarding.models.*;
import com.petboarding.models.app.Module;
import com.petboarding.models.data.*;
import com.petboarding.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.swing.text.html.Option;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Controller
@RequestMapping("reservations")
public class ReservationController extends AppBaseController{
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private PetRepository petRepository;
    @Autowired
    private OwnerRepository ownerRepository;
    @Autowired
    private PetServiceRepository serviceRepository;
    @Autowired
    private ReservationStatusRepository reservationStatusRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ConfigurationRepository configurationRepository;

    @GetMapping("grid")
    public String displayReservations(@RequestParam(required = false, defaultValue = "false") Boolean showAll, Model model) {
        model.addAttribute("reservations", showAll ? reservationRepository.findAll() : reservationRepository.findByActive(true));
        model.addAttribute("showAll", showAll);
        return "reservations/reservations";
    }
    @GetMapping
    public String displayCalendar(Model model) {
        Function<Reservation, String> getTitle = reservation ->
                "#" + reservation.getConfirmation() + " | " +
                        reservation.getPet().getOwner().getFullName() + " | " +
                        reservation.getPet().getPetName();
        List<CalendarEvent> events = CalendarEventUtils.parseEventsFromReservations(
                reservationRepository.findAll(),
                getTitle,
                configurationRepository.findByName("RESERVATION_COLOR").getValue());
        model.addAttribute("reservations", events);
        return "reservations/calendarView";
    }

    private void addCommonAttributes(Integer ownerId, Model model) {
        if(ownerId == null){
            model.addAttribute("pets", new ArrayList <Pet>());
        }else{
            Optional<Owner> result = ownerRepository.findById(ownerId);
            if(result.isEmpty()){
                model.addAttribute("pets", new ArrayList <Pet>());
            }else{
                model.addAttribute("pets", result.get().getPets());
                model.addAttribute("ownerId", ownerId );
            }
        }
        model.addAttribute("owners", ownerRepository.findAll());
        model.addAttribute("services", serviceRepository.findByStayService(true));
        model.addAttribute("statuses", reservationStatusRepository.findAll());
    }

    @GetMapping("create")
    public String displayCreateReservationsForm(Model model, @RequestParam(required = false) Integer ownerId, @RequestParam(required = false) Boolean checkInOnSave) {
        model.addAttribute("title", "Create Reservation");
        model.addAttribute(new Reservation());
        model.addAttribute("checkInOnSave", checkInOnSave);
        addLocation("New", model);
        addCommonAttributes(ownerId, model);
        return "reservations/create";
    }

    @PostMapping("create")
    public String processCreateReservationsForm(@ModelAttribute @Valid Reservation newReservation,
                                                @RequestParam(required = false) Integer ownerId,
                                                @RequestParam(required = false) Boolean checkInOnSave,
                                                Errors errors, Model model,RedirectAttributes redirectAttributes) {
        boolean hasErrors = errors.hasErrors();
        if(newReservation.getStartDateTime().after(newReservation.getEndDateTime())) {
            hasErrors = true;
            errors.rejectValue("startDateTime", "startDateTime.InvalidRange", "");
            errors.rejectValue("endDateTime", "endDateTime.InvalidRange", "The end date must be the same day or after the start date.");
        }
        if(hasErrors) {
            model.addAttribute("title", "Create Event");
            addCommonAttributes(ownerId, model);
            return "reservations/create";
        }
        newReservation.assignConfirmationCode();
        ReservationStatus status = new ReservationStatus();
        status.setId(1);
        newReservation.setStatus(status);
        reservationRepository.save(newReservation);
        SimpleMailMessage message=new SimpleMailMessage();
        String body="Confirmation code: "+ newReservation.getConfirmation()+
                "\nGuest: "+ newReservation.getPet().getPetName()+
                "\nStart Date: "+ newReservation.getStartDateTime()+
                "\nEnd Date: "+ newReservation.getEndDateTime();
        message.setFrom("petboardingservicelc@gmail.com");
        message.setTo(newReservation.getPet().getOwner().getEmail());
        message.setSubject("Reservation Confirmation");
        message.setText(body);
//        emailService.send(message);
        if(checkInOnSave) {
            return "redirect:/stays/add?reservationId=" + newReservation.getId();
        }
        redirectAttributes.addFlashAttribute("infoMessage", "Reservation info sent to email");
        return "redirect:/reservations/grid";
    }

    @GetMapping("delete")
    public String displayDeleteReservationsForm(@RequestParam Integer reservationId, Model model) {

        Optional<Reservation> result = reservationRepository.findById(reservationId);
        if (result.isEmpty()) {
            model.addAttribute("title", "Invalid Reservation ID: " + reservationId);
        } else {
            Reservation reservation = result.get();
            model.addAttribute("title", reservation.getId() + " Details");
            model.addAttribute("reservation", reservation);
        }
        return "reservations/delete";
    }

    @PostMapping("delete")
    public String processDeleteReservationsForm(@RequestParam Integer reservationId, Model model, RedirectAttributes redirectAttributes) {

        if (reservationId != null) {
            Optional<Reservation> reservationOptional = reservationRepository.findById(reservationId);
            if(reservationOptional.isPresent()){
                Reservation reservation = reservationOptional.get();
                if (reservation.getStay() == null){
                    reservationRepository.deleteById(reservationId);
                }else{
                    reservation.setActive(false);
                    reservationRepository.save(reservation);
                    redirectAttributes.addFlashAttribute("infoMessage","Reservation has been deactivated could not be deleted");
                }
                return "redirect:/reservations/grid";
            }
        }
        redirectAttributes.addFlashAttribute("errorMessage", "Reservation could not be found");
        return "redirect:/reservations/delete?reservationId=" + reservationId;
    }

    @GetMapping("detail")
    public String displayReservationsDetails(@RequestParam Integer reservationId, Model model) {

        Optional<Reservation> result = reservationRepository.findById(reservationId);

        if (result.isEmpty()) {
            model.addAttribute("title", "Invalid Reservation ID: " + reservationId);
        } else {
            Reservation reservation = result.get();
            model.addAttribute("title", reservation.getId() + " Details");
            model.addAttribute("reservation", reservation);
        }
        return "reservations/detail";
    }
    @GetMapping("resendEmail")
    public String resendEmail(@RequestParam Integer reservationId, RedirectAttributes redirectAttributes){
        Optional<Reservation> result = reservationRepository.findById(reservationId);
        if (result.isEmpty()) {
            redirectAttributes.addFlashAttribute("title", "Invalid Reservation ID: " + reservationId);
        }
        Reservation reservation = result.get();
        SimpleMailMessage message = new SimpleMailMessage();
        String body="Confirmation code: "+ reservation.getConfirmation() +
                "\nGuest: "+ reservation.getPet().getPetName()+
                "\nStart Date: "+ reservation.getStartDateTime()+
                "\nEnd Date: "+ reservation.getEndDateTime();
        message.setFrom("petboardingservicelc@gmail.com");
        message.setTo(reservation.getPet().getOwner().getEmail());
        message.setSubject("Reservation Confirmation");
        message.setText(body);
        emailService.send(message);
        redirectAttributes.addFlashAttribute("infoMessage","Reservation details has been sent");
        return "redirect:/reservations/detail?reservationId=" + reservationId;
    }
    @GetMapping("update")
    public String displayUpdateReservationsForm(@RequestParam Integer reservationId,@Valid Reservation reservation,Errors errors, Model model) {
        Optional<Reservation> result = reservationRepository.findById(reservationId);

        if (result.isEmpty()) {
            model.addAttribute("title", "Invalid Reservation ID: " + reservationId);
        } else {
            reservation = result.get();
            model.addAttribute("title", reservation.getId() + " Details");
            model.addAttribute("reservation", reservation);
            model.addAttribute("services", serviceRepository.findAll());
            model.addAttribute("statuses", reservationStatusRepository.findAll());
        }
        return "reservations/update";
    }
    @PostMapping("update")
    public String processUpdateReservationsForm(@Valid Reservation updatedReservation,Errors errors, Model model) {
//        Optional<Reservation> result = reservationRepository.findById(reservationId);

        if (errors.hasErrors()) {
            model.addAttribute("services", serviceRepository.findAll());
            model.addAttribute("statuses", reservationStatusRepository.findAll());
            return "reservation/update";
        }

        //save the updated reservation
        reservationRepository.save(updatedReservation);


        return "redirect:/reservations/grid";
    }
    @ModelAttribute("activeModule")
    public Module addActiveModule(){
        return getActiveModule("reservations");
    }
}
