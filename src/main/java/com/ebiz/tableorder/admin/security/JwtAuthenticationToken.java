package com.ebiz.tableorder.admin.security;


import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken extends UsernamePasswordAuthenticationToken {
    public JwtAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> auths) {
        super(principal, null, auths);
    }
}