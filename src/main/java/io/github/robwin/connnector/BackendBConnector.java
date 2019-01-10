package io.github.robwin.connnector;


import io.github.robwin.exception.BusinessException;
import io.reactivex.Observable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;

import static io.github.robwin.util.SleepUtils.cleanSleep;

@Component(value = "backendBConnector")
public class BackendBConnector implements Connector {

    private int resourceCounter = 0;
    private int intermittentCounter = 1;

    @Override
    public String failure() {
        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "This is a remote exception");
    }

    @Override
    public String success() {
        return "Hello World from backend B";
    }

    @Override
    public String ignoreException() {
        throw new BusinessException("This exception is ignored by the CircuitBreaker of backend B");
    }

    @Override
    public String getLimitedResource() {
        return "Resouce " + resourceCounter++ + " acquired \n";
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

        if (intermittentCounter % 3 == 0 || intermittentCounter % 4 == 0 ||
        intermittentCounter % 5 == 0 || intermittentCounter % 2 == 0) {
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
