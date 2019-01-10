package io.github.robwin.connnector;

import io.reactivex.Observable;

public interface Connector {
    String failure();

    String success();

    String ignoreException();

    String getLimitedResource();

    String getHeavyResource(String resourceId);

    String getIntermittentResource();

    Observable<String> methodWhichReturnsAStream();
}
