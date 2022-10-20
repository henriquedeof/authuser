package com.ead.authuser.controllers;

import com.ead.authuser.dtos.UserDto;
import com.ead.authuser.enums.UserStatus;
import com.ead.authuser.enums.UserType;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.service.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController // The Spring context will manage this class
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    UserService userService;

    /**
     * The JsonView on this method, take into consideration just the fields of 'UserDto' that are annotated with RegistrationPost.
     * If I add extra or fewer fields, and they are not annotated with RegistrationPost, then they will be ignored.
     * NOTE: Very likely, is better to use specific DTOs for each situation.
     *
     * @param userDto
     * @return
     */
    @PostMapping("/signup")
    public ResponseEntity<Object> registerUser(@RequestBody
                                               @Validated(UserDto.UserView.RegistrationPost.class) // Call the Validation annotations on UserDto
                                               @JsonView(UserDto.UserView.RegistrationPost.class) UserDto userDto
    ) {
        var userModel = new UserModel();

        if(userService.existsByUsername(userDto.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Username is already taken");
        }
        if(userService.existsByEmail(userDto.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Email is already taken");
        }

        // ========= Maybe I should have done it on the Service class ========
        BeanUtils.copyProperties(userDto, userModel);
        userModel.setUserStatus(UserStatus.ACTIVE);
        userModel.setUserType(UserType.STUDENT);
        userModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC"))); // Maybe I should have this configured on the database, to update this field once anything is updated on the database.
//                                                                                      It would be something like this: CREATE TABLE foo (
//                                                                                                                          id INT PRIMARY KEY
//                                                                                                                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, KEY (updated_at)
//                                                                                                                        );
        // ====================================================================

        userService.save(userModel); // Maybe I could return the object, not void.
        return ResponseEntity.status(HttpStatus.CREATED).body(userModel);
    }

}
