package com.dmv.footballheadz.manager.impl;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;

@ExtendWith(SpringExtension.class)
class ManagerControllerTest {

    @InjectMocks
    private ManagerController controller;

    @Mock
    private ManagerService service;

    @Test
    public void listShouldRespondWithNoContentWhenNothingInDatabase() throws Exception {

        when(service.list()).thenReturn(emptyList());
        ResponseEntity<List<Manager>> result = controller.list();
        assertThat(result, is(responseEntityWithStatus(NO_CONTENT)));
    }

    @Test
    public void listShouldRespondWithOkAndResultsFromService() throws Exception {

        Manager manager1 = new Manager().withManagerName("Jane Doe");
        Manager manager2 = new Manager().withManagerName("John Doe");
        when(service.list()).thenReturn(asList(manager1, manager2));
        ResponseEntity<List<Manager>> result = controller.list();
        assertThat(result, is(allOf(
                responseEntityWithStatus(OK),
                responseEntityThat(containsInAnyOrder(manager1, manager2)))));
    }

    @Test
    public void listExpressionShouldRespondWithNoContentWhenNothingInDatabase() throws Exception {

        String managerName = "John Doe";
        when(service.retrieveTeams(managerName)).thenReturn(emptyList());
        ResponseEntity<Set<String>> result = controller.retrieveManagerTeams(managerName);
        assertThat(result, is(responseEntityWithStatus(NO_CONTENT)));
    }

    @Test
    public void listExpressionShouldRespondWithOkAndEmptyTeamsResultsFromService() throws Exception {

        String managerName = "John Doe";
        Manager manager1 = new Manager().withManagerName(managerName);
        when(service.retrieveTeams(managerName)).thenReturn(asList(manager1));
        ResponseEntity<Set<String>> result = controller.retrieveManagerTeams(managerName);
        assertThat(result, is(responseEntityWithStatus(NO_CONTENT)));
    }

    @Test
    public void listExpressionShouldRespondWithOkAndResultsFromService() throws Exception {

        String managerName = "John Doe";
        Manager manager1 = new Manager().withManagerName(managerName).withTeams(asList("Team1", "Team2"));
        when(service.retrieveTeams(managerName)).thenReturn(asList(manager1));
        ResponseEntity<Set<String>> result = controller.retrieveManagerTeams(managerName);
        assertThat(result, is(allOf(
                responseEntityWithStatus(OK),
                responseEntityThat(equalTo(manager1.getTeams())))));
    }

    @Test
    public void readShouldReplyWithNotFoundIfNoSuchManager() throws Exception {

        when(service.read("John Doe")).thenReturn(Optional.empty());
        ResponseEntity<Manager> result = controller.read("John Doe");
        assertThat(result, is(responseEntityWithStatus(NOT_FOUND)));
    }

    @Test
    public void readShouldReplyWithManagerIfManagerExists() throws Exception {

        Manager manager = new Manager().withManagerName("John Doe");
        when(service.read("John Doe")).thenReturn(Optional.of(manager));
        ResponseEntity<Manager> result = controller.read("John Doe");
        assertThat(result, is(allOf(
                responseEntityWithStatus(OK),
                responseEntityThat(equalTo(manager)))));
    }

    @Test
    public void createShouldReplyWithConflictIfManagerAlreadyExists() throws Exception {

        Manager manager = new Manager().withManagerName("John Doe");
        when(service.create(manager)).thenReturn(Optional.empty());
        ResponseEntity<Manager> result = controller.create(manager);
        assertThat(result, is(responseEntityWithStatus(CONFLICT)));
    }

    @Test
    public void createShouldReplyWithCreatedAndManagerData() throws Exception {

        Manager manager = new Manager().withManagerName("John Doe");
        when(service.create(manager)).thenReturn(Optional.of(manager));
        ResponseEntity<Manager> result = controller.create(manager);
        assertThat(result, is(allOf(
                responseEntityWithStatus(CREATED),
                responseEntityThat(equalTo(manager)))));
    }

    @Test
    public void putShouldReplyWithNotFoundIfManagerDoesNotExist() throws Exception {

        Manager newManagerData = new Manager().withManagerName("John Doe").withTeams(Arrays.asList("GimmyDaLoot"));
        when(service.replace(newManagerData)).thenReturn(Optional.empty());
        ResponseEntity<Manager> result = controller.put("John Doe", new Manager().withTeams(Arrays.asList("GimmyDaLoot")));
        assertThat(result, is(responseEntityWithStatus(NOT_FOUND)));
    }

    @Test
    public void putShouldReplyWithUpdatedManagerAndOkIfManagerExists() throws Exception {

        Manager newManagerData = new Manager().withManagerName("John Doe").withTeams(Arrays.asList("GimmyDaLoot"));
        when(service.replace(newManagerData)).thenReturn(Optional.of(newManagerData));
        ResponseEntity<Manager> result = controller.put("John Doe", new Manager().withTeams(Arrays.asList("GimmyDaLoot")));
        assertThat(result, is(allOf(
                responseEntityWithStatus(OK),
                responseEntityThat(equalTo(newManagerData)))));
    }

    @Test
    public void patchShouldReplyWithNotFoundIfManagerDoesNotExist() throws Exception {

        Manager newManagerData = new Manager().withManagerName("John Doe").withTeams(Arrays.asList(Arrays.asList("GimmyDaLoot")));
        when(service.update(newManagerData)).thenReturn(Optional.empty());
        ResponseEntity<Manager> result = controller.patch("John Doe", new Manager().withTeams(Arrays.asList("GimmyDaLoot")));
        assertThat(result, is(responseEntityWithStatus(NOT_FOUND)));
    }

    @Test
    public void patchShouldReplyWithUpdatedManagerAndOkIfManagerExists() throws Exception {

        Manager newManagerData = new Manager().withManagerName("John Doe").withTeams(Arrays.asList("GimmyDaLoot"));
        when(service.update(newManagerData)).thenReturn(Optional.of(newManagerData));
        ResponseEntity<Manager> result = controller.patch("John Doe", new Manager().withTeams(Arrays.asList("GimmyDaLoot")));
        assertThat(result, is(allOf(
                responseEntityWithStatus(OK),
                responseEntityThat(equalTo(newManagerData)))));
    }

    @Test
    public void deleteShouldRespondWithNotFoundIfManagerDoesNotExist() throws Exception {

        when(service.delete("John Doe")).thenReturn(false);
        ResponseEntity<Void> result = controller.delete("John Doe");
        assertThat(result, is(responseEntityWithStatus(NOT_FOUND)));
    }

    @Test
    public void deleteShouldRespondWithNoContentIfDeleteSuccessful() throws Exception {

        when(service.delete("John Doe")).thenReturn(true);
        ResponseEntity<Void> result = controller.delete("John Doe");
        assertThat(result, is(responseEntityWithStatus(NO_CONTENT)));
    }

    private Matcher<ResponseEntity> responseEntityWithStatus(HttpStatus status) {

        return new TypeSafeMatcher<ResponseEntity>() {

            @Override
            protected boolean matchesSafely(ResponseEntity item) {

                return status.equals(item.getStatusCode());
            }

            @Override
            public void describeTo(Description description) {

                description.appendText("ResponseEntity with status ").appendValue(status);
            }
        };
    }

    private <T> Matcher<ResponseEntity<? extends T>> responseEntityThat(Matcher<T> categoryMatcher) {

        return new TypeSafeMatcher<ResponseEntity<? extends T>>() {
            @Override
            protected boolean matchesSafely(ResponseEntity<? extends T> item) {

                return categoryMatcher.matches(item.getBody());
            }

            @Override
            public void describeTo(Description description) {

                description.appendText("ResponseEntity with ").appendValue(categoryMatcher);
            }
        };
    }

}