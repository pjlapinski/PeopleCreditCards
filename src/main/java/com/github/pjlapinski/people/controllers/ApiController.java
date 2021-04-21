package com.github.pjlapinski.people.controllers;

import com.github.pjlapinski.people.util.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/person")
public class ApiController {

    @Autowired
    private PersonRepository pr;

    @GetMapping("/{id}")
    public Object getPerson(@PathVariable(value = "id") final String id) {
        var person = pr.findById(Long.valueOf(id));
        return person.isPresent() ? person.get() : "no person with id " + id;
    }

    @PostMapping("/delete/{id}")
    public void deletePerson(@PathVariable(value = "id") final String id) {
        pr.deleteById(Long.valueOf(id));
    }
}
