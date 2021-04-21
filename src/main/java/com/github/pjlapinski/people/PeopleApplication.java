package com.github.pjlapinski.people;

import com.github.pjlapinski.people.models.User;
import com.github.pjlapinski.people.util.CSVHandler;
import com.github.pjlapinski.people.util.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.IOException;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"com.github.pjlapinski.people.util"})
@EnableTransactionManagement
@EntityScan(basePackages = {"com.github.pjlapinski.people.models"})
public class PeopleApplication {

	@Autowired
	private CSVHandler handler;

	public static void main(String[] args) {SpringApplication.run(PeopleApplication.class, args);}

	@Bean
	public CommandLineRunner init(UserRepository ur, PasswordEncoder passwordEncoder) {
		return args -> {
			if (ur.findAdmin().isEmpty()) {
				ur.deleteAll();
				ur.save(new User(
						"admin",
						"admin@admin.admin",
						passwordEncoder.encode("admin"),
						true,
						"ROLE_ADMIN"));
				try {
					handler.importPeople("PersonOne.csv");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
	}
}
