package com.amigos.authenautho.demo.entities.user;

import com.amigos.authenautho.demo.entities.token.Token;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TAI_KHOAN")
public class User implements UserDetails {
    @Id
    @GeneratedValue
    @Column(name = "TK_ID")
    private  Integer id;

    @Column(name = "TK_EMAIL", unique = true)
    private String email;

    @Column(name = "TK_MATKHAU")
    private String password;

    @Column(name="KH_ID")
    private int khId;

    @Enumerated(EnumType.STRING)
    @Column(name = "TK_ROLE")
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Token> tokens;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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
}
