package com.petboarding;

import com.petboarding.controllers.AuthenticationController;
import com.petboarding.models.User;
import com.petboarding.models.data.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AuthenticationFilter extends HandlerInterceptorAdapter {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationController authenticationController;

    private static final List<String> whitelist = Arrays.asList("/css/login.css", "/img/logo.svg");
    private static final List<String> EMPLOYEE_WHITELIST = Arrays.asList("/users", "/employees");

    private static boolean isWhitelisted(String path, User user) {
        if (user != null) {
                return !path.startsWith("/users") && !path.startsWith("/employees");
            }
        return whitelist.contains(path);
    }

    private static boolean isWhitelisted(String path) {
        for (String pathRoot : whitelist) {
            if (path.startsWith(pathRoot)) {
                return true;
            }
        }
        return false;
    }
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws IOException {

        String path = request.getRequestURI();
        HttpSession session = request.getSession();
        User user = authenticationController.getUserFromSession(session);

        if (isWhitelisted(path, user)) {
            return true;
        }

        if (path.startsWith("/sign-in")){
            return true;
        }

        // Check if user is logged
        if (user != null) {
            if (user.isAdmin()) {
                // allows admins to access any non-whitelisted path
                return true;
            } else {
                // allows employees to only access other paths
                if (!path.startsWith("/users")  && !path.startsWith("/employees")) {
                    return true;
                } else {
                    //redirects to home page if trying to access different paths
                    response.sendRedirect("/");
                    return false;
                }
            }
        }

        // Redirect to login page
        response.sendRedirect("/sign-in/login");
        return false;
    }

}