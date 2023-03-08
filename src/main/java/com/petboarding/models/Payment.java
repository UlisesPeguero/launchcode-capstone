package com.petboarding.models;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;
import java.util.Date;

@Entity
public class Payment extends AbstractEntity{

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @CreationTimestamp
    private Timestamp datetime;

    @Column(nullable = false)
    private Double amount;

    @Column(columnDefinition = "boolean default true")
    private Boolean cashPayment;

    @Column(columnDefinition = "varchar(100) default null")
    private String cardConfirmation;

    public Payment() {
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public Timestamp getDatetime() {
        return datetime;
    }

    public void setDatetime(Timestamp datetime) {
        this.datetime = datetime;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Boolean getCashPayment() {
        return cashPayment;
    }

    public void setCashPayment(Boolean cashPayment) {
        this.cashPayment = cashPayment;
    }

    public String getCardConfirmation() {
        return cardConfirmation;
    }

    public void setCardConfirmation(String cardConfirmation) {
        this.cardConfirmation = cardConfirmation;
    }
}
