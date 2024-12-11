package com.xpto.distancelearning.authuser.services.impl;

import com.xpto.distancelearning.authuser.repositories.UserCourseRepository;
import com.xpto.distancelearning.authuser.services.UserCourseService;
import org.springframework.stereotype.Service;

@Service
public class UserCourseServiceImpl implements UserCourseService {

    private final UserCourseRepository userCourseRepository;

    public UserCourseServiceImpl(UserCourseRepository userCourseRepository) {
        this.userCourseRepository = userCourseRepository;
    }
}
