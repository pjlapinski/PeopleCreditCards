package com.github.pjlapinski.people.util.user;

import com.github.pjlapinski.people.util.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository ur;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = ur.findByUsername(username);
        if (user.size() == 0) throw new UsernameNotFoundException("No user with this username");
        var foundUser = user.get(0);
        return new CustomUserPrincipal(foundUser);
    }
}
