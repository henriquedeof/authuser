package com.xpto.distancelearning.authuser.services;

import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UtilsService {

    String createUrlGetAllCoursesByUser(UUID userId, Pageable pageable);
}
