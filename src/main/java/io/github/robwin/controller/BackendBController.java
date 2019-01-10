package io.github.robwin.controller;

import io.github.robwin.service.BusinessService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/backendB")
public class BackendBController {

    private final BusinessService businessBService;
    private final HealthIndicator rateLimiterHealthIndicator;

    public BackendBController(@Qualifier("businessBService")BusinessService businessBService,
                              @Qualifier("backendBRateLimiterHealth") HealthIndicator rateLimiterHealthIndicator){
        this.businessBService = businessBService;
        this.rateLimiterHealthIndicator = rateLimiterHealthIndicator;
    }

    @GetMapping("failure")
    public String backendBFailure(){
        return businessBService.failure();
    }

    @GetMapping("success")
    public String backendBSuccess(){
        return businessBService.success();
    }

    @GetMapping("ignore")
    public String ignore(){
        return businessBService.ignore();
    }

    @GetMapping("getLimitedResource")
    public String limitedResouce() {
        return businessBService.getLimitedResource();
    }

    @GetMapping("rateLimiterHealth")
    public String rateLimiterHealth() {
        return rateLimiterHealthIndicator.health().toString();
    }

    @GetMapping("getHeavyResource/{resourceId}")
    public String heavyResource(@PathVariable("resourceId") String resourceId) {
        return businessBService.getHeavyResource(resourceId);
    }

    @GetMapping("getIntermittentResource")
    public String heavyResource() {
        return businessBService.getIntermittentResource();
    }

}
