package com.petboarding.controllers;

import com.petboarding.models.User;
import com.petboarding.models.app.Module;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AppBaseController {
    // TODO: Move the modules to a table in the database
    List<Module> appModules = new ArrayList<>();

    public AppBaseController() {
        appModules.add(new Module("home", "Home", "bi-house", "employee"));
        appModules.add(new Module("owners", "Pet Owners", "bi-person-vcard", "employee"));
        appModules.add(new Module("pets", "Pets", "paw-icon", "employee"));
        appModules.add(new Module("kennels", "Kennels", "bi-grid-3x3-gap-fill", "employee"));
        appModules.add(new Module("reservations", "Reservations", "bi-calendar-plus", "employee"));
        appModules.add(new Module("stays", "Stays", "bi-calendar-check", "employee"));
        appModules.add(new Module("invoices", "Invoices", "bi-receipt", "employee"));
        appModules.add(new Module("employees", "Employees", "bi-person-rolodex", "admin"));
        appModules.add(new Module("users", "Users", "bi-people-fill", "admin"));
    }

    @ModelAttribute("modules")
    public List<Module> addAppModules(HttpSession httpSession) {
        User user = (User) httpSession.getAttribute("user");
        //TODO: Allow pass if not user is present only for testing, !IMPORTANT [REMOVE CONDITION]
        if(user != null) {
            List<Module> allowedModules = new ArrayList<>();
            for (Module module : appModules) {
                if (user.isAdmin() || module.getRole().equalsIgnoreCase(user.getRole().getName()))
                    allowedModules.add(module);
            }
            return allowedModules;
        }
        return appModules;
    }

    @ModelAttribute("user")
    public User addUserInSession(HttpServletRequest request) {
        return (User)request.getSession().getAttribute("user");
    }

    public void setActiveModule(String path, Model model) {
        List<String> locations = getPaths(path);
        // get active main module
        model.addAttribute("activeModule", getActiveModule(locations.get(0)));
        // add locations
        if(locations.size() > 0) locations.remove(0);
        model.addAttribute("moduleLocations", locations);
    }

    public void addLocation(String newLocation, Model model) {
        List<String> locations;
        locations = model.containsAttribute("moduleLocations") ? (List<String>) model.getAttribute("moduleLocations") : new ArrayList<>();
        locations.addAll(getPaths(newLocation));
        model.addAttribute("moduleLocations", locations);
    }

    private List<String> getPaths(String path) {
        String pathSeparator = "/";
        String []paths = path.split(pathSeparator);
        return new ArrayList<>(Arrays.asList(paths));
    }

    public Module getActiveModule(String path) {
        return getModule(path);
    }

    public Module getModule(String path) {
        return appModules
                .stream()
                .filter(module -> path.equalsIgnoreCase(module.getPath()))
                .findFirst()
                .orElse(Module.dummyFactory());
    }

}
