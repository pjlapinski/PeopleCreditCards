package com.github.pjlapinski.people.util;

import com.github.pjlapinski.people.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface PersonRepository extends JpaRepository<Person, Long> {}
