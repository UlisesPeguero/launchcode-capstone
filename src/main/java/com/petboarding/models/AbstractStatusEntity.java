package com.petboarding.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@MappedSuperclass
public abstract class AbstractStatusEntity extends AbstractEntity{
    @NotEmpty
    @Size(max = 25, message = "Status name cannot be longer than 25 characters.")
    @Column(length = 25)
    private String name;

    @Column(length = 10)
    private String color;

    @Column(columnDefinition = "boolean default true")
    private Boolean active = true;

    public AbstractStatusEntity() {
    }

    public AbstractStatusEntity(Integer id) {
        super();
        setId(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
