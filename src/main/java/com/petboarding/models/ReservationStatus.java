package com.petboarding.models;

import javax.persistence.Entity;

@Entity
public class ReservationStatus extends AbstractStatusEntity{
    public ReservationStatus() {}

    public ReservationStatus(Integer id) {
        super(id);
    }
}
