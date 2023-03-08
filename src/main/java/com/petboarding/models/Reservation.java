package com.petboarding.models;

import com.petboarding.models.AbstractEntity;
import com.petboarding.models.data.PetServiceRepository;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
public class Reservation extends AbstractEntity {

    @ManyToOne
    private Pet pet;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private PetService service;

    private String confirmation;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Start date is required")
    private Date startDateTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "End date is required")
    private Date endDateTime;

    @NotNull
    @Size(max = 250, message = "A comment cannot be longer than 250 characters.")
    private String comments;

    @Valid
    @ManyToOne
    @JoinColumn(name = "status_id",columnDefinition = "int default 1")
    private ReservationStatus status;

    @Column(columnDefinition = "boolean default true")
    private Boolean active = true;

    @OneToOne(mappedBy = "reservation")
    private Stay stay;

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public Reservation() {
    }
    public Reservation(Date aStartDateTime, Date anEndDateTime, String aComment) {
        super();
        this.startDateTime = aStartDateTime;
        this.endDateTime = anEndDateTime;
        this.comments = aComment;
    }

    public PetService getService() {
        return service;
    }

    public void setService(PetService service) {
        this.service = service;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getComments() {
        return comments;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(String confirmation) {
        this.confirmation = confirmation;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    @Override
    public Boolean getActive() {
        return active;
    }

    @Override
    public void setActive(Boolean active) {
        this.active = active;
    }

    public Stay getStay() {
        return stay;
    }

    public void setStay(Stay stay) {
        this.stay = stay;
    }

    public void assignConfirmationCode(){
        confirmation = generateConfirmationCode();
    }
    private String generateConfirmationCode() {
        // get code based on current milliseconds
        String code = getCodeFromLong(System.currentTimeMillis());
        try {
            int count = 0;
            while(count < 3) { // three tries to get non-pure digits confirmation
                // test if the code is decimal number
                long longCode = Long.parseLong(code);
                // turn new number into Hexadecimal
                code = getCodeFromLong(longCode);
                count++;
            }
        } catch(Exception e) {}
        return code;
    }

    private String getCodeFromLong(long number) {
        if(number < 4096) number += 4096; //force 4 digit hexadecimal
        String code = Long.toHexString(number);
        return code.substring( code.length() - 4 ).toUpperCase();
    }

    public boolean isDateRangeValid() {
        if(endDateTime == null || startDateTime == null) return false;
        return !startDateTime.after(endDateTime);
    }

}
