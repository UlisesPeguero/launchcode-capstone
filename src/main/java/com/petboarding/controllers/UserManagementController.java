package com.petboarding.controllers;

import com.petboarding.models.Employee;
import com.petboarding.models.Role;
import com.petboarding.models.app.Module;
import com.petboarding.models.data.EmployeeRepository;
import com.petboarding.models.data.OwnerRepository;
import com.petboarding.models.data.RoleRepository;
import com.petboarding.models.data.UserRepository;
import com.petboarding.models.dto.AddNewUserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import com.petboarding.models.User;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("users")
public class UserManagementController extends AppBaseController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private OwnerRepository ownerRepository;

    public List<User> listUsers() {
        return userRepository.findAll();
    }

    public List<Employee> listEmployees() {
        return employeeRepository.findAll();
    }

    public List<Role> listRoles() {
        return roleRepository.findAll();
    }

    public List<Employee> filteredEmployeeList() {
        // filters employees that are not already associated with a user
        Map<Integer, Integer> employeeIds = listUsers().stream().filter(user -> user.getEmployee() != null)
                .collect(Collectors.toMap(user -> user.getEmployee().getId(), user -> user.getId()));
        List<Employee> filteredEmployees = new ArrayList<>();
        for (Employee employee : listEmployees()) {
            if (!employeeIds.containsKey(employee.getId())) {
                filteredEmployees.add(employee);
            }
        }
        return filteredEmployees;
    }

    @GetMapping("/addUserForm")
    public String addUserForm(Model model) {
        model.addAttribute("listRoles", listRoles());
        model.addAttribute("listEmployees", filteredEmployeeList());
        model.addAttribute("addNewUserDTO", new AddNewUserDTO());
        addLocation("Add", model);
        return "users/addUserForm";
    }

    @PostMapping("/addUserForm")
    public String addUser(@ModelAttribute @Valid AddNewUserDTO addNewUserDTO, BindingResult result, Model model,
            Errors errors) {
        boolean fieldError = false;
        User existingUser = userRepository.findByUsername(addNewUserDTO.getUsername());
        if (existingUser != null) {
            errors.rejectValue("username", "username.alreadyexists", "A user with that username already exists");
            fieldError = true;
        }

        String password = addNewUserDTO.getPassword();
        String verifyPassword = addNewUserDTO.getVerifyPassword();
        if (!password.equals(verifyPassword)) {
            errors.rejectValue("password", "passwords.mismatch", "Passwords do not match");
            fieldError = true;
        }

        if (errors.hasErrors() || fieldError) {
            model.addAttribute("listRoles", listRoles());
            model.addAttribute("listEmployees", filteredEmployeeList());
            return "users/addUserForm";
        }

        User newUser = new User(addNewUserDTO.getUsername(), addNewUserDTO.getPassword(), addNewUserDTO.getRole(),
                addNewUserDTO.getEmployee());
        userRepository.save(newUser);
        return "redirect:/users";
    }

    @GetMapping("")
    public String listUsers(@RequestParam(required = false, defaultValue = "false") Boolean showAll, Model model) {
        model.addAttribute("listUsers", showAll ? userRepository.findAll() : userRepository.findByActive(true));
        model.addAttribute("showAll", showAll);
        return "users/userList";
    }

    @GetMapping("/editUserForm/{id}")
    public String editUser(@PathVariable("id") int id, Model model) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            model.addAttribute("listRoles", listRoles());
            addLocation("Update", model);
        }
        return "users/editUserForm";
    }

    // Saves user profile changes in editUserForm
    @Transactional
    @PostMapping("/saveUser")
    public String saveUser(@RequestParam int id, @RequestParam String username, @RequestParam Role role,
            @RequestParam(required = false) Boolean active, @RequestParam(required = false) String password) {

        Optional<User> optUser = userRepository.findById(id);
        if (optUser.isPresent()) {
            User user = optUser.get();

            if (active != null) {
                user.setActive(active);
            }
            user.setUsername(username);
            user.setRole(role);
        }
        return "redirect:/users";
    }

    @Transactional
    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable int id, RedirectAttributes redirectAttributes) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            String username = user.get().getUsername();
            redirectAttributes.addFlashAttribute("infoMessage",
                    "The account for <strong>" + username + "</strong>  was successfully deactivated.");
            user.get().setActive(false);
            // userRepository.delete(user.get());
        }
        return "redirect:/users";
    }

    @GetMapping("/saveTheme")
    public String processSaveTheme(@RequestParam String theme, @RequestParam(defaultValue = "null") String origin,
            HttpSession session) {
        if (session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            user.setTheme(theme);
            userRepository.save(user);
        }
        return "redirect:" + (origin == null ? "/home" : origin);
    }

    @ModelAttribute("activeModule")
    public Module addActiveModule() {
        return getActiveModule("users");
    }
}
