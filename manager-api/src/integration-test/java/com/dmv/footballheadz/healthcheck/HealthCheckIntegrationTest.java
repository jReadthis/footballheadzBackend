package com.dmv.footballheadz.healthcheck;

import com.dmv.footballheadz.Application;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.URI;

import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HealthCheckIntegrationTest {

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void healthCheckShouldReturnUp() throws Exception {

        ResponseEntity<String> response = restTemplate.getForEntity(url("/health"), String.class);
        assertThat(response.getStatusCode(), CoreMatchers.is(HttpStatus.OK));
        assertThat(response.getBody(), CoreMatchers.is("up"));
    }

    private URI url(String url) {

        return URI.create("http://localhost:" + port + url);
    }

}
