package io.github.robwin.service;

import io.github.resilience4j.retry.Retry;
import io.github.robwin.connnector.Connector;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service(value = "businessAService")
public class BusinessAService implements BusinessService {

    private final Connector backendAConnector;

    public BusinessAService(@Qualifier("backendAConnector") Connector backendAConnector){
        this.backendAConnector = backendAConnector;
    }

    @Override
    public String failure() {
        return backendAConnector.failure();
    }

    @Override
    public String success() {
        return backendAConnector.success();
    }

    @Override
    public String ignore() {
        return backendAConnector.ignoreException();
    }

    @Override
    public String getLimitedResource() {
        return backendAConnector.getLimitedResource();
    }

    @Override
    public String getHeavyResource(String resourceId) {
        return backendAConnector.getHeavyResource(resourceId);
    }

    @Override
    public String getIntermittentResource() {
        return backendAConnector.getIntermittentResource();
    }

    @Override
    public Try<String> methodWithRecovery() {
        return Try.of(backendAConnector::failure)
                .recover((throwable) -> recovery());
    }

    private String recovery() {
        return "Hello world from recovery";
    }
}
