package com.xpto.distancelearning.authuser.services.impl;

import com.xpto.distancelearning.authuser.services.UtilsService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UtilsServiceImpl implements UtilsService {

//    private String REQUEST_URI = "http://localhost:8082";

    @Override
    public String createUrlGetAllCoursesByUser(UUID userId, Pageable pageable) {
//        return REQUEST_URI + "/courses?userId=" + userId + "&page=" + pageable.getPageNumber() + "&size=" + pageable.getPageSize()
//                + "&sort=" + pageable.getSort().toString().replaceAll(": ", ",");

        return "/courses?userId=" + userId + "&page=" + pageable.getPageNumber() + "&size=" + pageable.getPageSize()
                + "&sort=" + pageable.getSort().toString().replaceAll(": ", ",");
    }
}
