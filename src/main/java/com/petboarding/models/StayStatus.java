package com.petboarding.models;

import javax.persistence.Entity;

@Entity
public class StayStatus extends AbstractStatusEntity{
    public StayStatus() {}

    public StayStatus(Integer id) {
        super(id);
    }
}
