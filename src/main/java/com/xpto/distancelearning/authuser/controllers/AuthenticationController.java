package com.xpto.distancelearning.authuser.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.xpto.distancelearning.authuser.dtos.UserDto;
import com.xpto.distancelearning.authuser.enums.RoleType;
import com.xpto.distancelearning.authuser.enums.UserStatus;
import com.xpto.distancelearning.authuser.enums.UserType;
import com.xpto.distancelearning.authuser.models.RoleModel;
import com.xpto.distancelearning.authuser.models.UserModel;
import com.xpto.distancelearning.authuser.services.RoleService;
import com.xpto.distancelearning.authuser.services.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/auth")
public class AuthenticationController {

    // Using log4j2 configured in the @Log4j2 Lombok annotation above
    // private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    // private static final Logger logger = LogManager.getLogger(AuthenticationController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Because I am using Validation Groups (or 'groups'), I need to use the annotation @Validated (NOT @Valid)
    @PostMapping("/signup")
    public ResponseEntity<Object> registerUser(@RequestBody @Validated(UserDto.UserView.RegistrationPost.class)
                                               @JsonView(UserDto.UserView.RegistrationPost.class) UserDto userDto) {

        log.debug("POST registerUser userDto received {}: ", userDto.toString());
        if (userService.existsByUsername(userDto.getUsername())) {
            log.warn("Username {} is already taken: ", userDto.getUsername());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Username is already taken!");
        }

        if (userService.existsByEmail(userDto.getEmail())) {
            log.warn("Email {} is already taken: ", userDto.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Email is already taken!");
        }

        RoleModel roleModel = roleService.findByRoleName(RoleType.ROLE_STUDENT)
                .orElseThrow(() -> new RuntimeException("Error: Role is Not Found."));

        // Encrypt the password before saving it
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));

        var userModel = new UserModel();
        BeanUtils.copyProperties(userDto, userModel);
        userModel.setUserStatus(UserStatus.ACTIVE);
        userModel.setUserType(UserType.STUDENT);
        userModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        userModel.getRoles().add(roleModel);

        //userService.save(userModel);
        userService.saveUser(userModel);
        log.debug("POST registerUser UserId saved {}: ", userModel.getUserId());
        log.info("User saved successfully. UserId {}: ", userModel.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(userModel);
    }

    @GetMapping("/") // Access the URL: http://localhost:8080/auth/
    public String myLoggingTest() {
        log.trace("A TRACE Message");
        log.debug("A DEBUG Message");
        log.warn("A WARN Message");
        log.info("An INFO Message");
        log.error("An ERROR Message");
        return "My logging test";
    }
}
