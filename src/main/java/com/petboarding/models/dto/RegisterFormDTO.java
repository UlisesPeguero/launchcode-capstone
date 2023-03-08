package com.petboarding.models.dto;

import com.petboarding.models.Role;
import com.sun.istack.NotNull;

import javax.validation.constraints.NotBlank;

public class RegisterFormDTO extends LoginFormDTO {

    private String verifyPassword;

    public String getVerifyPassword() {
        return verifyPassword;
    }

    public void setVerifyPassword(String verifyPassword) {
        this.verifyPassword = verifyPassword;
    }

}
