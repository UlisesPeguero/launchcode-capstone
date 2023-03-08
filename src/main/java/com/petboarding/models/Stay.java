package com.petboarding.models;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Stay extends AbstractEntity{

    @CreationTimestamp
    @Column(name = "checkin_time", updatable = false)
    private Timestamp checkInTime = null;

    @Column(name = "checkout_time")
    private Timestamp checkOutTime = null;

    @Valid
    @OneToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @Valid
    @NotNull(message = "Select a kennel.")
    @ManyToOne
    private Kennel kennel;

    @Valid
    @NotNull(message = "Select a caretaker")
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private StayStatus status;

    @OneToMany(mappedBy = "stay")
    private List<StayService> additionalServices = new ArrayList<>();

    @OneToOne(mappedBy = "stay")
    private Invoice invoice;

    public Stay() {
    }

    public Timestamp getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(Timestamp checkInTime) {
        this.checkInTime = checkInTime;
    }

    public Timestamp getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(Timestamp checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public StayStatus getStatus() {
        return status;
    }

    public void setStatus(StayStatus status) {
        this.status = status;
    }

    public List<StayService> getAdditionalServices() {
        return additionalServices;
    }

    public void setAdditionalServices(List<StayService> additionalServices) {
        this.additionalServices = additionalServices;
    }

    public Kennel getKennel() {
        return kennel;
    }

    public void setKennel(Kennel kennel) {
        this.kennel = kennel;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }
}
