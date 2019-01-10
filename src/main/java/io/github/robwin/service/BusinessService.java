package io.github.robwin.service;


import io.vavr.control.Try;

public interface BusinessService {
    String failure();

    String success();

    String ignore();

    String getLimitedResource();

    String getHeavyResource(String resourceId);

    String getIntermittentResource();

    Try<String> methodWithRecovery();
}
