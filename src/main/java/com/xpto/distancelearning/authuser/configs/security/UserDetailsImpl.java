package com.xpto.distancelearning.authuser.configs.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xpto.distancelearning.authuser.models.UserModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails { // This UserDetails interface is used by the Spring Security for the authentication process.

    // ================================== Improvements I could do ==================================
    // henrique: Instead of using the UserDetailsImpl.build method, I could use the UserDetailsImpl constructor, passing userModel as argument.
    //  private final UserModel user;
    //  public UserDetailsImpl(UserModel user) {
    //      this.user = user;
    //  }
    //
    //    @Override
    //    public Collection<? extends GrantedAuthority> getAuthorities() {
    //        return user.getRoles().stream()
    //                .map(role -> new SimpleGrantedAuthority(role.getName()))
    //                .collect(Collectors.toList());
    //    }
    // ===============================================================================================

    private UUID userId;
    private String fullName;
    private String username;
    @JsonIgnore
    private String password;
    private String email;
    private Collection<? extends GrantedAuthority> authorities; // This line links to the RoleModel class as it implements the GrantedAuthority interface.

    // This is called by the UserDetailsServiceImpl class for basic authentication. This method is used for the authentication process.
    // THis method is also called by the JwtProvider class for the generation of the JWT token.
    public static UserDetailsImpl build(UserModel userModel) {
        List<GrantedAuthority> authorities = userModel.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                userModel.getUserId(),
                userModel.getFullName(),
                userModel.getUsername(),
                userModel.getPassword(),
                userModel.getEmail(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
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
        // If the user is active, then the user is enabled. In this example, the userModel does not have a field to check if it is enabled.
        // In this case, I am considering that the user always is enabled.
        return true;
    }
}