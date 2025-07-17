package com.aet.studyassistant.auth_service.security;

import com.aet.studyassistant.auth_service.model.User; // Import your User model
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID; // Import UUID

public class UserDetailsImpl implements UserDetails {

    private UUID id; // This will store the user's UUID
    private String email;
    private String passwordHash;
    private Collection<? extends GrantedAuthority> authorities;

    // Constructor that takes your User model
    public UserDetailsImpl(User user) {
        this.id = user.getUuid(); // Get the UUID from your User model
        this.email = user.getEmail();
        this.passwordHash = user.getPasswordHash();
        this.authorities = Collections.emptyList(); // Default to no roles for simplicity
    }

    // --- NEW: Getter for the User's UUID ---
    public UUID getId() {
        return id;
    }

    // --- NEW: Getter for the User's Email (can be used instead of getUsername()
    // sometimes) ---
    public String getEmail() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email; // Spring Security uses this for the principal's name
    }

    // All these methods return true for simplicity unless you have specific logic
    // for them
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // Optional: Override equals() and hashCode() if you plan to store
    // UserDetailsImpl in collections
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl that = (UserDetailsImpl) o;
        return id.equals(that.id); // Equality based on UUID
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}