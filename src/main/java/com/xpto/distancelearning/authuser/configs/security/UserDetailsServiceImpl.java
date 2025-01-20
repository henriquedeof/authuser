package com.xpto.distancelearning.authuser.configs.security;

import com.xpto.distancelearning.authuser.models.UserModel;
import com.xpto.distancelearning.authuser.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * henrique
     * This method is automatically called by the Spring Security because the UserDetailsServiceImpl class implements the UserDetailsService interface.
     * The UserDetailsService interface is used to retrieve user-related data and is a part of the Spring Security core.
     * This method is called by the authentication process.
     * It receives the username and returns the UserDetailsImpl object.
     *
     * @param username The username of the user.
     * @return The UserDetailsImpl object.
     * @throws UsernameNotFoundException If the user is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel userModel = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        // Instead of using the UserDetailsImpl.build method, I could use the UserDetailsImpl constructor, passing userModel as argument.
        return UserDetailsImpl.build(userModel);
    }

    /**
     * henrique
     * This method is automatically called by the Spring Security because the UserDetailsServiceImpl class implements the UserDetailsService interface.
     * The UserDetailsService interface is used to retrieve user-related data and is a part of the Spring Security core.
     * This method is called by the authentication process.
     * It receives the username and returns the UserDetailsImpl object.
     */
    public UserDetails loadUserById(UUID userId) throws AuthenticationCredentialsNotFoundException {
        UserModel userModel = userRepository.findById(userId)
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("User Not Found with userId: " + userId));

        // Instead of using the UserDetailsImpl.build method, I could use the UserDetailsImpl constructor, passing userModel as argument.
        return UserDetailsImpl.build(userModel);
    }
}