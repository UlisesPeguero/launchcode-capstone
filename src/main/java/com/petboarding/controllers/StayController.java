package com.petboarding.controllers;

import com.petboarding.controllers.utils.DateUtils;
import com.petboarding.controllers.utils.InvoiceUtils;
import com.petboarding.models.*;
import com.petboarding.controllers.utils.JsonService;
import com.petboarding.models.app.Module;
import com.petboarding.models.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

import static javax.swing.text.html.HTML.Tag.S;

@Controller
@RequestMapping("stays")
public class StayController extends AppBaseController {

    private final String FORM_NEW_TITLE = "New stay";
    private final String FORM_UPDATE_TITLE = "Update: ${confirmation}";

    @Autowired
    private StayRepository stayRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private KennelRepository kennelRepository;

    @Autowired
    private PetServiceRepository serviceRepository;

    @Autowired
    private StayServiceRepository stayServiceRepository;

    @Autowired
    private StayStatusRepository stayStatusRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceDetailRepository invoiceDetailRepository;

    @Autowired
    private ConfigurationRepository configurationRepository;

    @GetMapping
    public String displayStaysCalendar(Model model) {
        return "redirect:/stays/grid";
    }

    @GetMapping("grid")
    public String displayStaysGrid(@RequestParam(required = false, defaultValue = "false") Boolean showAll, Model model) {
        Sort sortDescCheckInTime = Sort.by(Sort.Direction.DESC, "checkInTime");
        model.addAttribute("stays", showAll ? stayRepository.findAll(sortDescCheckInTime) : stayRepository.findByActive(true, sortDescCheckInTime));
        model.addAttribute("showAll", showAll);
        return "stays/indexGrid";
    }

    @GetMapping("add")
    public String displayAddStayForm(@RequestParam(required = true) Integer reservationId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Reservation> optReservation = reservationRepository.findById(reservationId);
        if(optReservation.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "The reservation could not be found.");
            return "redirect:/stays";
        }
        Reservation reservation = optReservation.get();
        if(reservation.getStay() != null) {
            return "redirect:/stays/update/" + reservation.getStay().getId();
        }
        Stay stay = new Stay();
        stay.setReservation(reservation);
        prepareAddFormModel(stay, model);
        return "stays/form";
    }

    @PostMapping("add")
    public String processAddStayForm(@Valid Stay newStay, @RequestParam(required = false) String endDateValue, Errors validation, Model model) {
        boolean hasErrors = validation.hasErrors();
        hasErrors |= processDateValidation(newStay, model);
        if(hasErrors) {
            prepareAddFormModel(newStay, model);
            return "stays/form";
        }
        stayRepository.save(newStay);
        updateAdditionalServices(newStay);
        return "redirect:/stays";
    }

    @GetMapping("update/{id}")
    public String displayUpdateStayForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Stay> optStay = stayRepository.findById(id);
        if(optStay.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "The stay could not be found.");
            return "redirect:/stays";
        }
        prepareUpdateFormModel(optStay.get(), model);
        return "stays/form";
    }

    @PostMapping("update/{id}")
    public String processUpdateStayForm(@Valid Stay stay,
                                        Errors validation,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {
        boolean hasErrors = validation.hasErrors();
        hasErrors |= processDateValidation(stay, model);
        if(hasErrors) {
            prepareUpdateFormModel(stay, model);
            return "stays/form";
        }
        stayRepository.save(stay);
        updateAdditionalServices(stay);
        redirectAttributes.addFlashAttribute("infoMessage", "The Stay information has been updated.");
        return "redirect:" + stay.getId();
    }

    @PostMapping("delete/{id}")
    public String processDeleteStay(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        Optional<Stay> optStay = stayRepository.findById(id);
        if(optStay.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "The stay couldn't be found.");
        } else {
            Stay stay = optStay.get();
            if(stay.getInvoice() != null) {
                stay.setActive(false);
                stayRepository.save(stay);
                redirectAttributes.addFlashAttribute("infoMessage", "Stay <strong>#" + stay.getReservation().getConfirmation() +
                        "</strong> is linked to the Invoice #<strong>" + stay.getInvoice().getFullNumber() + "</strong>, so it will be set inactive.");
            } else {
                stayRepository.deleteById(id);
                redirectAttributes.addFlashAttribute("infoMessage", "Stay was successfully deleted.");
            }
        }
        return "redirect:/stays/grid";
    }

    @Transactional
    @GetMapping("checkout/{id}")
    public String processCheckoutAndShowInvoice(@PathVariable Integer id,
                                                Model model,
                                                RedirectAttributes redirectAttributes) {
        Optional<Stay> optStay = stayRepository.findById(id);
        if(optStay.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "The stay wasn't found.");
            return "redirect: /stays";
        }

        Stay stay = optStay.get();
        Invoice invoice = new Invoice();
        invoice.setDate(new Date());
        BigDecimal nextNumber = invoiceRepository.findNextNumberByDate(invoice.getDate());
        invoice.setNumber(nextNumber == null ? 1 : nextNumber.intValue());
        invoice.setStatus(getInvoiceStatus("OPEN_INVOICE"));
        invoice.setTaxPercent(Float.parseFloat(configurationRepository.findByName("SALES_TAX").getValue()));
        stay.setCheckOutTime(new Timestamp(invoice.getDate().getTime()));
        stay.setStatus(getStayStatus("COMPLETED_STAY"));
        stayRepository.save(stay);
        stay.getReservation().setStatus(getReservationStatus("COMPLETED_RESERVATION"));
        reservationRepository.save(stay.getReservation());
        invoice.setStay(stay);
        invoiceRepository.save(invoice);
        // generating detail
        Long daysStay = DateUtils.getDaysDifference(stay.getCheckInTime(), stay.getCheckOutTime());
        InvoiceDetail stayDetail = new InvoiceDetail();
        stayDetail.setInvoice(invoice);
        stayDetail.setQuantity(daysStay.floatValue());
        stayDetail.setService(stay.getReservation().getService());
        stayDetail.setPricePerUnit(stay.getReservation().getService().getPricePerUnit());
        stayDetail.setDescription("Stay from " +
                DateUtils.format(stay.getCheckInTime(), "MM/dd/yyyy") +
                " to " +
                DateUtils.format(stay.getCheckOutTime(), "MM/dd/yyyy"));
        invoiceDetailRepository.save(stayDetail);
        for(StayService service: stay.getAdditionalServices()) {
            InvoiceDetail detail = new InvoiceDetail(service);
            detail.setInvoice(invoice);
            invoiceDetailRepository.save(detail);
        }
        return "redirect:/invoices/update/" + invoice.getId();
    }

    private void updateAdditionalServices(Stay stay) {
        for(StayService service: stay.getAdditionalServices()) {
            if(service.getService() == null) {
                stayServiceRepository.delete(service);
            } else {
                service.setStay(stay);
                stayServiceRepository.save(service);
            }
        }
    }

    private void prepareCommonFormModel(Stay stay, Model model) {
        model.addAttribute("kennels", kennelRepository.findAll());
        model.addAttribute("services", serviceRepository.findByStayService(true));
        model.addAttribute("statuses", stayStatusRepository.findAll());
        model.addAttribute("caretakers", employeeRepository.findByPositionName("caretaker"));
        model.addAttribute("additionalServices", serviceRepository.findByStayService(false));
        HashMap<Integer, JsonService> mapJsonStayServices = new HashMap<>();
        for(StayService service: stay.getAdditionalServices()) {
            mapJsonStayServices.put(service.getId(),
                    new JsonService(service));
        }
        model.addAttribute("mapStaysAdditionalServices", mapJsonStayServices);
    }
    private void prepareAddFormModel(Stay stay, Model model) {
        model.addAttribute("formTitle", FORM_NEW_TITLE);
        model.addAttribute("stay", stay);
        model.addAttribute("submitURL", "/stays/add");
        addLocation("New", model);
        prepareCommonFormModel(stay, model);
    }


    private void prepareUpdateFormModel(Stay stay, Model model) {
        model.addAttribute("formTitle", FORM_UPDATE_TITLE.replace("${confirmation}", stay.getReservation().getConfirmation()));
        model.addAttribute("stay", stay);
        model.addAttribute("submitURL", "/stays/update/" + stay.getId());
        addLocation("Update", model);
        prepareCommonFormModel(stay, model);
    }

    private ReservationStatus getReservationStatus(String name) {
        Integer statusId = Integer.parseInt(configurationRepository.findByName(name).getValue());
        return new ReservationStatus(statusId);
    }

    private StayStatus getStayStatus(String name) {
        Integer statusId = Integer.parseInt(configurationRepository.findByName(name).getValue());
        return new StayStatus(statusId);
    }

    private InvoiceStatus getInvoiceStatus(String name) {
        Integer statusId = Integer.parseInt(configurationRepository.findByName(name).getValue());
        return new InvoiceStatus(statusId);
    }


    private boolean processDateValidation(Stay stay, Model model) {
        boolean hasErrors = false;
        if(!stay.getReservation().isDateRangeValid()) {
            hasErrors = true;
            model.addAttribute("errorMessage",
                    "The end date <strong>" + DateUtils.showFormatter.format(stay.getReservation().getEndDateTime()) +
                            "</strong> has to be on the same day or after <strong>" + DateUtils.showFormatter.format(stay.getReservation().getStartDateTime()) + "</strong>");
        }
        return hasErrors;
    }

    @ModelAttribute("activeModule")
    public Module addActiveModule() {
        return getActiveModule(this.getClass().getAnnotation(RequestMapping.class).value()[0]);
    }
}
