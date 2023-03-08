package com.petboarding.models.data;

import com.petboarding.models.PetService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface PetServiceRepository extends JPARepositoryActiveFiltering<PetService, Integer> {
    public Collection<PetService> findByStayService(Boolean isStayService);
}
