package io.github.robwin.service;


import io.github.resilience4j.cache.Cache;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.autoconfigure.CircuitBreakerProperties;
import io.github.resilience4j.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.robwin.connnector.Connector;
import io.reactivex.Observable;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.spi.CachingProvider;

@Service(value = "businessBService")
public class BusinessBService implements BusinessService  {

    private final Connector backendBConnector;
    private final CircuitBreaker circuitBreaker;
    private final RateLimiter rateLimiter;
    private final Cache<String, String> cacheContext;
    private final Retry retry;

    public BusinessBService(@Qualifier("backendBConnector") Connector backendBConnector,
                            CircuitBreakerRegistry circuitBreakerRegistry,
                            RateLimiterRegistry rateLimiterRegistry,
                            CircuitBreakerProperties circuitBreakerProperties){
        this.backendBConnector = backendBConnector;
        circuitBreaker = circuitBreakerRegistry.circuitBreaker("backendB",
                () -> circuitBreakerProperties.createCircuitBreakerConfig("backendB"));

        RateLimiterConfig rateLimiterConfig = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofSeconds(20))
                .limitForPeriod(5)
                .build();
        this.rateLimiter = rateLimiterRegistry.rateLimiter("backendB", rateLimiterConfig);

        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager();
        MutableConfiguration<String, String> cacheConfig = new MutableConfiguration<String, String>()
                .setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(javax.cache.expiry.Duration.ONE_MINUTE));
        javax.cache.Cache<String, String> heavyResourceCache
                = cacheManager.createCache("backendBCache", cacheConfig);
        this.cacheContext = Cache.of(heavyResourceCache);

        this.retry = Retry.of("backendBRetry", RetryConfig.custom()
                .maxAttempts(3)
                .build());
    }

    public String failure() {
        return CircuitBreaker.decorateSupplier(circuitBreaker, backendBConnector::failure).get();
    }

    public String success() {
        return CircuitBreaker.decorateSupplier(circuitBreaker, backendBConnector::success).get();
    }

    @Override
    public String ignore() {
        return CircuitBreaker.decorateSupplier(circuitBreaker, backendBConnector::ignoreException).get();
    }

    @Override
    public String getLimitedResource() {
        return RateLimiter.decorateSupplier(rateLimiter, backendBConnector::getLimitedResource).get();
    }

    @Override
    public String getHeavyResource(final String resourceId) {
        Function<String, String> cachedFunction = Cache.decorateSupplier(cacheContext,
                () -> backendBConnector.getHeavyResource(resourceId));
        return cachedFunction.apply(resourceId);
    }

    @Override
    public String getIntermittentResource() {
        return Retry.decorateSupplier(retry, backendBConnector::getIntermittentResource).get();
    }

    @Override
    public Try<String> methodWithRecovery() {
        Supplier<String> backendFunction = CircuitBreaker.decorateSupplier(circuitBreaker, () -> backendBConnector.failure());
        return Try.ofSupplier(backendFunction)
                .recover((throwable) -> recovery(throwable));
    }

    public Observable<String> methodWhichReturnsAStream() {
        return backendBConnector.methodWhichReturnsAStream()
                .timeout(1, TimeUnit.SECONDS)
                .lift(CircuitBreakerOperator.of(circuitBreaker));
    }

    private String recovery(Throwable throwable) {
        // Handle exception and invoke fallback
        return "Hello world from recovery";
    }
}
