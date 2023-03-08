package com.petboarding.service;

import com.petboarding.models.Pet;
import com.petboarding.models.data.JPARepositoryActiveFiltering;

import java.util.List;

public interface PetService {
    // create methods for Pets
    List<Pet> getAllPets();
    void savePet(Pet pet);

    Pet getPetById(Integer id);
    void deletePetById(Integer id);

    List<Pet> findByActive(Boolean active);
}
