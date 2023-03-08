package com.petboarding.controllers;

import com.petboarding.models.Role;
import com.petboarding.models.User;
import com.petboarding.models.data.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("users/roles")
public class RoleController extends AppBaseController {

    @Autowired
    private RoleRepository roleRepository;

    public List<Role> listRoles() {
        return roleRepository.findAll();
    }

    @GetMapping("")
    public String roleList(@RequestParam(required = false, defaultValue = "false") Boolean showAll, Model model) {
        model.addAttribute("listRoles", showAll ? listRoles() : roleRepository.findByActive(true));
        model.addAttribute("showAll", showAll);
        return "users/roles/roleList";
    }

    @GetMapping("/addRoleForm")
    public String addRoleForm(Model model) {
        model.addAttribute("role", new Role());
        addLocation("Add", model);
        return "users/roles/addRoleForm";
    }

    @GetMapping("/editRoleForm/{id}")
    public String editRoleForm(@PathVariable("id") Integer id, Model model) {
        Optional<Role> role = roleRepository.findById(id);
        if (role.isPresent()) {
            addLocation("Update", model);
            model.addAttribute("role", role.get());
        }
        return "users/roles/editRoleForm";
    }

    @Transactional
    @PostMapping("/saveRole")
    public String createRole(@ModelAttribute Role role, @RequestParam(required = false) Boolean active) {
        roleRepository.save(role);
        if (active != null) {
            role.setActive(active);
        }
        return "redirect:/users/roles";
    }

    @Transactional
    @PostMapping("/delete/{id}")
    public String deleteRole(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        Role role = roleRepository.findById(id).get();
        List<User> users = role.getUsers();
        if (!users.isEmpty()) {
            model.addAttribute("errorMessage",
                    "This role is currently assigned to one or more users and cannot be deactivated.");
            model.addAttribute("role", role);
            return "users/roles/deleteRoleForm";
        } else {
            String name = role.getName();
            redirectAttributes.addFlashAttribute("infoMessage",
                    "User: <strong>" + name + "</strong>  was successfully deactivated.");
            role.setActive(false);
            return "redirect:/users/roles";
        }
    }

    @ModelAttribute
    public void addActiveModule(Model model) {
        setActiveModule("users/Role List", model);
    }
}
