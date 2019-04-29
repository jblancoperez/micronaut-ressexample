package template.cb;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Singleton;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.micrometer.CircuitBreakerMetrics;
import io.micrometer.core.instrument.MeterRegistry;

@Singleton
public class CircuitBreakerConfigurer {

    private final CircuitBreakerRegistry registry;
    private final Set<String> active;

    public CircuitBreakerConfigurer(MeterRegistry meterRegistry,CbProperties props) {
        registry = CircuitBreakerRegistry.ofDefaults();
        active = new HashSet<>();
        for (String name : props.names) {
            registry.circuitBreaker(name);
            active.add(name);
        }
        CircuitBreakerMetrics.ofCircuitBreakerRegistry(registry).bindTo(meterRegistry);
    }

    public CircuitBreaker getCb(String name) {
        if (active.contains(name))
            return registry.circuitBreaker(name);
        else {
            return null;
        }
    }
}