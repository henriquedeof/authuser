package com.xpto.distancelearning.authuser.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Henrique: Controller just for testing purposes.
// The idea is to test the cases where I have a configuration (dl-authuser-service.yml) in the config-server-repo and I want to refresh it without restarting this service.
// I can do it by calling the /POST http://localhost:8087/dl-authuser/actuator/refresh endpoint.
@RestController
@RefreshScope // This annotation is used to refresh the configuration without restarting the service.
public class RefreshScopeController {

    // This is coming from the dl-authuser-service.yml file, that is in the config-server-repo (Global Config Pattern).
    @Value("${authuser.refreshscope.name}")
    private String name;

    @RequestMapping("/refreshscope")
    public String refreshscope() {
        return this.name;
    }
}