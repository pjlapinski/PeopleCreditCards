package com.github.pjlapinski.people.util.user;

import com.github.pjlapinski.people.models.User;
import com.github.pjlapinski.people.models.UserDTO;
import com.github.pjlapinski.people.util.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository ur;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User register(UserDTO userDTO) throws UserExistsException {
        var users = ur.findByEmail(userDTO.getEmail());
        if (users.size() != 0) throw new UserExistsException("User with such email already exists");
        users = ur.findByUsername(userDTO.getUsername());
        if (users.size() != 0) throw new UserExistsException("User with such username already exists");
        var user = new User(
                userDTO.getUsername(),
                userDTO.getEmail(),
                passwordEncoder.encode(userDTO.getPassword()));
        return ur.save(user);
    }
}
