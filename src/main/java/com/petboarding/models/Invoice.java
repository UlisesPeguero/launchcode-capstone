package com.petboarding.models;

import com.petboarding.controllers.utils.DateUtils;
import com.petboarding.models.data.InvoiceRepository;
import com.petboarding.models.utils.InvoiceAggregatedInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.*;

@Entity
public class Invoice extends AbstractEntity{

    @Transient
    private InvoiceAggregatedInformation aggregatedInformation;

    @Column(nullable = false, updatable = false)
    private Date date;

    @Column(nullable = false, updatable = false)
    private Integer number;

    @OneToOne
    @JoinColumn(name = "stay_id", updatable = false)
    private Stay stay;

    @ManyToOne
    @JoinColumn(name = "owner_id", updatable = false)
    private Owner owner;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private InvoiceStatus status;

    @OneToMany(mappedBy = "invoice")
    @OrderBy("id ASC")
    private List<InvoiceDetail> details = new ArrayList<>();

    @Column(columnDefinition = "float(5,2) default 0.0")
    private Float discountPercent = 0.0f;

    @Column(columnDefinition = "float(5,2) default 0.0")
    private Float taxPercent = 0.0f;

    @OneToMany(mappedBy = "invoice")
    @OrderBy("datetime ASC")
    private List<Payment> payments = new ArrayList<>();

    public Invoice() {}

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getFullNumber() {
        if(date == null || number == null) return "";
        return DateUtils.format(this.date, "yyyy") + "." + this.number;
    }

    public Stay getStay() {
        return stay;
    }

    public void setStay(Stay stay) {
        this.stay = stay;
        this.owner = stay.getReservation().getPet().getOwner();
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    public List<InvoiceDetail> getDetails() {
        return details;
    }

    public void setDetails(List<InvoiceDetail> details) {
        this.details = details;
        processAggregated();
        aggregatedInformation.processDetails(details);
    }

    public Float getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(Float discountPercent) {
        this.discountPercent = discountPercent;
    }

    public Double getDiscountTotal() {
        return getSubTotal() * (discountPercent/100);
    }

    public Float getTaxPercent() {
        return taxPercent;
    }

    public void setTaxPercent(Float taxPercent) {
        this.taxPercent = taxPercent;
    }

    public Double getTaxTotal() {
        return getSubTotal() * (taxPercent/100);
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public Payment getPayment() {
        if(payments.isEmpty()) return new Payment();
        return payments.get(0);
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public String getServicesList() {
        processAggregated();
        return aggregatedInformation.getServicesList();
    }

    public String getCondensedServicesList() {
        String condensedList = "";
        if(details.size() > 0) condensedList = details.get(0).getService().getName();
        if(details.size() > 1) condensedList += " ...";
        return condensedList;
    }

    public Double getSubTotal() {
        processAggregated();
        return aggregatedInformation.getSubTotal();
    }

    public Double getTotal() {
        return getSubTotal() + getTaxTotal() - getDiscountTotal();
    }

    public Double getTotalPaid() {
        processAggregated();
        return aggregatedInformation.getTotalPaid();
    }

    public Double getToPayTotal() {
        return getTotal() - getTotalPaid();
    }

    private void processAggregated() {
        if(aggregatedInformation != null) return;
        aggregatedInformation = new InvoiceAggregatedInformation(details);
        aggregatedInformation.processPayments(payments);
    }


}
