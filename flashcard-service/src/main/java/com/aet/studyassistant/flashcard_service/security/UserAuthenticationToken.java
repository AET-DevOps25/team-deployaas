package com.aet.studyassistant.flashcard_service.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

public class UserAuthenticationToken extends UsernamePasswordAuthenticationToken {
    
    private final UUID userId;

    public UserAuthenticationToken(Object principal, Object credentials, 
                                 Collection<? extends GrantedAuthority> authorities, UUID userId) {
        super(principal, credentials, authorities);
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }
}
