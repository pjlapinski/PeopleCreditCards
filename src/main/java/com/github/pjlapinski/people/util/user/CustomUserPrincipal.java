package com.github.pjlapinski.people.util.user;

import com.github.pjlapinski.people.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class CustomUserPrincipal implements UserDetails {

    private User user;

    public CustomUserPrincipal(User user) {this.user = user;}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        var list = new ArrayList<GrantedAuthority>();
        list.add(new SimpleGrantedAuthority(user.getRole()));
        return list;
    }

    @Override
    public String getPassword() {return user.getPassword();}

    @Override
    public String getUsername() {return user.getUsername();}

    public String getEmail() {return user.getEmail();}

    public User getModel() {return user;}

    @Override
    public boolean isAccountNonExpired() {return true;}

    @Override
    public boolean isAccountNonLocked() {return true;}

    @Override
    public boolean isCredentialsNonExpired() {return true;}

    @Override
    public boolean isEnabled() {return user.isActive();}
}
