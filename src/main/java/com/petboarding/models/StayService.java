package com.petboarding.models;

import javax.mail.Service;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.Valid;

@Entity
public class StayService extends AbstractDetailEntity{

    @Valid
    @ManyToOne
    @JoinColumn(name = "stay_id")
    private Stay stay;

   public StayService() {
   }

   public StayService(Integer id, PetService service, Float quantity, Double pricePerUnit, String description) {
       super();
       this.setId(id);
       this.setService(service);
       this.setQuantity(quantity);
       this.setPricePerUnit(pricePerUnit);
       this.setDescription(description);
   }

    public Stay getStay() {
        return stay;
    }

    public void setStay(Stay stay) {
        this.stay = stay;
    }
}
