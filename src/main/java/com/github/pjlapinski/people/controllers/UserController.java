package com.github.pjlapinski.people.controllers;

import com.github.pjlapinski.people.models.UserDTO;
import com.github.pjlapinski.people.util.UserRepository;
import com.github.pjlapinski.people.util.user.CustomUserPrincipal;
import com.github.pjlapinski.people.util.user.UserExistsException;
import com.github.pjlapinski.people.util.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
public class UserController {

    private final String websiteAddress = "http://localhost:5000/";

    @Autowired
    private JavaMailSender emailSender;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository ur;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new UserDTO());
        return "register";
    }

    @GetMapping("/activate/{id}")
    public String activateAccount(@PathVariable(value = "id") long id) {
        var userOptional = ur.findById(id);
        userOptional.ifPresent(user -> {
            user.setActive(true);
            ur.save(user);
        });
        return "redirect:/";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") @Valid UserDTO user, Errors errors, Model model) {
        if (errors.hasErrors())
            return "register";
        if (!user.getPassword().equals(user.getMatchingPassword())) {
            model.addAttribute("message", "Passwords don't match");
            return "register";
        }
        try {
            userService.register(user);
        } catch (UserExistsException e) {
            model.addAttribute("message", e.getMessage());
            return "register";
        }
        var users = ur.findByUsername(user.getUsername());
        if (users.size() <= 0) return "redirect:/";
        var usr = users.get(0);
        var msg = new SimpleMailMessage();
        msg.setTo(user.getEmail());
        msg.setSubject("Thank you for registering to the People App");
        msg.setText("To activate your account, please visit: " + websiteAddress + "activate/" + usr.getId());
        emailSender.send(msg);
        return "redirect:/";
    }

    @PostMapping("/login_failed")
    public String failedLogin() {return "redirect:/login?errors=true";}

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) Boolean errors, Model model) {
        if (errors != null)
            model.addAttribute("message", "Invalid username and password");
        return "login";
    }

    @GetMapping("/account")
    public String accountPage() {return "account";}

    @GetMapping("/remove-account")
    public String removeAccountPage() {return "confirmRemoveAccount";}

    @PostMapping("/remove-account")
    public String sendRemoveAccountEmail(Authentication authentication) {
        var user = (CustomUserPrincipal) authentication.getPrincipal();
        var msg = new SimpleMailMessage();
        var userModel = ur.findByUsername(user.getUsername()).get(0);
        msg.setTo(user.getEmail());
        msg.setSubject("People App account removal");
        msg.setText("If you'd like to confirm the removal of your account, please visit: "
                + websiteAddress + "confirmed-remove-account/" + userModel.getId());
        emailSender.send(msg);
        userModel.setRequestedAccountRemoved(true);
        return "redirect:/";
    }

    @GetMapping("/confirmed-remove-account/{id}")
    public String confirmAccountRemoval(@PathVariable(value = "id") long id,
                                        Authentication authentication,
                                        HttpServletRequest request) {
        var userOptional = ur.findById(id);
        if (userOptional.isPresent()) {
            var user = userOptional.get();
            if (authentication.isAuthenticated() &&
                    !user.getUsername().equals(((CustomUserPrincipal)authentication.getPrincipal()).getUsername())) {
                user.setRequestedAccountRemoved(false);
                return "redirect:/";
            } else if (authentication.isAuthenticated()) {
                try {
                    request.logout();
                } catch (ServletException e) {
                    e.printStackTrace();
                }
            }
            ur.delete(user);
        }
        return "redirect:/";
    }

    @GetMapping("/change-information")
    public String changeAccountInformationPage(Model model, Authentication authentication) {
        var newUser = new UserDTO();
        var user = ((CustomUserPrincipal) authentication.getPrincipal()).getModel();
        newUser.setUsername(user.getUsername());
        newUser.setEmail(user.getEmail());
        newUser.setId(user.getId());
        model.addAttribute("user", newUser);
        return "changeAccountInfo";
    }

    @PostMapping("/change-information")
    public String changeAccountInformation(@ModelAttribute("user") @Valid UserDTO newUser, Errors errors) {
        if (errors.hasErrors())
            return "changeAccountInfo";
        ur.findById(newUser.getId()).ifPresent(user -> {
            user.setUsername(newUser.getUsername());
            user.setPassword(passwordEncoder.encode(newUser.getPassword()));
            ur.save(user);
        });
        return "redirect:/";
    }

}
