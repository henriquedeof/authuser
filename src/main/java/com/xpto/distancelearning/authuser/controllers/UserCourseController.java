package com.xpto.distancelearning.authuser.controllers;

import com.xpto.distancelearning.authuser.clients.CourseClient;
import com.xpto.distancelearning.authuser.dtos.CourseDto;
import com.xpto.distancelearning.authuser.models.UserModel;
import com.xpto.distancelearning.authuser.services.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserCourseController {

    @Autowired
    private CourseClient courseClient;

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAnyRole('STUDENT')")
    @GetMapping("/users/{userId}/courses")
    public ResponseEntity<Object> getAllCoursesByUser(@PageableDefault(page = 0, size = 10, sort = "courseId", direction = Sort.Direction.ASC) Pageable pageable,
                                                               @PathVariable(value = "userId") UUID userId,
                                                               @RequestHeader("Authorization") String token) { // @RequestHeader is used to get the token from the header of the request.
        Optional<UserModel> userModelOptional = userService.findById(userId);
        if (userModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        // Call the Course Service SYNCHRONOUSLY, using RestTemplate.
        // I need to pass the token to the Course Service, so it can check if the user is authorized to access the courses. CourseClient is used to call the Course Service.
        Page<CourseDto> allCoursesByUser = courseClient.getAllCoursesByUser(userId, pageable, token);
        return ResponseEntity.status(HttpStatus.OK).body(allCoursesByUser);
    }


//===============================================================================================================
//    NOTE: Methods deleted as the communication is now asynchronous
//===============================================================================================================

//    @PostMapping("/users/{userId}/courses/subscription")
//    public ResponseEntity<Object> saveSubscriptionUserInCourse(@PathVariable(value = "userId") UUID userId,
//                                                               @RequestBody @Valid UserCourseDto userCourseDto){
//        Optional<UserModel> userModelOptional = userService.findById(userId);
//        if(!userModelOptional.isPresent()){
//            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
//        }
//        if(userCourseService.existsByUserAndCourseId(userModelOptional.get(), userCourseDto.getCourseId())){
//            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: subscription already exists!");
//        }
//        UserCourseModel userCourseModel = userCourseService.save(userModelOptional.get().convertToUserCourseModel(userCourseDto.getCourseId()));
//        return ResponseEntity.status(HttpStatus.CREATED).body(userCourseModel);
//    }
//
//    @DeleteMapping("/users/courses/{courseId}")
//    public ResponseEntity<Object> deleteUserCourseByCourse(@PathVariable(value = "courseId") UUID courseId){
//        if (!userCourseService.existsByCourseId(courseId)) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("UserCourse not found.");
//        }
//        userCourseService.deleteUserCourseByCourse(courseId);
//        return ResponseEntity.status(HttpStatus.OK).body("UserCourse deleted successfully.");
//    }
}
