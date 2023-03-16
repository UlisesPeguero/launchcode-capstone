package com.petboarding.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "pets")
public class Pet extends AbstractEntity {

    @NotBlank(message = "Name cannot be empty.")
    private String name;

    @ManyToOne // Sets up relationship
    private Owner owner; // Parents -> Owner

    @NotBlank(message = "Select a valid breed.")
    @OneToMany
    @JoinColumn(name = "breed_id")
    private Breed breed;

    private String notes;

    @Column(nullable = true, length = 64)
    private String photo;

    @Column(columnDefinition = "boolean default true")
    private Boolean active = true;

    @Transient
    public String getPhotoPath() {
        if (photo == null || getId() == 0) {
            return null;
        }
        return "/uploads/pet-photos/" + getId() + "/" + photo;
    }

    public Pet(String petName, Owner owner, Breed breed, String notes, Boolean active) {
        super();
        this.name = petName;
        this.owner = owner; // Parents -> Owner
        this.breed = breed;
        this.notes = notes;
        this.active = active;
    }

    public Pet() {
    }

    public String gePetName() {
        return name;
    }

    public void setPetName(String petName) {
        this.name = petName;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isActive() {
        return this.active;
    }

    public Owner getOwner() {
        return this.owner;
    } // Parents -> Owner

    public void setOwner(Owner owner) { // Parents -> Owner
        this.owner = owner;
    }

    public Breed getBreed() {
        return breed;
    }

    public void setBreed(Breed breed) {
        this.breed = breed;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public Boolean getActive() {
        return active;
    }

    @Override
    public void setActive(Boolean active) {
        this.active = active;
    }
}
