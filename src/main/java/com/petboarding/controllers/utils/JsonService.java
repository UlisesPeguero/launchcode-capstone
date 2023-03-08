package com.petboarding.controllers.utils;

import com.petboarding.models.AbstractDetailEntity;
import com.petboarding.models.StayService;
import org.hibernate.cache.spi.support.AbstractEntityDataAccess;

public class JsonService {
    private Integer id;
    private Float quantity;
    private Integer serviceId;
    private Double pricePerUnit;
    private String description;
    private Double subTotal;

    public JsonService() {
    }

    public JsonService(AbstractDetailEntity service) {
        this.id = service.getId();
        this.quantity = service.getQuantity();
        this.serviceId = service.getService().getId();
        this.pricePerUnit = service.getPricePerUnit();
        this.description = service.getDescription();
        this.subTotal = service.getSubTotal();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getQuantity() {
        return quantity;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
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

    public Double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Double subTotal) {
        this.subTotal = subTotal;
    }
}
