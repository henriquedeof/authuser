package com.xpto.distancelearning.authuser.configs;

import net.kaczmarzyk.spring.data.jpa.web.SpecificationArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Configuration
public class ResolverConfig extends WebMvcConfigurationSupport {

    /**
     * Registers the SpecificationArgumentResolver and PageableHandlerMethodArgumentResolver.
     * The SpecificationArgumentResolver is used to convert the parameters (filters) in the URL to a Specification object.
     *
     * @See SpecificationTemplate for an example of how the filters are defined.
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new SpecificationArgumentResolver()); // -> https://github.com/tkaczmarzyk/specification-arg-resolver
        argumentResolvers.add(new PageableHandlerMethodArgumentResolver()); // Adding resolver for pagination
        super.addArgumentResolvers(argumentResolvers);
    }
}
