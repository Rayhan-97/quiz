package com.quiz.configuration;

import com.quiz.core.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class DefaultPrincipal implements UserDetails
{
    private final User user;
    private final org.springframework.security.core.userdetails.User springUser;

    private DefaultPrincipal(User user, org.springframework.security.core.userdetails.User springUser)
    {
        this.user = user;
        this.springUser = springUser;
    }

    public static DefaultPrincipal from(User user)
    {
        return new DefaultPrincipal(user, new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                Collections.emptyList()));
    }

    @Override
    public String getUsername()
    {
        return user.getEmail();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return springUser.getAuthorities();
    }

    @Override
    public String getPassword()
    {
        return springUser.getPassword();
    }

    @Override
    public boolean isAccountNonExpired()
    {
        return springUser.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked()
    {
        return springUser.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired()
    {
        return springUser.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled()
    {
        return springUser.isEnabled();
    }
}
