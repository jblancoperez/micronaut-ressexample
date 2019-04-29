package template;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.ratelimiter.RateLimiter;

import io.github.resilience4j.timelimiter.TimeLimiter;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import template.cb.CircuitBreakerConfigurer;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

@Controller("/test")
public class TestController {

    Random r = new Random();
    CircuitBreaker cb;
    Scheduler e = Schedulers.from(Executors.newFixedThreadPool(10));

    Logger logger = org.slf4j.LoggerFactory.getLogger(TestController.class);
    RateLimiter rt;
    TimeLimiter timeLimiter = TimeLimiter.of(Duration.ofMillis(1000));

    public TestController(CircuitBreakerConfigurer cb) {

        this.cb = cb.getCb("test");
    }

    @Get("/")
    public Single<String> index() {
     
        return test();
    }

    public Single<String> test() {


        Callable<String> c = () -> provider() ;
        return Completable.complete().
        observeOn(e) .andThen(
                        Single.defer( () -> Single.fromCallable(c ) ) .
                        timeout(1000,TimeUnit.MILLISECONDS).
             lift(CircuitBreakerOperator.of(cb))
        ).observeOn(Schedulers.computation()) ;

    }

    private String provider() {
        logger.error("Printing");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
            return "Test";
  }
}