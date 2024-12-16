package com.xpto.distancelearning.authuser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableEurekaClient // No more need to specify this annotation. It is included in the dependencies
public class AuthUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthUserApplication.class, args);
    }

}
