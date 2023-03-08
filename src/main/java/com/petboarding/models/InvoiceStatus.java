package com.petboarding.models;

import javax.persistence.Entity;

@Entity
public class InvoiceStatus extends AbstractStatusEntity{

    public InvoiceStatus(){
        super();
    }

    public InvoiceStatus(Integer id) {
        super(id);
    }

}
