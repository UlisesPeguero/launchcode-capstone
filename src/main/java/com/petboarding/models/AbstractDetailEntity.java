package com.petboarding.models;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@MappedSuperclass
public abstract class AbstractDetailEntity extends AbstractEntity{

    @ManyToOne
    @JoinColumn(name = "service_id")
    private PetService service;

    @NotNull(message = "The quantity is required.")
    @Column(columnDefinition = "float(3, 2) default 1.0")
    private Float quantity;

    @NotNull(message = "The price per unit is required.")
    private Double pricePerUnit;

    @Column(columnDefinition = "varchar(100) default null")
    private String description;

    public PetService getService() {
        return service;
    }

    public void setService(PetService service) {
        this.service = service;
    }

    public Float getQuantity() {
        return quantity;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }

    public Double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(Double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getSubTotal() {
        return quantity * pricePerUnit;
    }
}
