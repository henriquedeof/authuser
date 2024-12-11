package com.xpto.distancelearning.authuser.repositories;

import com.xpto.distancelearning.authuser.models.UserCourseModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserCourseRepository extends JpaRepository<UserCourseModel, UUID> {

}
