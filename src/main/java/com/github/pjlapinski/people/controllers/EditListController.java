package com.github.pjlapinski.people.controllers;

import com.github.pjlapinski.people.models.Person;
import com.github.pjlapinski.people.util.PersonRepository;
import com.github.pjlapinski.people.util.user.CustomUserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
public class EditListController {

    @Autowired
    private PersonRepository pr;

    @GetMapping("/add")
    public String personForm(Model model) {
        var person = new Person();
        model.addAttribute("person", person);
        return "personForm";
    }

    @PostMapping("/add")
    public String addPerson(@Valid @ModelAttribute("person") Person person, Errors errors, Authentication authentication) {
        if (errors.hasErrors()) {
            return "personForm";
        }
        var user = (CustomUserPrincipal) authentication.getPrincipal();
        person.setCreatedBy(user.getModel());
        pr.save(person);
        return "redirect:/list";
    }

    @GetMapping("/edit/{id}")
    public String editPersonForm(@PathVariable(value = "id") final String id,
                                 Model model,
                                 Authentication authentication,
                                 HttpServletRequest request) {
        var p = pr.findById(Long.valueOf(id));
        if (p.isEmpty())
            return "redirect:/";
        var person = p.get();
        var user = (CustomUserPrincipal) authentication.getPrincipal();
        if (!(person.getCreatedBy().getUsername().equals(user.getUsername()) || request.isUserInRole("ADMIN")))
            return "redirect:/";
        var newPerson = new Person();
        newPerson.setId(person.getId());
        newPerson.setFirstName(person.getFirstName());
        newPerson.setLastName(person.getLastName());
        newPerson.setGender(person.getGender());
        newPerson.setEmail(person.getEmail());
        newPerson.setCreditCardNumber(person.getCreditCardNumber());
        newPerson.setCreditCardType(person.getCreditCardType());
        model.addAttribute("person", newPerson);
        return "personForm";
    }

    @PostMapping("/edit")
    public String editPerson(@Valid @ModelAttribute("person") Person newPerson,
                             Errors errors,
                             Authentication authentication,
                             HttpServletRequest request) {
        if (errors.hasErrors())
            return "personForm";
        var p = pr.findById(newPerson.getId());
        if (p.isEmpty())
            return "redirect:/";
        var person = p.get();
        var user = (CustomUserPrincipal) authentication.getPrincipal();
        if (!(person.getCreatedBy().getUsername().equals(user.getUsername()) || request.isUserInRole("ADMIN")))
            return "redirect:/";
        person.setFirstName(newPerson.getFirstName());
        person.setLastName(newPerson.getLastName());
        person.setGender(newPerson.getGender());
        person.setEmail(newPerson.getEmail());
        person.setCreditCardNumber(newPerson.getCreditCardNumber());
        person.setCreditCardType(newPerson.getCreditCardType());
        pr.save(person);
        return "redirect:/list";
    }
}
