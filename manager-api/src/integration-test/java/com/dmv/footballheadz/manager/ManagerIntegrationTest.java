package com.dmv.footballheadz.manager;

import com.dmv.footballheadz.Application;
import com.dmv.footballheadz.manager.impl.Manager;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.URI;
import java.util.Arrays;

import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ManagerIntegrationTest {

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void postShouldCreateManagerAndRespondWithCreated() throws Exception {
        Manager manager = new Manager().withManagerName(randomUUID().toString());
        ResponseEntity<Manager> result = restTemplate.postForEntity(url("/v1/manager"), manager, Manager.class);
        assertThat(result.getStatusCode(), CoreMatchers.is(HttpStatus.CREATED));
        assertThat(result.getBody(), CoreMatchers.is(CoreMatchers.equalTo(manager)));
    }

    @Test
    public void postShouldNotCreateManagerIfAlreadyExistsAndRespondWithConflict() throws Exception {
        Manager manager = new Manager().withManagerName(randomUUID().toString());
        restTemplate.postForEntity(url("/v1/manager"), manager, Manager.class);
        ResponseEntity<Manager> result = restTemplate.postForEntity(url("/v1/manager"), manager, Manager.class);
        assertThat(result.getStatusCode(), CoreMatchers.is(HttpStatus.CONFLICT));
    }

    @Test
    public void postShouldRespondWithBadRequestIfManagerNameNotPassed() throws Exception {
        Manager manager = new Manager().withActiveStatus(true);
        restTemplate.postForEntity(url("/v1/manager"), manager, Manager.class);
        ResponseEntity<Manager> result = restTemplate.postForEntity(url("/v1/manager"), manager, Manager.class);
        assertThat(result.getStatusCode(), CoreMatchers.is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void getShouldReturnPreviouslyCreatedManagers() throws Exception {
        Manager manager1 = new Manager().withManagerName(randomUUID().toString());
        Manager manager2 = new Manager().withManagerName(randomUUID().toString());
        restTemplate.postForEntity(url("/v1/manager"), manager1, Manager.class);
        restTemplate.postForEntity(url("/v1/manager"), manager2, Manager.class);
        ResponseEntity<Manager[]> result = restTemplate.getForEntity(url("/v1/manager"), Manager[].class);
        assertThat(result.getStatusCode(), CoreMatchers.is(HttpStatus.OK));
        assertThat(Arrays.asList(result.getBody()), CoreMatchers.hasItems(manager1, manager2));
    }

    @Test
    public void getByNameShouldRespondWithNotFoundForManagerThatDoesNotExist() throws Exception {
        String managerName = randomUUID().toString();
        ResponseEntity<Manager> result = restTemplate.getForEntity(url("/v1/manager/" + managerName), Manager.class);
        assertThat(result.getStatusCode(), CoreMatchers.is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void getByNameShouldReturnPreviouslyCreatedManager() throws Exception {
        String managerId = randomUUID().toString();
        Manager manager = new Manager().withManagerName(managerId).withTeams(Arrays.asList("Team1","Team2"));
        restTemplate.postForEntity(url("/v1/manager"), manager, Manager.class);
        ResponseEntity<Manager> result = restTemplate.getForEntity(url("/v1/manager/" + managerId), Manager.class);
        assertThat(result.getStatusCode(), CoreMatchers.is(HttpStatus.OK));
        assertThat(result.getBody(), CoreMatchers.is(CoreMatchers.equalTo(manager)));
    }

    @Test
    public void putShouldReplyWithNotFoundForManagerThatDoesNotExist() throws Exception {
        String managerName = randomUUID().toString();
        Manager manager = new Manager().withManagerName(managerName);
        RequestEntity<Manager> request = new RequestEntity<>(manager, HttpMethod.PUT, url("/v1/manager/" + managerName));
        ResponseEntity<Manager> result = restTemplate.exchange(request, Manager.class);
        assertThat(result.getStatusCode(), CoreMatchers.is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void putShouldReplaceExistingManagerValues() throws Exception {
        String managerName = randomUUID().toString();
        Manager oldManagerData = new Manager().withManagerName(managerName).withActiveStatus(true);
        Manager newManagerData = new Manager().withManagerName(managerName).withTeams(Arrays.asList("Team1","Team3"));
        restTemplate.postForEntity(url("/v1/manager"), oldManagerData, Manager.class);
        RequestEntity<Manager> request = new RequestEntity<>(newManagerData, HttpMethod.PUT, url("/v1/manager/" + managerName));
        ResponseEntity<Manager> result = restTemplate.exchange(request, Manager.class);
        assertThat(result.getStatusCode(), CoreMatchers.is(HttpStatus.OK));
        assertThat(result.getBody(), CoreMatchers.is(CoreMatchers.equalTo(newManagerData)));
    }

    @Test
    public void patchShouldReplyWithNotFoundForManagerThatDoesNotExist() throws Exception {
        String managerName = randomUUID().toString();
        Manager manager = new Manager().withManagerName(managerName);
        RequestEntity<Manager> request = new RequestEntity<>(manager, HttpMethod.PATCH, url("/v1/manager/" + managerName));
        ResponseEntity<Manager> result = restTemplate.exchange(request, Manager.class);
        assertThat(result.getStatusCode(), CoreMatchers.is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void patchShouldAddNewValuesToExistingManagerValues() throws Exception {
        String managerName = randomUUID().toString();
        Manager oldManagerData = new Manager().withManagerName(managerName).withActiveStatus(true);
        Manager newManagerData = new Manager().withManagerName(managerName).withTeams(Arrays.asList("Team1","Team3"));
        Manager expectedNewManagerData = new Manager().withManagerName(managerName).withTeams(Arrays.asList("Team1","Team3")).withActiveStatus(true);
        restTemplate.postForEntity(url("/v1/manager"), oldManagerData, Manager.class);
        RequestEntity<Manager> request = new RequestEntity<>(newManagerData, HttpMethod.PATCH, url("/v1/manager/" + managerName));
        ResponseEntity<Manager> result = restTemplate.exchange(request, Manager.class);
        assertThat(result.getStatusCode(), CoreMatchers.is(HttpStatus.OK));
        assertThat(result.getBody(), CoreMatchers.is(CoreMatchers.equalTo(expectedNewManagerData)));
    }

    @Test
    public void deleteShouldReturnNotFoundWhenManagerDoesNotExist() throws Exception {
        String managerName = randomUUID().toString();
        RequestEntity<Void> request = new RequestEntity<>(HttpMethod.DELETE, url("/v1/manager/" + managerName));
        ResponseEntity<Void> result = restTemplate.exchange(request, Void.class);
        assertThat(result.getStatusCode(), CoreMatchers.is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void deleteShouldRemoveExistingManagerAndRespondWithNoContent() throws Exception {
        String managerName = randomUUID().toString();
        Manager manager = new Manager().withManagerName(managerName);
        restTemplate.postForEntity(url("/v1/manager"), manager, Manager.class);
        RequestEntity<Void> request = new RequestEntity<>(HttpMethod.DELETE, url("/v1/manager/" + managerName));
        ResponseEntity<Void> result = restTemplate.exchange(request, Void.class);
        assertThat(result.getStatusCode(), CoreMatchers.is(HttpStatus.NO_CONTENT));
    }

    private URI url(String url) {

        return URI.create("http://localhost:" + port + url);
    }
}
