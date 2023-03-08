package com.petboarding.models;


import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "pets")
public class Pet extends AbstractEntity {

    @NotBlank(message = "Enter pet name!")
    @Column(name = "pet_name")
    private String petName;


//    @NotBlank(message = "Enter parent name!")     // Some Not Blank error?
    @ManyToOne                                      // Sets up relationship
    private Owner owner;                            // Parents -> Owner

    @NotBlank(message = "Enter breed type!")
    @Column(name = "breed")
    private String breed;
    @Column(name = "notes")
    private String notes;
    @Column(nullable = true, length = 64)
    private String photo;

    @Column(columnDefinition = "boolean default true")
    private Boolean active = true;


    @Transient
    public String getPhotoPath() {
        if (photo == null || getId() == 0) {return null;}
        return "/uploads/pet-photos/" + getId() + "/" + photo;
    }

    public Pet(String petName, Owner owner, String breed, String notes, Boolean active) {
        super();
        this.petName = petName;
        this.owner = owner;                         // Parents -> Owner
        this.breed = breed;
        this.notes = notes;
        this.active = active;
    }

    public Pet(){}

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public Owner getOwner() {
        return this.owner;
    }                   // Parents -> Owner

    public void setOwner(Owner owner) {                             // Parents -> Owner
        this.owner = owner;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPhoto() {return photo;}

    public void setPhoto(String photo) {this.photo = photo;}

    @Override
    public Boolean getActive() {
        return active;
    }

    @Override
    public void setActive(Boolean active) {
        this.active = active;
    }
}
