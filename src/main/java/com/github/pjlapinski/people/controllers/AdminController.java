package com.github.pjlapinski.people.controllers;

import com.github.pjlapinski.people.models.UserDTO;
import com.github.pjlapinski.people.util.CSVHandler;
import com.github.pjlapinski.people.util.PersonRepository;
import com.github.pjlapinski.people.util.UserRepository;
import com.github.pjlapinski.people.util.user.CustomUserPrincipal;
import com.github.pjlapinski.people.util.user.UserExistsException;
import com.github.pjlapinski.people.util.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;

@Controller
@RequestMapping("/manage")
public class AdminController {
    @Autowired
    private UserRepository ur;
    @Autowired
    private PersonRepository pr;
    @Autowired
    private CSVHandler handler;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String adminPanel(Model model) {
        model.addAttribute("users", ur.findAll());
        return "adminPanel";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable(value = "id") String id) {
        ur.deleteById(Long.parseLong(id));
        return "redirect:/manage/";
    }

    @GetMapping("/users/edit/{id}")
    public String editUserPage(@PathVariable(value = "id") String id, Model model) {
        var user = ur.findById(Long.parseLong(id));
        if (user.isPresent()) {
            var usr = user.get();
            var newUser = new UserDTO();
            newUser.setId(usr.getId());
            newUser.setUsername(usr.getUsername());
            newUser.setEmail(usr.getEmail());
            model.addAttribute("user", newUser);
            return "editUser";
        }
        return "redirect:/manage/";
    }

    @PostMapping("/users/edit")
    public String editUser(@Valid @ModelAttribute("user") UserDTO newUser, Errors errors) {
        if (errors.hasErrors())
            return "editUser";
        var u = ur.findById(newUser.getId());
        if (u.isEmpty())
            return "redirect:/manage/";
        var user = u.get();
        user.setUsername(newUser.getUsername());
        user.setEmail(newUser.getEmail());
        user.setPassword(passwordEncoder.encode(newUser.getPassword()));
        ur.save(user);
        return "redirect:/manage/";
    }

    @PostMapping("/import")
    public String importPeople(Authentication authentication) {
        var userPrincipal = (CustomUserPrincipal) authentication.getPrincipal();
        var user = ur.findById(userPrincipal.getModel().getId());
        user.ifPresent(usr -> {
            try {
                pr.deleteAll(usr.getPeople());
                handler.importPeople("PersonOne.csv");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return "redirect:/manage/";
    }

    @GetMapping("/add-user")
    public String addUserPage(Model model) {
        model.addAttribute("user", new UserDTO());
        return "register";
    }

    @PostMapping("/add-user")
    public String addUser(@ModelAttribute("user") @Valid UserDTO user, Errors errors, Model model) {
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
        return "redirect:/manage/";
    }
}
