package com.xpto.distancelearning.authuser.clients;

import com.xpto.distancelearning.authuser.dtos.CourseDto;
import com.xpto.distancelearning.authuser.dtos.ResponsePageDto;
import com.xpto.distancelearning.authuser.services.UtilsService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
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

import java.util.ArrayList;
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

    // The @Retry annotation is used to retry the request in case of failure. The retryInstance was configured in the application.yml resilience4j.retry.instances.retryInstance.etc
        // It is used in conjunction with RestTemplateConfig, checking if I had a timeout or a connection error.
        // I used the @Retry annotation just for testing purposes.
    //@Retry(name = "retryInstance", fallbackMethod = "retryFallback") // Using retry at the method level.
    @CircuitBreaker(name = "circuitBreakerInstance") // , fallbackMethod = "circuitBreakerFallback") // Using circuit breaker at the method level.
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

    // The fallback method is called when the circuit breaker is open, and the request is not made.
    // The method must have the same return and signature as the method that calls it.
    public Page<CourseDto> retryFallback(UUID userId, Pageable pageable, Throwable t) {
        log.error("Error in the retry retryFallback, for the userId {} ", userId, t);
        //return new PageImpl<>(List.of());
        return new PageImpl<>(new ArrayList<>());
    }

    // The fallback method is called when the circuit breaker is open, and the request is not made.
    // The method must have the same return and signature as the method that calls it.
    public Page<CourseDto> circuitBreakerFallback(UUID userId, Pageable pageable, Throwable t) {
        log.error("Error in the circuit breaker circuitBreakerFallback, for the userId {} ", userId, t);
        //return new PageImpl<>(List.of());
        return new PageImpl<>(new ArrayList<>());
    }


//===============================================================================================================
//    NOTE: Methods deleted as the communication is now asynchronous
//===============================================================================================================

//    public void deleteUserInCourse(UUID userId){
//        String url = REQUEST_URL_COURSE + "/courses/users/" + userId;
//        restTemplate.exchange(url, HttpMethod.DELETE, null, String.class);
//    }
}
