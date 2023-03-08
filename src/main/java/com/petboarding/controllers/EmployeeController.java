package com.petboarding.controllers;

import com.petboarding.models.Employee;
import com.petboarding.models.app.Module;
import com.petboarding.models.data.EmployeeRepository;
import com.petboarding.models.data.PositionRepository;
import com.petboarding.models.data.UserRepository;
import com.petboarding.controllers.utils.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("employees")
public class EmployeeController extends AppBaseController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PositionRepository positionRepository;

    private final String FORM_NEW_TITLE = "New Employee";
    private final String FORM_UPDATE_TITLE = "Update: ${employeeName}";
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String displayEmployeesGrid(@RequestParam(required = false, defaultValue = "false") Boolean showAll, Model model) {
        Sort sortAscNames = Sort.by("lastName").ascending().and(Sort.by("firstName").ascending());
        model.addAttribute("employees", showAll ? employeeRepository.findAll(sortAscNames) : employeeRepository.findByActive(true, sortAscNames));
        model.addAttribute("showAll", showAll);
        return "employees/index";
    }

    @GetMapping("add")
    public String displayAddEmployeeForm(Model model) {
        prepareAddFormModel(new Employee(), model);
        return "employees/form";
    }

    @Transactional
    @PostMapping("add")
    public String processAddEmployeeRequest(@Valid @ModelAttribute Employee newEmployee, Errors errors, Model model, @RequestParam(value = "image", required = false) MultipartFile multipartFile) throws IOException {

        Employee existingEmployee = employeeRepository.findByEmail(newEmployee.getEmail());
//      checks to see if an email is already associated with an employee
        if (existingEmployee != null) {
            errors.rejectValue("email", "email.alreadyexists", "This email is already associated with another employee");
        }

            newEmployee.setPhoto(null);
        if(errors.hasErrors()) {
            prepareAddFormModel(newEmployee, model);
            return "employees/form";
        }
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        if (!"".equals(fileName)){
            String uploadDir = "uploads/employee-photos/" + newEmployee.getId();
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
            newEmployee.setPhoto(fileName);
        }
        employeeRepository.save(newEmployee);

        return "redirect:/employees";

    }

    @GetMapping("update/{id}")
    public String displayUpdateEmployeeForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Employee> optEmployee = employeeRepository.findById(id);
        if(optEmployee.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "The employee ID:" + id + " couldn't be found.");
            return "redirect:/employees";
        }
        prepareUpdateFormModel(optEmployee.get(), model);
        return "employees/form";
    }

    @PostMapping("update/{id}")
    public String processUpdateEmployeeRequest(
            @PathVariable Integer id, @Valid @ModelAttribute Employee employee,
            Errors errors, Model model, RedirectAttributes redirectAttributes,
            @RequestParam(value = "image", required = false) MultipartFile multipartFile) throws IOException {
        if(errors.hasErrors()) {
            prepareUpdateFormModel(employee, model);
            return "employees/form";
        }
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        if (!"".equals(fileName)){
            String uploadDir = "uploads/employee-photos/" + employee.getId();
            if (!"".equals(employee.getPhoto()) && employee.getPhoto() != null){
                FileUploadUtil.deletePhoto(uploadDir, employee.getPhoto());
            }
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
            employee.setPhoto(fileName);
        } else if("".equals(employee.getPhoto())){
            employee.setPhoto(null);
        }
        employeeRepository.save(employee);
        redirectAttributes.addFlashAttribute("infoMessage", "The Employee information has been updated.");
        return "redirect:" + id;
    }

    @PostMapping("delete/{id}")
    public String processDeleteEmployeeRequest(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        Optional<Employee> optEmployee = employeeRepository.findById(id);
        if(optEmployee.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "The employee ID:" + id + " couldn't be found.");
        } else {
            Employee employee = optEmployee.get();
            if(employee.getUser() != null || !employee.getStays().isEmpty()) {
                redirectAttributes.addFlashAttribute("infoMessage", "Employee <strong>" + employee.getFullName() + "</strong> is linked to an user account or stays and cannot be deleted. It will be deactivated.");
                employee.setActive(false);
                employeeRepository.save(employee);
            } else {
                employeeRepository.deleteById(id);
                redirectAttributes.addFlashAttribute("infoMessage", "Employee was successfully deleted.");
            }
        }
        return "redirect:/employees";
    }

    @GetMapping("profile/{id}")
    public String displayEmployeeProfile(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes){
        Optional<Employee> optEmployee = employeeRepository.findById(id);
        if(optEmployee.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "The employee ID:" + id + " couldn't be found.");
            return "redirect:/employees";
        }
        Employee employee = optEmployee.get();
        addLocation("profile/" + employee.getFullName(), model);
        model.addAttribute("employee", employee);
        return "employees/profile";
    }

    @ModelAttribute("activeModule")
    public Module addActiveModule() {
        return getActiveModule(this.getClass().getAnnotation(RequestMapping.class).value()[0]);
    }

    private void prepareCommonFormModel(Model model) {
        model.addAttribute("positions", positionRepository.findAll());
    }
    private void prepareAddFormModel(Employee employee, Model model) {
        model.addAttribute("formTitle", FORM_NEW_TITLE);
        model.addAttribute("employee", employee);
        model.addAttribute("submitURL", "/employees/add");
        model.addAttribute("submitMethod", "post");
        addLocation("New", model);
        prepareCommonFormModel(model);
    }


    private void prepareUpdateFormModel(Employee employee, Model model) {
        model.addAttribute("formTitle", FORM_UPDATE_TITLE.replace("${employeeName}", employee.getFullName()));
        model.addAttribute("employee", employee);
        model.addAttribute("submitURL", "/employees/update/" + employee.getId());
        model.addAttribute("submitMethod", "post");
        addLocation("Update", model);
        prepareCommonFormModel(model);
    }
}
