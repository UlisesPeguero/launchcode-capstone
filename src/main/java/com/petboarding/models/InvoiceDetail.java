package com.petboarding.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class InvoiceDetail extends AbstractDetailEntity{

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    public InvoiceDetail() {
    }

    public InvoiceDetail(StayService service) {
        setService(service.getService());
        setQuantity(service.getQuantity());
        setPricePerUnit(service.getPricePerUnit());
        setDescription(service.getDescription());
        setActive(service.getActive());
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }
}
