package com.github.pjlapinski.people.util;

import com.github.pjlapinski.people.models.User;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByUsername(String username);
    List<User> findByEmail(String email);

    default Optional<User> findAdmin() {
        var f = findByUsername("admin");
        return f.size() == 0 ? Optional.empty() : Optional.of(f.get(0));
    }
}
