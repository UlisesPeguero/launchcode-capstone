package com.petboarding.models.data;

import com.petboarding.models.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetRepository extends JPARepositoryActiveFiltering<Pet, Integer> {
}
