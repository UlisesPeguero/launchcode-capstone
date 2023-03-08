package com.petboarding.controllers;

import com.petboarding.models.Owner;
import com.petboarding.models.Pet;
import com.petboarding.models.app.Module;
import com.petboarding.models.data.EmployeeRepository;
import com.petboarding.models.data.OwnerRepository;
import com.petboarding.models.data.PetRepository;
import com.petboarding.controllers.utils.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("owners")
public class OwnerController extends AppBaseController{

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private PetRepository petRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

// Owner Home Page

    @RequestMapping()
    public String showMainOwnerPage(Model model) {
        Iterable<Owner> owners;
        owners = ownerRepository.findAll();

        model.addAttribute("title", "All Owners");
        model.addAttribute("owners", owners);
        this.setActiveModule("owners", model);

        return "owners/index";
    }

// Return to Pet Boarding Home page
    @GetMapping("/home")
    public String showDashboardHome(){
    return "redirect:../home";
}

// Adding Owners
    @GetMapping("/add")
    public String displayAddOwnerForm(Model model){
        this.setActiveModule("owners", model);
        model.addAttribute(new Owner());
        return "owners/add";
    }

    @Transactional
    @PostMapping("add")
    public String processAddOwnerForm(@Valid @ModelAttribute Owner newOwner, Errors errors, Model model, @RequestParam(value = "image", required = false) MultipartFile multipartFile) throws IOException {
        this.setActiveModule("owners", model);
        if(errors.hasErrors()){
            return "owners/add";
        }
        newOwner.setPhoto(null);
        Owner owner = ownerRepository.save(newOwner);

        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        if (!"".equals(fileName)){
            String uploadDir = "uploads/owner-photos/" + owner.getId();
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
            owner.setPhoto(fileName);
        }
        return "redirect:/owners";
    }

// View an individual Owner
    @GetMapping("view/{ownerId}")
    public String displayViewOwner(Model model, @PathVariable int ownerId){
        this.setActiveModule("owners", model);
        Optional optOwner = ownerRepository.findById(ownerId);
        if(optOwner.isPresent()){
            Owner owner = (Owner) optOwner.get();
            model.addAttribute("owner", owner);
            return "owners/view";
        } else {
            return "redirect:../owners";
        }
    }

// Updating Owners
    @GetMapping("updateOwner/{ownerId}")
    public String displayUpdateOwner(Model model, @PathVariable int ownerId){
        this.setActiveModule("owners", model);
        Optional optOwner = ownerRepository.findById(ownerId);
        if(optOwner.isPresent()){
            Owner owner = (Owner) optOwner.get();
            model.addAttribute("owner", owner);
            return "owners/updateOwner";
        } else {
            return "redirect:../owners";
        }
    }

    @PostMapping("updateOwner/{ownerId}")
    public String updateOwner(@Valid @ModelAttribute Owner owner, Errors errors, Model model, @PathVariable int ownerId, @RequestParam(value = "image", required = false) MultipartFile multipartFile) throws IOException {
        this.setActiveModule("owners", model);
        if(errors.hasErrors()){
            return "owners/updateOwner";
        }
        Owner updatedOwner = ownerRepository.findById(ownerId).get();
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        if (!"".equals(fileName)){
            String uploadDir = "uploads/owner-photos/" + updatedOwner.getId();
            if (!"".equals(updatedOwner.getPhoto()) && updatedOwner.getPhoto() != null){
                FileUploadUtil.deletePhoto(uploadDir, updatedOwner.getPhoto());
            }
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
            updatedOwner.setPhoto(fileName);
        } else if("".equals(updatedOwner.getPhoto())) {
            updatedOwner.setPhoto(null);
        }
        updatedOwner.setFirstName(owner.getFirstName());
        updatedOwner.setLastName(owner.getLastName());
        updatedOwner.setAddress(owner.getAddress());
        updatedOwner.setAddress2(owner.getAddress2());
        updatedOwner.setPhoneNumber(owner.getPhoneNumber());
        updatedOwner.setEmail(owner.getEmail());
        updatedOwner.setNotes(owner.getNotes());
        ownerRepository.save(updatedOwner);

        return "redirect:/owners";
    }

// Delete Owner
    @RequestMapping("/{ownerId}")
    public String deleteOwner(@PathVariable("ownerId") int ownerId){
        ownerRepository.deleteById(ownerId);

        return "redirect:../owners";
    }

    @GetMapping("/profile/{id}")
    public String displayEmployeeProfile(@PathVariable("id") int id, Model model, RedirectAttributes redirectAttributes){
        Optional<Owner> optOwner = ownerRepository.findById(id);
        if(optOwner.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "The Owner ID:" + id + " couldn't be found.");
            return "redirect:/owners";
        }
        model.addAttribute("owner", optOwner.get());
        return "owners/profile";
    }

    @ModelAttribute("activeModule")
    public Module addActiveModule() {
        return getActiveModule(this.getClass().getAnnotation(RequestMapping.class).value()[0]);
    }

}
