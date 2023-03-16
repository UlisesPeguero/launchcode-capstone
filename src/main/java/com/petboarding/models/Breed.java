package com.petboarding.models;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;

@Entity
public class Breed extends AbstractEntity {

  @NotBlank(message = "Name cannot be empty.")
  private String name;

  public Breed() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
