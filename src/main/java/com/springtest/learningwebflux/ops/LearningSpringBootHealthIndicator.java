package com.springtest.learningwebflux.ops;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LearningSpringBootHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        try {
            URL url = new URL("http://springtest.com/books/learning-springboot");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int statusCode = conn.getResponseCode();
            if (statusCode >= 200 && statusCode <300) {
                return Health.up().build();
            } else {
                return Health.down()
                        .withDetail("HTTP Status Code",statusCode)
                        .build();
            }
        } catch (MalformedURLException e) {
            return Health.down(e).build();
        } catch (IOException e) {
            return Health.down(e).build();
        }

    }
}
