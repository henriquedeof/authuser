package com.xpto.distancelearning.authuser.services;

import com.xpto.distancelearning.authuser.models.UserCourseModel;
import com.xpto.distancelearning.authuser.models.UserModel;

import java.util.UUID;

public interface UserCourseService {

    boolean existsByUserAndCourseId(UserModel userModel, UUID courseId);

    UserCourseModel save(UserCourseModel userCourseModel);

}
