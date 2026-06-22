package br.edu.ifg.med_clinica_api.domain.entity;

import br.edu.ifg.med_clinica_api.domain.dto.user.UserDTO;
import br.edu.ifg.med_clinica_api.domain.enums.AuthProvider;
import br.edu.ifg.med_clinica_api.domain.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Table(name = "users")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {

    private static final long serialVersionUID = 5495657854955008689L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column
    private AuthProvider provider = AuthProvider.LOCAL;

    @Column(unique = true)
    private String providerId;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private Boolean active = true;

    public User(UserDTO data) {
        this.email = data.email();
        this.password = data.password();
        this.provider = AuthProvider.LOCAL;
    }

    @PrePersist
    private void prePersist() {
        if (this.provider == null) {
            this.provider = AuthProvider.LOCAL;
        }
        if (this.active == null) {
            this.active = true;
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority((role.name())));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return !Boolean.FALSE.equals(this.active);
    }
}
