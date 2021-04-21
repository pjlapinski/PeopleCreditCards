package com.github.pjlapinski.people.controllers;

import com.github.pjlapinski.people.util.PersonRepository;
import com.github.pjlapinski.people.util.Statistics;
import com.github.pjlapinski.people.util.UserRepository;
import com.github.pjlapinski.people.util.user.CustomUserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class StatisticsController {

    @Autowired
    private PersonRepository pr;
    @Autowired
    private UserRepository ur;

    @GetMapping("/stats")
    public String displayStatistics(Model model, Authentication authentication, HttpServletRequest request) {
        var user = (CustomUserPrincipal) authentication.getPrincipal();
        var stats = new Statistics(request.isUserInRole("ADMIN") ?
                pr.findAll() :
                ur.findByUsername(user.getUsername()).get(0).getPeople());
        model.addAttribute("stats", stats);
        return "stats";
    }
}
