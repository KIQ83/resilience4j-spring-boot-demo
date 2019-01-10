package io.github.robwin.connnector;


import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.robwin.exception.BusinessException;
import io.reactivex.Observable;

import static io.github.robwin.util.SleepUtils.cleanSleep;

/**
 * This Connector shows how to use the CircuitBreaker annotation.
 */
@CircuitBreaker(backend = "backendA")
@Component(value = "backendAConnector")
public class BackendAConnector implements Connector {

    private int intermittentCounter = 1;

    @Override
    public String success() {
        System.out.println("Success Backend A called");
        cleanSleep(1000);
        return "Hello World from backend A";
    }

    @Override
    public String failure() {
        System.out.println("Failure Backend A called");
        cleanSleep(1000);
        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "This is a remote exception");
    }

    @Override
    public String ignoreException() {
        System.out.println("Ignored Exception Backend A called");
        throw new BusinessException("This exception is ignored by the CircuitBreaker of backend A");
    }

    @Override
    public String getLimitedResource() {
        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Unsupported operation exception");
    }

    @Override
    public String getHeavyResource(String resourceId) {
        System.out.println("Heavy lifting begin");
        cleanSleep(5000L);
        return "BIRL " + resourceId;
    }

    @Override
    public String getIntermittentResource() {
        System.out.println("Trying to get intermittent resource");
        intermittentCounter++;

        if (intermittentCounter % 3 == 0) {
            System.out.println("INTERMITTENT ERROR");
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERMITTENT ERROR");
        }

        return "Resource " + intermittentCounter;
    }

    @Override
    public Observable<String> methodWhichReturnsAStream() {
        return Observable.never();
    }
}
