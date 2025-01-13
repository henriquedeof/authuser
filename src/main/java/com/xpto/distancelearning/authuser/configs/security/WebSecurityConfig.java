package com.xpto.distancelearning.authuser.configs.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class WebSecurityConfig {

    // I do not need to set it here as the UserDetailsServiceImpl class is already setting it. The UserDetailsServiceImpl class is automatically called by the Spring Security framework.
//    @Autowired
//    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthenticationEntryPointImpl authenticationEntryPoint;

    private static final String[] AUTH_WHITELIST = {
            // Allow users to access the /auth endpoint/resource, which is located on AuthenticationController > @RequestMapping("/auth")
            "/auth/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // The authenticationEntryPoint is used to return a 401 Unauthorized error to the client when an exception is thrown
                // due to an unauthenticated user trying to access a resource that requires authentication.
                .httpBasic(httpBasic -> httpBasic.authenticationEntryPoint(authenticationEntryPoint))
                //.httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize
                        // Allow users to access the AUTH_WHITELIST endpoints/resources
                        .requestMatchers(AUTH_WHITELIST).permitAll()

                        // Allow users to access the /users endpoint/resource, which is located on UserController > @RequestMapping("/users"). This is only allowed for users with the role "STUDENT".
                        // The String "STUDENT" is what I have on DB (tb_roles) but without the "ROLE_" prefix.
                        // If User not "STUDENT", then the response will be a 403 Forbidde, which means the user was able to authenticate, but they do not have the correct permissions to access the resource.
                        .requestMatchers(HttpMethod.GET, "/users/**").hasRole("STUDENT")
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

//    This method is using the InMemoryUserDetailsManager class to create a user with the username "admin" and password "123456.
//    However, this is not the best way to create a user. The best way is to use the UserDetailsServiceImpl class to create a user.
//    @Bean
//    public InMemoryUserDetailsManager userDetailsService() {
//        UserDetails user = User.withUsername("admin")
//                .password(passwordEncoder().encode("123456"))
//                .roles("ADMIN")
//                .build();
//        return new InMemoryUserDetailsManager(user);
//    }

    // This is used on the AuthenticationController class to encrypt the password before saving it.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


// ====================================================================================================================
// The code below is deprecated and should not be used. It is kept here for reference only.
// ====================================================================================================================
//@Configuration
//@EnableGlobalMethodSecurity(prePostEnabled=true) // Setting global config of the authentication manager
//@EnableWebSecurity // Turn off the default security configuration ??
//public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
//
//    @Autowired
//    private UserDetailsServiceImpl userDetailsService;
//
//    @Autowired
//    private AuthenticationEntryPointImpl authenticationEntryPoint;
//
//    private static final String[] AUTH_WHITELIST = {
//            // Allow users to access the /auth endpoint/resource, which is located on AuthenticationController > @RequestMapping("/auth")
//            "/dl-authuser/auth/**"
//            //"/auth/**"
//    };
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .httpBasic()
//                .authenticationEntryPoint(authenticationEntryPoint)
//                .and()
//                .authorizeRequests()
//                .antMatchers(AUTH_WHITELIST).permitAll()
//                .antMatchers(HttpMethod.GET, "/users/**").hasRole("ADMIN")
//                .anyRequest().authenticated()
//                .and()
//                .csrf().disable();
//    }
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userDetailsService)
//                .passwordEncoder(passwordEncoder());
//    }
//
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}
