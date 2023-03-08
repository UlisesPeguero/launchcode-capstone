package com.petboarding.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Kennel extends AbstractEntity{

    @NotNull
    @Size(max = 25, message = "The name cannot be longer than 50 characters.")
    private String name;

    @OneToMany
    @JoinColumn(name = "id")
    private List<Stay> stays = new ArrayList<>();

    @Column(name="x_Pos", precision = 7, scale = 3)
    private float xPos;
    @Column(name="y_Pos", precision = 7, scale = 3)
    private float yPos;
    @Column(precision = 7, scale = 3)
    private float width;
    @Column(precision = 7, scale = 3)
    private float height;


    public Kennel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getXPos() {
        return xPos;
    }

    public void setXPos(float xPos) {
        this.xPos = xPos;
    }

    public float getYPos() {
        return yPos;
    }

    public void setYPos(float yPos) {
        this.yPos = yPos;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
