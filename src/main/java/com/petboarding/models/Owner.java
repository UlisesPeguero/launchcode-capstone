package com.petboarding.models;
import com.petboarding.models.Pet;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Just a name for now
@Entity
public class Owner extends AbstractEntity{
    @NotBlank(message = "First name cannot be empty.")
    @Size(max = 50, message = "First name cannot be longer than 50 characters.")
    private String firstName;
    @NotBlank(message = "Last name cannot be empty.")
    @Size(max = 50, message = "Last name cannot be longer than 50 characters.")
    private String lastName;
    @NotBlank(message = "Address cannot be empty.")
    @Size(max = 100, message = "Address cannot be longer than 100 characters.")
    private String address;
    @Size(max = 100, message = "Address 2 cannot be longer than 100 characters.")
    private String address2;
    @Size(min = 10, message = "Phone number cannot be shorter than 10 characters.")
    private String phoneNumber;
    @NotBlank(message = "Email cannot be empty.")
    @Email
    private String email;

    @OneToMany
    @JoinColumn(name = "owner_id")
    private List<Pet> pets = new ArrayList<>();

    @Column(nullable = true, length = 64)
    private String photo;

    private String notes;

    public Owner(){
    }

    public Owner(String aFirstName, String aLastName, String aAddress, String aAddress2, String aPhoneNumber, String aEmail, List<Pet> apets, String aNotes){

        firstName = aFirstName;
        lastName = aLastName;
        address = aAddress;
        address2 = aAddress2;
        phoneNumber = aPhoneNumber;
        email = aEmail;
        pets = apets;
        notes = aNotes;
    }

    @Transient
    public String getPhotoPath() {
        if (photo == null || getId() == 0) {return null;}
        return "/uploads/owner-photos/" + getId() + "/" + photo;
    }

    @Override
    public String toString() {
        return "Owner{" +
                "id=" + this.getId() +
                ", firstName=" + firstName +
                ", lastName=" + lastName +
                ", address=" + address +
                ", address2=" + address2 +
                ", phoneNumber=" + phoneNumber +
                ", email=" + email +
                ", pets=" + pets +
                ", notes=" + notes +
                '\'' +
                '}';
    }

    public String getFullName() {
        return this.lastName + ", " + this.firstName;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Pet> getPets() {
        return pets;
    }

    public void setPets(List<Pet> pets) {
        this.pets = pets;
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

    public String getContactInformation() {
        String information = "";
        information += "<strong>Address: </strong>" + this.address;
        information += (this.address2 != null && this.address2 != "" ? " , " + this.address2 : "");
        information += (this.phoneNumber != null ? "<br><strong>Phone number</strong>: " + this.phoneNumber : "");
        information += (this.email != null ? "<br><strong>Email</strong>: " + this.email : "");
        return information;
    }
}
