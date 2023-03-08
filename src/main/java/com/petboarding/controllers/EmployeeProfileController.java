package com.petboarding.controllers;

import com.petboarding.controllers.utils.FileUploadUtil;
import com.petboarding.models.Employee;
import com.petboarding.models.User;
import com.petboarding.models.app.Module;
import com.petboarding.models.data.EmployeeRepository;
import com.petboarding.models.data.PositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("myprofile")
public class EmployeeProfileController extends AppBaseController{

    private final String FORM_UPDATE_TITLE = "Update: ${employeeName}";

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    private PositionRepository positionRepository;

    @GetMapping("")
    public String displayEmployeeProfile(Model model, RedirectAttributes redirectAttributes, HttpSession httpSession){

        User user = (User) httpSession.getAttribute("user");

        model.addAttribute("employee", user.getEmployee());
        addLocation("my profile/" + user.getEmployee().getFullName(), model);
        return "employees/profile";
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
        return "redirect:/myprofile";
    }

    private void prepareCommonFormModel(Model model) {
        model.addAttribute("positions", positionRepository.findAll());
    }

    private void prepareUpdateFormModel(Employee employee, Model model) {
        model.addAttribute("formTitle", FORM_UPDATE_TITLE.replace("${employeeName}", employee.getFullName()));
        model.addAttribute("employee", employee);
        model.addAttribute("submitURL", "/myprofile/update/" + employee.getId());
        model.addAttribute("submitMethod", "post");
        addLocation("Update", model);
        prepareCommonFormModel(model);
    }

    @ModelAttribute("activeModule")
    public Module addActiveModule() {
        return getActiveModule("employees");
    }

}
