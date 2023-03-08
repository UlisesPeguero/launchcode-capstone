package com.petboarding.controllers;

import com.petboarding.models.Position;
import com.petboarding.models.app.Module;
import com.petboarding.models.data.EmployeeRepository;
import com.petboarding.models.data.PositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("employees/positions")
public class PositionController extends AppBaseController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PositionRepository positionRepository;

    private final String VIEW_BASE_PATH = "employees/positions";
    private final String FORM_NEW_TITLE = "New Job Position";
    private final String FORM_UPDATE_TITLE = "Update: ${name}";

    @GetMapping
    public String displayEmployeesGrid(@RequestParam(required = false, defaultValue = "false") Boolean showAll, Model model) {
        Sort sortAscName = Sort.by(Sort.Direction.ASC, "name");
        model.addAttribute("positions", showAll ? positionRepository.findAll(sortAscName) : positionRepository.findByActive(true, sortAscName));
        model.addAttribute("showAll", showAll);
        return VIEW_BASE_PATH + "/index";
    }

    @GetMapping("add")
    public String displayAddEmployeeForm(Model model) {
        prepareAddFormModel(model);
        return VIEW_BASE_PATH + "/form";
    }

    @PostMapping("add")
    public String processAddEmployeeRequest(@Valid @ModelAttribute Position newPosition, Errors errors, Model model) {
        if(errors.hasErrors()) {
            model.addAttribute("positions", positionRepository.findAll());
            return VIEW_BASE_PATH + "/form";
        }
        positionRepository.save(newPosition);
        return "redirect:/employees/positions";
    }

    @GetMapping("update/{id}")
    public String displayUpdateEmployeeForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Position> optPosition = positionRepository.findById(id);
        if(optPosition.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "The position ID:" + id + " couldn't be found.");
            return "redirect:/employees/positions";
        }
        prepareUpdateFormModel(optPosition.get(), model);
        return VIEW_BASE_PATH + "/form";
    }

    @PostMapping("update/{id}")
    public String processUpdateEmployeeRequest(@PathVariable Integer id, @Valid Position position, Errors errors, Model model, RedirectAttributes redirectAttributes) {
        if(errors.hasErrors()) {
            return VIEW_BASE_PATH + "/form";
        }
        positionRepository.save(position);
        redirectAttributes.addFlashAttribute("infoMessage", "The Job Position has been updated.");
        return "redirect:" + id;
    }

    @PostMapping("delete/{id}")
    public String processDeleteEmployeeRequest(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        Optional<Position> optPosition = positionRepository.findById(id);
        if(optPosition.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "The position ID:" + id + " couldn't be found.");
        } else {
            Position position = optPosition.get();
            String name = position.getName();
            if(position.getEmployees().size() > 0 ) {
                redirectAttributes.addFlashAttribute("errorMessage", "Jop Position <strong>" + name + "</strong> is linked to employees and can't be deleted.");
            } else {
                positionRepository.deleteById(id);
                redirectAttributes.addFlashAttribute("infoMessage", "Job Position: <strong>" + name + "</strong>  was successfully deleted.");
            }
        }
        return "redirect:/employees/positions";
    }

    @ModelAttribute
    public void addActiveModule(Model model) {
        setActiveModule("employees/Job Positions", model);
    }

    private void prepareAddFormModel(Model model) {
        model.addAttribute("formTitle", FORM_NEW_TITLE);
        model.addAttribute("position", new Position());
        model.addAttribute("submitURL", "/employees/positions/add");
        model.addAttribute("submitMethod", "post");
        addLocation("New", model);
    }

    private void prepareUpdateFormModel(Position position, Model model) {
        model.addAttribute("formTitle", FORM_UPDATE_TITLE.replace("${name}", position.getName()));
        model.addAttribute("position", position);
        model.addAttribute("submitURL", "/employees/positions/update/" + position.getId());
        model.addAttribute("submitMethod", "post");
        addLocation("Update", model);
    }
}
