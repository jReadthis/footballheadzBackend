package com.dmv.footballheadz.game.impl;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsEmptyCollection.emptyCollectionOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository repository;

    @InjectMocks
    private GameService service;

    @Test
    public void readShouldReturnEmptyOptionalWhenNoGameFound() throws Exception {

        when(repository.read("1d")).thenReturn(Optional.empty());
        Optional<Game> result = service.read("1d");
        assertThat(result, is(Optional.empty()));
    }

    @Test
    public void readShouldReturnResultWhenGameFound() throws Exception {

        Game customer = new Game().withId("1d");
        when(repository.read("1d")).thenReturn(Optional.of(customer));
        Game result = service.read("1d").get();
        assertThat(result, is(equalTo(customer)));
    }

    @Test
    public void createShouldReturnNewGameWhenGameNotYetExists() throws Exception {

        Game newGame = new Game().withId("1d");
        when(repository.read("1d")).thenReturn(Optional.empty());
        Game result = service.create(newGame).get();
        assertThat(result, is(equalTo(newGame)));
        verify(repository).save(newGame);
    }

    @Test
    public void replaceShouldReturnEmptyOptionalWhenGameNotFound() throws Exception {

        Game newGameData = new Game().withId("1d").withHomeTeam("GimmyDatLoot");
        when(repository.read("1d")).thenReturn(Optional.empty());
        Optional<Game> result = service.replace(newGameData);
        assertThat(result, is(Optional.empty()));
        verify(repository, never()).save(newGameData);
    }

    @Test
    public void replaceShouldOverwriteAndReturnNewDataWhenGameExists() throws Exception {

        Game oldGameData = new Game().withId("1d").withHomeTeam("GimmyDatLoot");
        Game newGameData = new Game().withId("1d").withWeek("1");
        when(repository.read("1d")).thenReturn(Optional.of(oldGameData));
        Game result = service.replace(newGameData).get();
        assertThat(result, is(equalTo(newGameData)));
        verify(repository).save(newGameData);
    }

    @Test
    public void updateShouldReturnEmptyOptionalWhenGameNotFound() throws Exception {

        Game newGameData = new Game().withId("1d").withWeek("1");
        when(repository.read("1d")).thenReturn(Optional.empty());
        Optional<Game> result = service.update(newGameData);
        assertThat(result, is(Optional.empty()));
        verify(repository, never()).save(newGameData);
    }

    @Test
    public void updateShouldOverwriteExistingFieldAndReturnNewDataWhenGameExists() throws Exception {

        Game oldGameData = new Game().withId("1d").withWeek("2");
        Game newGameData = new Game().withId("1d").withWeek("1");
        when(repository.read("1d")).thenReturn(Optional.of(oldGameData));
        Game result = service.update(newGameData).get();
        assertThat(result, is(equalTo(newGameData)));
        verify(repository).save(newGameData);
    }

    @Test
    public void updateShouldNotOverwriteExistingFieldIfNoNewValuePassedAndShouldReturnNewDataWhenGameExists() throws Exception {

        Game oldGameData = new Game().withId("1d").withWeek("2");
        Game newGameData = new Game().withId("1d").withHomeTeam("GimmyDatLoot");
        Game expectedResult = new Game().withId("1d").withWeek("2").withHomeTeam("GimmyDatLoot");
        when(repository.read("1d")).thenReturn(Optional.of(oldGameData));
        Game result = service.update(newGameData).get();
        assertThat(result, is(equalTo(expectedResult)));
        verify(repository).save(expectedResult);
    }

    @Test
    public void deleteShouldReturnFalseWhenGameNotFound() throws Exception {

        when(repository.read("1d")).thenReturn(Optional.empty());
        boolean result = service.delete("1d");
        assertThat(result, is(false));
    }

    @Test
    public void deleteShouldReturnTrueWhenGameDeleted() throws Exception {

        when(repository.read("1d")).thenReturn(Optional.of(new Game().withId("1d")));
        boolean result = service.delete("1d");
        assertThat(result, is(true));
        verify(repository).delete("1d");
    }

    @Test
    public void listShouldReturnEmptyListWhenNothingFound() throws Exception {

        when(repository.readAll()).thenReturn(emptyList());
        List<Game> result = service.list();
        assertThat(result, is(emptyCollectionOf(Game.class)));
    }

    @Test
    public void listShouldReturnAllGames() throws Exception {

        Game customer1 = new Game().withId("1d");
        Game customer2 = new Game().withId("2d");
        when(repository.readAll()).thenReturn(asList(customer1, customer2));
        List<Game> result = service.list();
        assertThat(result, containsInAnyOrder(customer1, customer2));
    }

    @Test
    public void listExpressionShouldReturnEmptyListWhenNothingFound() throws Exception {

        when(repository.readExpression(any(DynamoDBScanExpression.class))).thenReturn(emptyList());
        List<Game> result = service.listOfGamesByTeam("GimmyDaLoot");
        assertThat(result, is(emptyCollectionOf(Game.class)));
    }

    @Test
    public void listExpressionShouldReturnGamesForYear() throws Exception {

        Game game1 = new Game().withId("1d").withYear("2012").withHomeTeam("GimmyDaLoot").withAwayTeam("HogPit");
        Game game2 = new Game().withId("2d").withYear("2012").withAwayTeam("GimmyDaLoot");

        when(repository.readExpression(any(DynamoDBScanExpression.class)))
                .thenReturn(asList(game1, game2));
        List<Game> result = service.listOfGamesByTeam("GimmyDaLoot");
        assertThat(result, containsInAnyOrder(game1, game2));
    }
}