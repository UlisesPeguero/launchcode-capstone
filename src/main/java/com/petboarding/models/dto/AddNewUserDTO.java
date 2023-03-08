package com.petboarding.models.dto;

import com.petboarding.models.Employee;
import com.petboarding.models.Role;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class AddNewUserDTO extends RegisterFormDTO{

    @Valid
    @NotNull(message = "Select a valid user Role.")
    private Role role;

    @Valid
    @NotNull(message = "Select a valid Employee.")
    private Employee employee;

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
