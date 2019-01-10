package io.github.robwin.controller;

import io.github.robwin.service.BusinessService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/backendA")
public class BackendAController {

    private final BusinessService businessAService;
    private final HealthIndicator circuitBreakerHealth;

    public BackendAController(@Qualifier("businessAService") BusinessService businessAService,
                              @Qualifier("backendA") HealthIndicator healthIndicator){
        this.businessAService = businessAService;
        this.circuitBreakerHealth = healthIndicator;
    }

    @GetMapping("failure")
    public String failure(){
        return businessAService.failure();
    }

    @GetMapping("success")
    public String success(){
        return businessAService.success();
    }

    @GetMapping("ignore")
    public String ignore(){
        return businessAService.ignore();
    }

    @GetMapping("recover")
    public String methodWithRecovery(){
        return businessAService.methodWithRecovery().get();
    }

    @GetMapping("health")
    public String health() {
        return circuitBreakerHealth.health().toString();
    }

    @GetMapping("getHeavyResource/{resourceId}")
    public String heavyResource(@PathVariable("resourceId") String resourceId) {
        return businessAService.getHeavyResource(resourceId);
    }

    @GetMapping("getIntermittentResource")
    public String heavyResource() {
        return businessAService.getIntermittentResource();
    }

}
