package com.petboarding.service;

import com.petboarding.exception.UserNotFoundException;
import com.petboarding.models.Employee;
import com.petboarding.models.User;
import com.petboarding.models.data.EmployeeRepository;
import com.petboarding.models.data.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class PasswordResetService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    public void updateResetPasswordToken(String token, String email) throws UserNotFoundException {
        Optional<Employee> employee = Optional.ofNullable(employeeRepository.findByEmail(email));
        if (employee.isPresent()){
            Optional<User> user = Optional.ofNullable(employee.get().getUser());
            if (user.isPresent()) {
                user.get().setResetPasswordToken(token);
            } else {
                throw new UserNotFoundException("Could not find any user with the email " + email);
            }
        } else {
            throw new UserNotFoundException("Could not find any user with the email " + email);
        }
    }

    public User getByResetPasswordToken(String token) {
        return userRepository.findByResetPasswordToken(token);
    }

    public void updatePassword(User user, String newPassword) {

        user.setPwHash(newPassword);

        user.setResetPasswordToken(null);
        userRepository.save(user);
    }

    public static String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

}
