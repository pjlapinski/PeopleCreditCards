package com.github.pjlapinski.people.controllers;

import com.github.pjlapinski.people.models.Person;
import com.github.pjlapinski.people.util.CSVHandler;
import com.github.pjlapinski.people.util.PersonRepository;
import com.github.pjlapinski.people.util.UserRepository;
import com.github.pjlapinski.people.util.user.CustomUserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.CollectionUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class ListController {

    @Autowired
    private PersonRepository pr;
    @Autowired
    private UserRepository ur;
    @Autowired
    private CSVHandler csv;

    @GetMapping("/list")
    public String listPage(Model model,
                           @RequestParam(required = false) Map<String, String> query,
                           Authentication authentication, HttpServletRequest request) {
        var user = (CustomUserPrincipal) authentication.getPrincipal();
        var people = request.isUserInRole("ADMIN") ?
                pr.findAll() :
                //because of lazy initialization
                ur.findByUsername(user.getUsername()).get(0).getPeople();
        people.sort(Comparator.comparingLong(Person::getId));
        if (query != null) {
            final var key = query.getOrDefault("search-key", null);
            final var value = query.getOrDefault("search-value", null);
            if (key != null && value != null && !value.equals("")) {
                CollectionUtils.filter(people, person -> {
                    try {
                        if (key.equals("creditCardNumber"))
                            return ((Person) person).getValueFromPropertyName(key).matches(value);
                        if (key.equals("gender") && value.equals("male"))
                            return ((Person)person).getGender().equals("Male");
                        Pattern pattern = Pattern.compile(value);
                        Matcher matcher = pattern.matcher(((Person) person).getValueFromPropertyName(key));
                        return matcher.find();
                    } catch (IllegalStateException e) {
                        return true;
                    }
                });
            }
        }
        model.addAttribute("people", people);
        return "list";
    }

    @PostMapping("/export")
    public ResponseEntity<FileSystemResource> exportPeople(Authentication authentication, HttpServletRequest request) {
        var user = (CustomUserPrincipal) authentication.getPrincipal();
        var people = request.isUserInRole("ADMIN") ?
                pr.findAll() :
                ur.findByUsername(user.getUsername()).get(0).getPeople();
        try {
            var file = csv.exportPeople(people);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"people.csv\"")
                    .contentType(new MediaType("text", "csv"))
                    .contentLength(file.length())
                    .body(new FileSystemResource(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
