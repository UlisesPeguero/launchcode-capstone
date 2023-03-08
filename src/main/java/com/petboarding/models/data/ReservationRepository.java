package com.petboarding.models.data;


import com.petboarding.models.PetService;
import com.petboarding.models.Reservation;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JPARepositoryActiveFiltering<Reservation, Integer> {
    public List<Reservation> findByPetId(Integer petId);
    public List<Reservation> findByStayIsNull();
    public List<Reservation> findByService(PetService petService);

}
