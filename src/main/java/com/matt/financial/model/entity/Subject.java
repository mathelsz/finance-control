package com.matt.financial.model.entity;

import com.matt.financial.config.security.entity.Authority;
import com.matt.financial.config.security.entity.GroupAuthority;
import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

import static java.util.Optional.ofNullable;

@Entity
@Table(name = "subject")
@Data
@NoArgsConstructor
public class Subject implements UserDetails, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "UUID_V1")
    private UUID id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "active", nullable = false)
    private Boolean active = Boolean.TRUE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_authority_id")
    private GroupAuthority groupAuthority;

    public Subject(Subject subject, String password) {
        this.name = subject.getName();
        this.username = subject.getUsername();
        this.email = subject.getEmail();
        this.phone = subject.getPhone();
        this.password = password;
        this.active = true;
        this.groupAuthority = subject.getGroupAuthority();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.groupAuthority.getAuthorities().stream()
                .map(Authority::getName)
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

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

    public void mergeForUpdate(Subject subject) {
        this.name = ofNullable(subject.getName()).orElse(this.name);
        this.username = ofNullable(subject.getUsername()).orElse(this.username);
        this.email = ofNullable(subject.getEmail()).orElse(this.email);
        this.phone = ofNullable(subject.getPhone()).orElse(this.phone);
        this.groupAuthority = ofNullable(subject.getGroupAuthority()).orElse(this.groupAuthority);
    }
}
