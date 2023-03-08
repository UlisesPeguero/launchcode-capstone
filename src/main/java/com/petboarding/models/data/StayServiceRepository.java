package com.petboarding.models.data;

import com.petboarding.models.PetService;
import com.petboarding.models.StayService;

import java.util.List;

public interface StayServiceRepository extends JPARepositoryActiveFiltering<StayService, Integer> {
    public List<StayService> findByService(PetService petService);
}
