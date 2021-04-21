package com.github.pjlapinski.people.util;

import com.github.pjlapinski.people.models.Person;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Statistics {
    // amount of people of gender
    private Map<String, Integer> genderStats;
    // amount of people with a card of a type
    private Map<String, Integer> creditCardTypeStats;

    public Statistics(List<Person> data) {
        genderStats = new HashMap<>();
        creditCardTypeStats = new HashMap<>();

        for (var person : data) {
            var gender = person.getGender();
            var amount = genderStats.getOrDefault(gender, 0);
            genderStats.put(gender, ++amount);

            var type = person.getCreditCardType();
            amount = creditCardTypeStats.getOrDefault(type, 0);
            creditCardTypeStats.put(type, ++amount);
        }
    }

    public List<Map.Entry<String, Integer>> getGenderStatsSorted() {
        return genderStats.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toList());
    }

    public List<Map.Entry<String, Integer>> getCreditCardTypeStatsSorted() {
        return creditCardTypeStats.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toList());
    }
}
