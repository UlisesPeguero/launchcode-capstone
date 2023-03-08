package com.petboarding.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class PetService extends AbstractEntity{

    @NotNull
    @Size(min = 3, max = 50, message = "The name must be between 3 and 50 characters long.")
    private String name;

    @Column(columnDefinition = "double default 0.0")
    private Double pricePerUnit;

    @Column(columnDefinition = "boolean default false")
    private Boolean stayService = false;

    public PetService() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(Double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public Boolean getStayService() {
        return stayService;
    }

    public void setStayService(Boolean stayService) {
        this.stayService = stayService;
    }
}
