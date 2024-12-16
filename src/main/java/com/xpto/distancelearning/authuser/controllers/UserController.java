package com.xpto.distancelearning.authuser.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.xpto.distancelearning.authuser.dtos.UserDto;
import com.xpto.distancelearning.authuser.models.UserModel;
import com.xpto.distancelearning.authuser.services.UserService;
import com.xpto.distancelearning.authuser.specifications.SpecificationTemplate;
import lombok.extern.log4j.Log4j2;
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
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * This method is used to get all users.
     * It is also used to filter the data using Specification, and Pagination.
     *
     * @param spec
     * @param pageable
     * @return
     */
    @GetMapping
    public ResponseEntity<Page<UserModel>> getAllUsers(SpecificationTemplate.UserSpec spec,
                                                       @PageableDefault(page = 0, size = 10, sort = "userId", direction = Sort.Direction.ASC) Pageable pageable,
                                                       @RequestParam(required = false) UUID courseId) {

//        Page<UserModel> userModelPage = userService.findAll(spec, pageable);
//        if (!userModelPage.isEmpty()) {
//            for (UserModel user : userModelPage.toList()) {
//                // Setting HATEOAS
//                user.add(linkTo(methodOn(UserController.class).getOneUser(user.getUserId())).withSelfRel());
//            }
//        }
//        return ResponseEntity.status(HttpStatus.OK).body(userModelPage);

        Page<UserModel> userModelPage = null;
        if(courseId != null){
            userModelPage = userService.findAll(SpecificationTemplate.userCourseId(courseId).and(spec), pageable);
        } else {
            userModelPage = userService.findAll(spec, pageable);
        }
        if(!userModelPage.isEmpty()){
            for(UserModel user : userModelPage.toList()){
                // Setting HATEOAS
                user.add(linkTo(methodOn(UserController.class).getOneUser(user.getUserId())).withSelfRel());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(userModelPage);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getOneUser(@PathVariable(value = "userId") UUID userId) {
        Optional<UserModel> userModel = userService.findById(userId);
        return userModel.<ResponseEntity<Object>>map(model -> ResponseEntity.status(HttpStatus.OK).body(model)).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"));
//        if (userModel.isPresent()) {
//            return ResponseEntity.status(HttpStatus.OK).body(userModel.get());
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
//        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable(value = "userId") UUID userId) {
        log.debug("DELETE deleteUser userId received {}: ", userId);
        Optional<UserModel> userModel = userService.findById(userId);
        if (userModel.isPresent()) {
            userService.delete(userModel.get());
            log.debug("DELETE deleteUser userId saved {}: ", userId);
            log.info("User deleted successfully. UserId {}: ", userId);
            return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    // Because I am using Validation Groups (or 'groups'), I need to use the annotation @Validated (NOT @Valid)
    @PutMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable(value = "userId") UUID userId,
                                             @RequestBody @Validated(UserDto.UserView.UserPut.class)
                                             @JsonView(UserDto.UserView.UserPut.class) UserDto userDto){

        log.debug("PUT updateUser userDto received {}: ", userDto.toString());
        Optional<UserModel> userModelOptional = userService.findById(userId);
        if(!userModelOptional.isPresent()){
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        } else{
            var userModel = userModelOptional.get();
            userModel.setFullName(userDto.getFullName());
            userModel.setPhoneNumber(userDto.getPhoneNumber());
            userModel.setCpf(userDto.getCpf());
            userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
            userService.save(userModel);

            log.debug("PUT updateUser userModel userId {}: ", userModel.getUserId());
            log.info("User updated successfully. UserId {}: ", userModel.getUserId());
            return ResponseEntity.status(HttpStatus.OK).body(userModel);
        }
    }

    // Because I am using Validation Groups (or 'groups'), I need to use the annotation @Validated (NOT @Valid)
    @PutMapping("/{userId}/password")
    public ResponseEntity<Object> updatePassword(@PathVariable(value = "userId") UUID userId,
                                                 @RequestBody @Validated(UserDto.UserView.PasswordPut.class)
                                                 @JsonView(UserDto.UserView.PasswordPut.class) UserDto userDto){
        Optional<UserModel> userModelOptional = userService.findById(userId);
        if (!userModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        } if (!userModelOptional.get().getPassword().equals(userDto.getOldPassword())) {
            log.warn("Mismatched old password for userId {}: ", userId);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Mismatched old password!");
        } else {
            var userModel = userModelOptional.get();
            userModel.setPassword(userDto.getPassword());
            userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
            userService.save(userModel);
            log.debug("PUT updatePassword userModel userId {}: ", userModel.getUserId());
            log.info("Password updated successfully. UserId {}: ", userModel.getUserId());
            return ResponseEntity.status(HttpStatus.OK).body("Password updated successfully.");
        }
    }

    // Because I am using Validation Groups (or 'groups'), I need to use the annotation @Validated (NOT @Valid)
    @PutMapping("/{userId}/image")
    public ResponseEntity<Object> updateImage(@PathVariable(value = "userId") UUID userId,
                                              @RequestBody @Validated(UserDto.UserView.ImagePut.class)
                                              @JsonView(UserDto.UserView.ImagePut.class) UserDto userDto){
        Optional<UserModel> userModelOptional = userService.findById(userId);
        if(!userModelOptional.isPresent()) {
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        } else {
            var userModel = userModelOptional.get();
            userModel.setImageUrl(userDto.getImageUrl());
            userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
            userService.save(userModel);
            return ResponseEntity.status(HttpStatus.OK).body(userModel);
        }
    }
}
