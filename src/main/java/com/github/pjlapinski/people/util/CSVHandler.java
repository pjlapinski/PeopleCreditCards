package com.github.pjlapinski.people.util;

import com.github.pjlapinski.people.models.Person;
import com.github.pjlapinski.people.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Service
public class CSVHandler {

    @Autowired
    private PersonRepository pr;

    @Autowired
    private UserRepository ur;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void importPeople(String filename) throws IOException {
        var admin = ur.findAdmin().get();
        var file = new ClassPathResource(filename).getInputStream();
        try (var scanner = new Scanner(file)) {
            scanner.nextLine(); // skip header
            var people = new ArrayList<Person>();
            while (scanner.hasNext()) {
                var line = scanner.nextLine();
                var values = line.split(",");
                var person = new Person();
                person.setId(Long.parseLong(values[0]));
                person.setFirstName(values[1]);
                person.setLastName(values[2]);
                person.setEmail(values[3]);
                person.setGender(values[4]);
                person.setCreditCardType(values[5]);
                person.setCreditCardNumber(values[6]);
                person.setCreatedBy(admin);
                people.add(person);
            }
            pr.saveAll(people);
        }
    }

    public File exportPeople(List<Person> people) throws IOException {
        var file = new File("people.csv");
        try (var writer = new FileWriter(file)) {
            writer.write("id,first_name,last_name,email,gender,credit card type,credit card number");
            writer.write(System.lineSeparator());
            for (var person : people) {
                writer.write(String.valueOf(person.getId()));
                writer.write(",");
                writer.write(person.getFirstName());
                writer.write(",");
                writer.write(person.getLastName());
                writer.write(",");
                writer.write(person.getEmail());
                writer.write(",");
                writer.write(person.getGender());
                writer.write(",");
                writer.write(person.getCreditCardType());
                writer.write(",");
                writer.write(person.getCreditCardNumber());
                writer.write(System.lineSeparator());
            }
        }
        return file;
    }

    public File exportPeople(User user) throws IOException {
        var file = new File("people.csv");
        try (var writer = new FileWriter(file)) {
            writer.write("id,first_name,last_name,email,gender,credit card type,credit card number");
            writer.write(System.lineSeparator());
            var people = user.getPeople();
            for (var person : people) {
                writer.write(String.valueOf(person.getId()));
                writer.write(",");
                writer.write(person.getFirstName());
                writer.write(",");
                writer.write(person.getLastName());
                writer.write(",");
                writer.write(person.getEmail());
                writer.write(",");
                writer.write(person.getGender());
                writer.write(",");
                writer.write(person.getCreditCardType());
                writer.write(",");
                writer.write(person.getCreditCardNumber());
                writer.write(System.lineSeparator());
            }
        }
        return file;
    }
}
