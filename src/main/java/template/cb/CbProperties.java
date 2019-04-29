package template.cb;

import java.util.List;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("circuitbreaker")
public class CbProperties {

    List<String> names;

}