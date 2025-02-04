package com.xpto.distancelearning.authuser.configs.security;

import com.xpto.distancelearning.authuser.configs.security.jwt.AuthenticationJwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true) // Setting global config of the authentication manager. This is used to enable the @PreAuthorize annotation.
@EnableWebSecurity
public class WebSecurityConfig { // henrique: Maybe the class name should be SecurityConfig

    // I do not need to set it here as the UserDetailsServiceImpl class is already setting it. The UserDetailsServiceImpl class is automatically called by the Spring Security framework.
//    @Autowired
//    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthenticationEntryPointImpl authenticationEntryPoint;

    private static final String[] AUTH_WHITELIST = {
            // Allow users to access the /auth endpoint/resource (without the need of authentication), which is located on AuthenticationController > @RequestMapping("/auth")
            "/auth/**"
    };

//    This method was using basic authentication. I changed it to JWT authentication (see below).
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                // The authenticationEntryPoint is used to return a 401 Unauthorized error to the client when an exception is thrown
//                // due to an unauthenticated user trying to access a resource that requires authentication.
//                .httpBasic(httpBasic -> httpBasic.authenticationEntryPoint(authenticationEntryPoint))
//                //.httpBasic(Customizer.withDefaults())
//                .authorizeHttpRequests(authorize -> authorize
//                        // Allow users to access the AUTH_WHITELIST endpoints/resources without the need of authentication.
//                        .requestMatchers(AUTH_WHITELIST).permitAll()
//
//                        // Allow users to access the /users endpoint/resource, which is located on UserController > @RequestMapping("/users"). This is only allowed for users with the role "STUDENT".
//                        // The String "STUDENT" is what I have on DB (tb_roles) but without the "ROLE_" prefix.
//                        // If User not "STUDENT", then the response will be a 403 Forbidde, which means the user was able to authenticate, but they do not have the correct permissions to access the resource.
//                        // Instead of using this method, I can use the @PreAuthorize annotation on the UserController class.
//                        .requestMatchers(HttpMethod.GET, "/users/**").hasRole("STUDENT")
//                        .anyRequest().authenticated()
//                )
//                .csrf(csrf -> csrf.disable());
//
//        return http.build();
//    }

    // This is the new implementation using JWT authentication.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(authenticationEntryPoint))
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // Allow users to access the AUTH_WHITELIST endpoints/resources without the need of authentication.
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable());

        http.addFilterBefore(authenticationJwtFilter(), UsernamePasswordAuthenticationFilter.class);
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

    /**
     * This is used to set the role hierarchy and set automatically by the Spring Security framework.
     */
    @Bean
    public RoleHierarchy roleHierarchy() {
        // This is the deprecated way to set the role hierarchy.
//        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
//        String hierarchy = "ROLE_ADMIN > ROLE_INSTRUCTOR \n ROLE_INSTRUCTOR > ROLE_STUDENT \n ROLE_STUDENT > ROLE_USER";
//        roleHierarchy.setHierarchy(hierarchy);
//        return roleHierarchy;

        // This is the new way to set the role hierarchy. See https://docs.spring.io/spring-security/reference/servlet/authorization/architecture.html#authz-hierarchical-roles
        return RoleHierarchyImpl.withDefaultRolePrefix()
                .role("ADMIN").implies("INSTRUCTOR")
                .role("INSTRUCTOR").implies("STUDENT")
                .role("STUDENT").implies("USER")
                .build();
    }

    // This is used on the AuthenticationController class to encrypt the password before saving it.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ====================================================================================================================
    // This is my implementation of the authenticationManagerBean method. I am not extending the WebSecurityConfigurerAdapter class.
    @Bean
    public AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class).build();
    }
//    // The method below is the implementation suggested by Michelli Brito, during the course.
//    @Bean
//    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration auth) throws Exception {
//        return auth.getAuthenticationManager();
//    }
    // ====================================================================================================================

    @Bean
    public AuthenticationJwtFilter authenticationJwtFilter() {
        return new AuthenticationJwtFilter();
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
//    @Override
//    @Bean
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return super.authenticationManagerBean();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}
