package com.petboarding.service;

import com.petboarding.models.Pet;
import com.petboarding.models.data.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
public class PetServiceImpl implements PetService{
    // what do created methods do
    @Autowired
    private PetRepository petRepository;
    @Override
    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    @Override
    public void savePet(Pet pet) {
        this.petRepository.save(pet);
    }

    @Override
    public Pet getPetById(Integer id) {
        Optional<Pet> optional = petRepository.findById(id);
        Pet pet = null;
        if(optional.isPresent()) {
            pet =optional.get();
        }else{
            throw new RuntimeException("Pet not found with id :: " + id);
        }
        return pet;
    }

    @Override
    public void deletePetById(Integer id) {
        this.petRepository.deleteById(id);
    }


    @Override
    public List<Pet> findByActive(Boolean active) {
        return petRepository.findByActive(active);
    }


}
