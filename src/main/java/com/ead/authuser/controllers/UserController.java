package com.ead.authuser.controllers;

import com.ead.authuser.dtos.UserDto;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.service.UserService;
import com.ead.authuser.specifications.SpecificationTemplate;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController // The Spring context will manage this class
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/users") // It is a good practice to have resources on plural.
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Using pagination on this method with Specification (<a href="https://mvnrepository.com/artifact/net.kaczmarzyk/specification-arg-resolver/2.7.0">...</a>)
     *
     * Starts from page 0, shows 10 items per page, and sorts userId attribute (ASC)
     * Also, because I added Specification, I can filter using other fields specified on SpecificationTemplate.UserSpec.
     */
    @GetMapping
    public ResponseEntity<Page<UserModel>> getAllUsers(SpecificationTemplate.UserSpec spec, @PageableDefault(page = 0, size = 10, sort = "userId", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<UserModel> userModelPage = userService.findAll(spec, pageable);
        if(!userModelPage.isEmpty()) {
            for (UserModel user : userModelPage.toList()) {
                user.add(linkTo(methodOn(UserController.class).getOneUser(user.getUserId())).withSelfRel());
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(userModelPage);
        //return ResponseEntity.status(HttpStatus.OK).body(userService.findAll()); // Before using Pagination
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getOneUser(@PathVariable(value = "userId") UUID userId) {
        Optional<UserModel> userModelOptional = userService.findById(userId);
        return userModelOptional.isEmpty() ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found") : ResponseEntity.status(HttpStatus.OK).body(userModelOptional.get());
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable(value = "userId") UUID userId) {
        Optional<UserModel> userModelOptional = userService.findById(userId);
        if(userModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        else {
            userService.delete(userModelOptional.get());
            return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully"); // Probably it should be 204 = NO_CONTENT, as there is no content to be returned.
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable(value = "userId") UUID userId,
                                             @RequestBody
                                             @Validated(UserDto.UserView.UserPut.class) // Call the Validation annotations on UserDto
                                             @JsonView(UserDto.UserView.UserPut.class) UserDto userDto) {
        Optional<UserModel> userModelOptional = userService.findById(userId);
        if(userModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        else {
            // I could create a helper method to create this object, or set it on the Service layer.
            var userModel = userModelOptional.get();
            userModel.setFullName(userDto.getFullName());
            userModel.setPhoneNumber(userDto.getPhoneNumber());
            userModel.setCpf(userDto.getCpf());
            userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC"))); // Maybe I should have this configured on the database, to update this field once anything is updated on the database.
//                                                                                      It would be something like this: CREATE TABLE foo (
//                                                                                                                          id INT PRIMARY KEY
//                                                                                                                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, KEY (updated_at)
//                                                                                                                        );
            userService.save(userModel); // Maybe I could return the object, not void.
            return ResponseEntity.status(HttpStatus.OK).body(userModel);
        }
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<Object> updatePassword(@PathVariable(value = "userId") UUID userId,
                                                 @RequestBody
                                                 @Validated(UserDto.UserView.PasswordPut.class) // Call the Validation annotations on UserDto
                                                 @JsonView(UserDto.UserView.PasswordPut.class) UserDto userDto) {
        Optional<UserModel> userModelOptional = userService.findById(userId);
        if(userModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        if(!userModelOptional.get().getPassword().equals(userDto.getOldPassword())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Mismatched old password!");
        }
        else {
            // I could create a helper method to create this object, or set it on the Service layer.
            var userModel = userModelOptional.get();
            userModel.setPassword(userDto.getPassword());
            userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC"))); // Maybe I should have this configured on the database, to update this field once anything is updated on the database.
//                                                                                      It would be something like this: CREATE TABLE foo (
//                                                                                                                          id INT PRIMARY KEY
//                                                                                                                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, KEY (updated_at)
//                                                                                                                        );
            userService.save(userModel); // Maybe I could return the object, not void.
            return ResponseEntity.status(HttpStatus.OK).body("Password updated successfully");
        }
    }

    @PutMapping("/{userId}/image")
    public ResponseEntity<Object> updateImage(@PathVariable(value = "userId") UUID userId,
                                              @RequestBody
                                              @Validated(UserDto.UserView.ImagePut.class) // Call the Validation annotations on UserDto
                                              @JsonView(UserDto.UserView.ImagePut.class) UserDto userDto) {
        Optional<UserModel> userModelOptional = userService.findById(userId);
        if(userModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        else {
            // I could create a helper method to create this object, or set it on the Service layer.
            var userModel = userModelOptional.get();
            userModel.setImageUrl(userDto.getImageUrl());
            userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC"))); // Maybe I should have this configured on the database, to update this field once anything is updated on the database.
//                                                                                      It would be something like this: CREATE TABLE foo (
//                                                                                                                          id INT PRIMARY KEY
//                                                                                                                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, KEY (updated_at)
//                                                                                                                        );
            userService.save(userModel); // Maybe I could return the object, not void.
            return ResponseEntity.status(HttpStatus.OK).body(userModel);
        }
    }

}
