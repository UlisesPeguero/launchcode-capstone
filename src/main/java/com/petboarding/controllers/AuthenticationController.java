package com.petboarding.controllers;

import com.petboarding.models.dto.ResetPasswordDTO;
import com.petboarding.service.PasswordResetService;
import com.petboarding.exception.UserNotFoundException;
import com.petboarding.models.User;
import com.petboarding.models.data.EmployeeRepository;
import com.petboarding.models.data.RoleRepository;
import com.petboarding.models.data.UserRepository;
import com.petboarding.models.dto.LoginFormDTO;
import com.petboarding.models.dto.RegisterFormDTO;
import com.petboarding.service.EmailService;
import net.bytebuddy.utility.RandomString;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.Optional;

@Controller
@RequestMapping("sign-in")
public class AuthenticationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    PasswordResetService passwordResetService;

    private static final String userSessionKey = "user";

    public User getUserFromSession(HttpSession session) {
        User user = (User) session.getAttribute(userSessionKey);
        if (user == null) {
            return null;
        }
        Optional<User> optUser = userRepository.findById(user.getId());
        if (optUser.isEmpty()) {
            return null;
        }
        return optUser.get();
    }

    private static void setUserInSession(HttpSession session, User user) {
        session.setAttribute(userSessionKey, user);
    }

    @GetMapping("/register")
    public String displayRegistrationForm(Model model) {
        model.addAttribute(new RegisterFormDTO());
        return "sign-in/register";
    }

    @PostMapping("/register")
    public String processRegistrationForm(@ModelAttribute @Valid RegisterFormDTO registerFormDTO, Errors errors,
            HttpServletRequest request, Model model) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Register");
            return "sign-in/register";
        }

        User existingUser = userRepository.findByUsername(registerFormDTO.getUsername());

        if (existingUser != null) {
            errors.rejectValue("username", "username.alreadyexists", "A user with that username already exists");
            model.addAttribute("title", "Register");
            return "sign-in/register";
        }

        String password = registerFormDTO.getPassword();
        String verifyPassword = registerFormDTO.getVerifyPassword();
        if (!password.equals(verifyPassword)) {
            errors.rejectValue("password", "passwords.mismatch", "Passwords do not match");
            model.addAttribute("title", "Register");
            return "sign-in/register";
        }
        User newUser = new User(registerFormDTO.getUsername(), registerFormDTO.getPassword());
        userRepository.save(newUser);
        setUserInSession(request.getSession(), newUser);

        return "redirect:/home";
    }

    @GetMapping("/login")
    public String displayLoginForm(Model model) {
        model.addAttribute(new LoginFormDTO());
        model.addAttribute("title", "Log In");
        return "sign-in/login";
    }

    @PostMapping("/login")
    public String processLoginForm(@ModelAttribute @Valid LoginFormDTO loginFormDTO, BindingResult result, Model model,
            HttpServletRequest request, Errors errors) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Log In");
            return "sign-in/login";
        }
        User theUser = userRepository.findByUsername(loginFormDTO.getUsername());

        if (theUser == null) {
            errors.rejectValue("username", "user.invalid", "Invalid username and/or password");
            model.addAttribute("title", "Log In");
            return "sign-in/login";
        }

        String password = loginFormDTO.getPassword();
        if (!theUser.isMatchingPassword(password)) {
            errors.rejectValue("password", "password.invalid", "Invalid username and/or password");
            model.addAttribute("title", "Log In");
            return "sign-in/login";
        }

        setUserInSession(request.getSession(), theUser);

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/sign-in/login";
    }

    // <---------------------------------------------Password
    // Reset--------------------------------------------->

    @GetMapping("forgotPassword")
    public String forgotPasswordForm() {
        return "sign-in/forgotPassword";
    }

    @PostMapping("forgotPassword")
    public String processForgotPasswordForm(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        String token = RandomString.make(30);

        try {
            passwordResetService.updateResetPasswordToken(token, email);
            String resetPasswordLink = PasswordResetService.getSiteURL(request) + "/sign-in/resetPassword/" + token;
            emailService.sendResetPasswordLink(email, resetPasswordLink);
            model.addAttribute("infoMessage", "We have sent a password reset link to your email. Please check.");

        } catch (UserNotFoundException ex) {
            model.addAttribute("errorMessage",
                    "Error while sending email. Please check the email provided and try again.");
        } catch (UnsupportedEncodingException | MessagingException e) {
            model.addAttribute("errorMessage",
                    "Error while sending email. Please check the email provided and try again.");
        }
        return "sign-in/forgotPassword";
    }

    @GetMapping("resetPassword/{token}")
    public String resetPasswordForm(@PathVariable("token") String token, Model model) {

        User user = passwordResetService.getByResetPasswordToken(token);
        model.addAttribute("token", token);
        model.addAttribute("resetPasswordDTO", new ResetPasswordDTO());

        if (user == null) {
            model.addAttribute("errorMessage", "Link has expired");
            return "sign-in/login";
        }

        return "sign-in/resetPassword";
    }

    @PostMapping("resetPassword/{token}")
    public String resetPasswordSubmit(@ModelAttribute("resetPasswordDTO") ResetPasswordDTO resetPasswordDTO,
            BindingResult result, Model model, @PathVariable("token") String token,
            RedirectAttributes redirectAttributes) {
        User user = passwordResetService.getByResetPasswordToken(token);
        if (user == null) {
            model.addAttribute("errorMessage", "Link has expired");
            return "sign-in/login";
        }
        if (!resetPasswordDTO.getPassword().equals(resetPasswordDTO.getVerifyPassword())) {
            result.rejectValue("password", "error.password", "Passwords do not match");
            model.addAttribute("errorMessage", "Passwords do not match");
            model.addAttribute("token", token);
            return "sign-in/resetPassword";
        }
        passwordResetService.updatePassword(user, resetPasswordDTO.getPassword());
        model.addAttribute(new LoginFormDTO());
        redirectAttributes.addFlashAttribute("infoMessage", "Your password has been reset.");
        return "redirect:/sign-in/login";
    }
}