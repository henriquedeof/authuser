package com.xpto.distancelearning.authuser.clients;

import com.xpto.distancelearning.authuser.dtos.CourseDto;
import com.xpto.distancelearning.authuser.dtos.ResponsePageDto;
import com.xpto.distancelearning.authuser.services.UtilsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Log4j2
@Component
public class CourseClient {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UtilsService utilsService;

    @Value("${dl.api.url.course}")
    private String REQUEST_URL_COURSE;

    public Page<CourseDto> getAllCoursesByUser(UUID userId, Pageable pageable) {
        List<CourseDto> searchResult = null;
        ResponseEntity<ResponsePageDto<CourseDto>> result = null;
        String url = REQUEST_URL_COURSE + utilsService.createUrlGetAllCoursesByUser(userId, pageable);

        log.debug("Request URL: {} ", url);
        log.info("Request URL: {} ", url);

        try {
            ParameterizedTypeReference<ResponsePageDto<CourseDto>> responseType = new ParameterizedTypeReference<>() { };
            result = restTemplate.exchange(url, HttpMethod.GET, null, responseType); // It calls the Course API and also the dl-course.tb_courses_users table
            searchResult = result.getBody().getContent();
            log.debug("Response Number of Elements: {} ", searchResult.size());
        } catch (HttpStatusCodeException e) {
            log.error("Error request /courses {} ", e.getMessage(), e);
        }
        log.info("Ending request /courses userId {} ", userId);
        return result.getBody();
    }

//    public Page<CourseDto> getAllCoursesByUser(UUID userId, Pageable pageable){
//        List<CourseDto> searchResult = null;
//        String url = REQUEST_URL_COURSE + utilsService.createUrlGetAllCoursesByUser(userId, pageable);
//        log.debug("Request URL: {} ", url);
//        log.info("Request URL: {} ", url);
//        try{
//            ParameterizedTypeReference<ResponsePageDto<CourseDto>> responseType = new ParameterizedTypeReference<ResponsePageDto<CourseDto>>() {};
//            ResponseEntity<ResponsePageDto<CourseDto>> result = restTemplate.exchange(url, HttpMethod.GET, null, responseType);
//            searchResult = result.getBody().getContent();
//            log.debug("Response Number of Elements: {} ", searchResult.size());
//        } catch (HttpStatusCodeException e){
//            log.error("Error request /courses {} ", e);
//        }
//        log.info("Ending request /courses userId {} ", userId);
//        return new PageImpl<>(searchResult);
//    }


//===============================================================================================================
//    NOTE: Methods deleted as the communication is now asynchronous
//===============================================================================================================

//    public void deleteUserInCourse(UUID userId){
//        String url = REQUEST_URL_COURSE + "/courses/users/" + userId;
//        restTemplate.exchange(url, HttpMethod.DELETE, null, String.class);
//    }
}
