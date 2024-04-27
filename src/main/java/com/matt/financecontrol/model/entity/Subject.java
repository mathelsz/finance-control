package com.matt.financecontrol.model.entity;

import com.matt.financecontrol.application.records.SubjectRecord;
import com.matt.financecontrol.config.security.entity.Authority;
import com.matt.financecontrol.config.security.entity.GroupAuthority;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

import static java.util.Optional.ofNullable;

@Entity
@Table(name = "subject")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
public class Subject implements UserDetails,Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "UUID_V1")
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "password")
    private String password;

    @Column(name = "active")
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_authority_id")
    private GroupAuthority groupAuthority;

    public Subject(SubjectRecord subjectRecord, String password) {
        this.name = subjectRecord.name();
        this.username = subjectRecord.username();
        this.email = subjectRecord.email();
        this.phone = subjectRecord.phone();
        this.password = password;
        this.active = true;
        this.groupAuthority = subjectRecord.groupAuthority();
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
        this.phone = ofNullable(subject.getPhone()).orElse(this.phone);
        this.groupAuthority = ofNullable(subject.getGroupAuthority()).orElse(this.groupAuthority);
    }
}
