package com.jean.loginreg.controllers;

import java.security.Principal;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jean.loginreg.auth.validator.UserValidator;
import com.jean.loginreg.models.User;
import com.jean.loginreg.services.UserService;

@Controller
public class UserController {
	private UserService userService;
	private UserValidator userValidator;
    
    public UserController(UserService userService, UserValidator userValidator) {
    		this.userService = userService;
    		this.userValidator = userValidator;
    }
	
	@RequestMapping("/login")
    public String login(@Valid @ModelAttribute("user") User user, @RequestParam(value="error", required=false) String error, @RequestParam(value="logout", required=false) String logout, Model model) {
        if(error != null) {
            model.addAttribute("errorMessage", "Invalid Credentials, Please try again.");
        }
        if(logout != null) {
            model.addAttribute("logoutMessage", "Logout Successfull!");
        }
        return "loginReg.jsp";
    }
	
	@PostMapping("/registration")
    public String registration(@Valid @ModelAttribute("user") User user, BindingResult result, Model model, HttpSession session) {
		userValidator.validate(user, result);
		if (result.hasErrors()) {
			return "loginReg.jsp";
		}
		session.setAttribute("created", "Account created. Please log in.");
		userService.saveUserWithAdminRole(user);
		return "redirect:/login";
    }
	
	@RequestMapping("/loggedin")
	public String updatedLoggedIn(Principal principal, HttpSession session) {
		session.removeAttribute("created");
		String email = principal.getName();
        userService.updateLoggedIn(userService.findByEmail(email));
        System.out.println("logged in");
        return "redirect:/";
	}
	
	@RequestMapping("/")
    public String home(Principal principal, Model model) {
        String email = principal.getName();
        model.addAttribute("currentUser", userService.findByEmail(email));
        return "home.jsp";
    }
	@RequestMapping("/admin")
    public String adminPage(Principal principal, Model model) {
        String email = principal.getName();
        model.addAttribute("currentUser", userService.findByEmail(email));
        return "adminPage.jsp";
    }
}
