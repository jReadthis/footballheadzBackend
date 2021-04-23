package com.dmv.footballheadz.manager.impl;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsEmptyCollection.emptyCollectionOf;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class ManagerServiceTest {

    @Mock
    private ManagerRepository repository;

    @InjectMocks
    private ManagerService service;

    @Test
    public void readShouldReturnEmptyOptionalWhenNoManagerFound() throws Exception {

        when(repository.read("John Doe")).thenReturn(Optional.empty());
        Optional<Manager> result = service.read("John Doe");
        assertThat(result, is(Optional.empty()));
    }

    @Test
    public void readShouldReturnResultWhenManagerFound() throws Exception {

        Manager customer = new Manager().withManagerName("John Doe");
        when(repository.read("John Doe")).thenReturn(Optional.of(customer));
        Manager result = service.read("John Doe").get();
        assertThat(result, is(equalTo(customer)));
    }

    @Test
    public void createShouldReturnEmptyOptionalWhenManagerAlreadyExists() throws Exception {

        Manager existingManager = new Manager().withManagerName("John Doe")
                .withTeams(Arrays.asList("Team1", "Team2"));
        when(repository.read("John Doe")).thenReturn(Optional.of(existingManager));
        Manager newManager = new Manager().withManagerName("John Doe");
        Optional<Manager> result = service.create(newManager);
        assertThat(result, is(Optional.empty()));
        verify(repository, never()).save(newManager);
    }

    @Test
    public void createShouldReturnNewManagerWhenManagerNotYetExists() throws Exception {

        Manager newManager = new Manager().withManagerName("John Doe");
        when(repository.read("John Doe")).thenReturn(Optional.empty());
        Manager result = service.create(newManager).get();
        assertThat(result, is(equalTo(newManager)));
        verify(repository).save(newManager);
    }

    @Test
    public void replaceShouldReturnEmptyOptionalWhenManagerNotFound() throws Exception {

        Manager newManagerData = new Manager().withManagerName("John Doe")
                .withTeams(Arrays.asList("Team1","Team2","Team3"));
        when(repository.read("John Doe")).thenReturn(Optional.empty());
        Optional<Manager> result = service.replace(newManagerData);
        assertThat(result, is(Optional.empty()));
        verify(repository, never()).save(newManagerData);
    }

    @Test
    public void replaceShouldOverwriteAndReturnNewDataWhenManagerExists() throws Exception {

        Manager oldManagerData = new Manager().withManagerName("John Doe")
                .withTeams(Arrays.asList("Team1","Team2"));
        Manager newManagerData = new Manager().withManagerName("John Doe").withActiveStatus(true);
        when(repository.read("John Doe")).thenReturn(Optional.of(oldManagerData));
        Manager result = service.replace(newManagerData).get();
        assertThat(result, is(equalTo(newManagerData)));
        verify(repository).save(newManagerData);
    }

    @Test
    public void updateShouldReturnEmptyOptionalWhenManagerNotFound() throws Exception {

        Manager newManagerData = new Manager().withManagerName("John Doe").withActiveStatus(true);
        when(repository.read("John Doe")).thenReturn(Optional.empty());
        Optional<Manager> result = service.update(newManagerData);
        assertThat(result, is(Optional.empty()));
        verify(repository, never()).save(newManagerData);
    }

    @Test
    public void updateShouldOverwriteExistingFieldAndReturnNewDataWhenManagerExists() throws Exception {

        Manager oldManagerData = new Manager().withManagerName("John Doe").withActiveStatus(false);
        Manager newManagerData = new Manager().withManagerName("John Doe").withActiveStatus(true);
        when(repository.read("John Doe")).thenReturn(Optional.of(oldManagerData));
        Manager result = service.update(newManagerData).get();
        assertThat(result, is(equalTo(newManagerData)));
        verify(repository).save(newManagerData);
    }

    @Test
    public void updateShouldNotOverwriteExistingFieldIfNoNewValuePassedAndShouldReturnNewDataWhenManagerExists() throws Exception {

        Manager oldManagerData = new Manager().withManagerName("John Doe").withActiveStatus(false);
        Manager newManagerData = new Manager().withManagerName("John Doe")
                .withTeams(Arrays.asList("Team1","Team2","Team3"));
        Manager expectedResult = new Manager().withManagerName("John Doe").withActiveStatus(false)
                .withTeams(Arrays.asList("Team1","Team2","Team3"));
        when(repository.read("John Doe")).thenReturn(Optional.of(oldManagerData));
        Manager result = service.update(newManagerData).get();
        assertThat(result, is(equalTo(expectedResult)));
        verify(repository).save(expectedResult);
    }

    @Test
    public void deleteShouldReturnFalseWhenManagerNotFound() throws Exception {

        when(repository.read("John Doe")).thenReturn(Optional.empty());
        boolean result = service.delete("John Doe");
        assertThat(result, is(false));
    }

    @Test
    public void deleteShouldReturnTrueWhenManagerDeleted() throws Exception {

        when(repository.read("John Doe")).thenReturn(Optional.of(new Manager().withManagerName("John Doe")));
        boolean result = service.delete("John Doe");
        assertThat(result, is(true));
        verify(repository).delete("John Doe");
    }

    @Test
    public void listShouldReturnEmptyListWhenNothingFound() throws Exception {

        when(repository.readAll()).thenReturn(emptyList());
        List<Manager> result = service.list();
        assertThat(result, is(emptyCollectionOf(Manager.class)));
    }

    @Test
    public void listShouldReturnAllManagers() throws Exception {

        Manager customer1 = new Manager().withManagerName("John Doe");
        Manager customer2 = new Manager().withManagerName("Jane Doe");
        when(repository.readAll()).thenReturn(asList(customer1, customer2));
        List<Manager> result = service.list();
        assertThat(result, containsInAnyOrder(customer1, customer2));
    }

    @Test
    public void listExpressionShouldReturnEmptyListWhenNothingFound() throws Exception {

        when(repository.readExpression(any(DynamoDBScanExpression.class))).thenReturn(emptyList());
        List<Manager> result = service.retrieveTeams("John Doe");
        assertThat(result, is(emptyCollectionOf(Manager.class)));
    }

    @Test
    public void listExpressionShouldReturnManagersForYear() throws Exception {

        String managerName = "John Doe";
        String managerName2 = "ManagerName";
        Manager manager1 = new Manager().withManagerName(managerName).withActiveStatus(true)
                .withTeams(Arrays.asList("Team1", "Team2", "Team3"));
        Manager manager2 = new Manager().withManagerName(managerName2).withActiveStatus(false)
                .withTeams(Arrays.asList("TeamA","TeamB"));

        when(repository.readExpression(any(DynamoDBScanExpression.class)))
                .thenReturn(asList(manager1, manager2));
        List<Manager> result = service.retrieveTeams(managerName);
        assertThat(result, containsInAnyOrder(manager1, manager2));
    }

}