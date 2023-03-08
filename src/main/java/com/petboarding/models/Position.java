package com.petboarding.models;

import org.springframework.stereotype.Controller;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Position extends AbstractEntity{

    @NotBlank
    @Size(max = 50, message = "Name cannot be longer than 100 characters.")
    private String name;

    @OneToMany
    @JoinColumn(name = "position_id")
    private List<Employee> employees = new ArrayList<>();

    public Position() {}

    public Position(@Size(max = 50, message = "Name cannot be longer than 100 characters.") String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Employee> getEmployees() { return employees; }

    @Override
    public String toString() {
        return name;
    }
}
