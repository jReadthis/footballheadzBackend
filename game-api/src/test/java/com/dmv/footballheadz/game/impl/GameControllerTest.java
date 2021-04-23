package com.dmv.footballheadz.game.impl;

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

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;

@ExtendWith(SpringExtension.class)
class GameControllerTest {

    @InjectMocks
    private GameController controller;

    @Mock
    private GameService service;

    @Test
    public void listShouldRespondWithNoContentWhenNothingInDatabase() throws Exception {

        when(service.list()).thenReturn(emptyList());
        ResponseEntity<List<Game>> result = controller.list();
        assertThat(result, is(responseEntityWithStatus(NO_CONTENT)));
    }

    @Test
    public void listShouldRespondWithOkAndResultsFromService() throws Exception {

        Game game1 = new Game().withId("1d");
        Game game2 = new Game().withId("1d2");
        when(service.list()).thenReturn(asList(game1, game2));
        ResponseEntity<List<Game>> result = controller.list();
        assertThat(result, is(allOf(
                responseEntityWithStatus(OK),
                responseEntityThat(containsInAnyOrder(game1, game2)))));
    }

    @Test
    public void listExpressionShouldRespondWithNoContentWhenNothingInDatabase() throws Exception {

        String teamName = "GimmyDaLoot";
        when(service.listOfGamesByTeam(teamName)).thenReturn(emptyList());
        ResponseEntity<List<Game>> result = controller.listOfGamesByTeam(teamName);
        assertThat(result, is(responseEntityWithStatus(NO_CONTENT)));
    }

    @Test
    public void listExpressionShouldRespondWithOkAndResultsFromService() throws Exception {

        String teamName = "GimmyDaLoot";
        Game game1 = new Game().withId("1d");
        Game game2 = new Game().withId("1d2");
        when(service.listOfGamesByTeam(teamName)).thenReturn(asList(game1, game2));
        ResponseEntity<List<Game>> result = controller.listOfGamesByTeam(teamName);
        assertThat(result, is(allOf(
                responseEntityWithStatus(OK),
                responseEntityThat(containsInAnyOrder(game1, game2)))));
    }

    @Test
    public void readShouldReplyWithNotFoundIfNoSuchGame() throws Exception {

        when(service.read("1d2")).thenReturn(Optional.empty());
        ResponseEntity<Game> result = controller.read("1d2");
        assertThat(result, is(responseEntityWithStatus(NOT_FOUND)));
    }

    @Test
    public void readShouldReplyWithGameIfGameExists() throws Exception {

        Game game = new Game().withId("1d2");
        when(service.read("1d2")).thenReturn(Optional.of(game));
        ResponseEntity<Game> result = controller.read("1d2");
        assertThat(result, is(allOf(
                responseEntityWithStatus(OK),
                responseEntityThat(equalTo(game)))));
    }

    @Test
    public void createShouldReplyWithConflictIfGameAlreadyExists() throws Exception {

        Game game = new Game().withId("1d2");
        when(service.create(game)).thenReturn(Optional.empty());
        ResponseEntity<Game> result = controller.create(game);
        assertThat(result, is(responseEntityWithStatus(CONFLICT)));
    }

    @Test
    public void createShouldReplyWithCreatedAndGameData() throws Exception {

        Game game = new Game().withId("1d2");
        when(service.create(game)).thenReturn(Optional.of(game));
        ResponseEntity<Game> result = controller.create(game);
        assertThat(result, is(allOf(
                responseEntityWithStatus(CREATED),
                responseEntityThat(equalTo(game)))));
    }

    @Test
    public void putShouldReplyWithNotFoundIfGameDoesNotExist() throws Exception {

        Game newGameData = new Game().withId("1d2").withHomeTeam("GimmyDaLoot");
        when(service.replace(newGameData)).thenReturn(Optional.empty());
        ResponseEntity<Game> result = controller.put("1d2", new Game().withHomeTeam("GimmyDaLoot"));
        assertThat(result, is(responseEntityWithStatus(NOT_FOUND)));
    }

    @Test
    public void putShouldReplyWithUpdatedGameAndOkIfGameExists() throws Exception {

        Game newGameData = new Game().withId("1d2").withHomeTeam("GimmyDaLoot");
        when(service.replace(newGameData)).thenReturn(Optional.of(newGameData));
        ResponseEntity<Game> result = controller.put("1d2", new Game().withHomeTeam("GimmyDaLoot"));
        assertThat(result, is(allOf(
                responseEntityWithStatus(OK),
                responseEntityThat(equalTo(newGameData)))));
    }

    @Test
    public void patchShouldReplyWithNotFoundIfGameDoesNotExist() throws Exception {

        Game newGameData = new Game().withId("1d2").withHomeTeam("GimmyDaLoot");
        when(service.update(newGameData)).thenReturn(Optional.empty());
        ResponseEntity<Game> result = controller.patch("1d2", new Game().withHomeTeam("GimmyDaLoot"));
        assertThat(result, is(responseEntityWithStatus(NOT_FOUND)));
    }

    @Test
    public void patchShouldReplyWithUpdatedGameAndOkIfGameExists() throws Exception {

        Game newGameData = new Game().withId("1d2").withHomeTeam("GimmyDaLoot");
        when(service.update(newGameData)).thenReturn(Optional.of(newGameData));
        ResponseEntity<Game> result = controller.patch("1d2", new Game().withHomeTeam("GimmyDaLoot"));
        assertThat(result, is(allOf(
                responseEntityWithStatus(OK),
                responseEntityThat(equalTo(newGameData)))));
    }

    @Test
    public void deleteShouldRespondWithNotFoundIfGameDoesNotExist() throws Exception {

        when(service.delete("1d2")).thenReturn(false);
        ResponseEntity<Void> result = controller.delete("1d2");
        assertThat(result, is(responseEntityWithStatus(NOT_FOUND)));
    }

    @Test
    public void deleteShouldRespondWithNoContentIfDeleteSuccessful() throws Exception {

        when(service.delete("1d2")).thenReturn(true);
        ResponseEntity<Void> result = controller.delete("1d2");
        assertThat(result, is(responseEntityWithStatus(NO_CONTENT)));
    }

    @Test
    public void headToHeadShouldRespondWithOkAndResultsFromService() throws Exception {
        String team1 = "team1";
        String team2 = "team2";
        Game game1 = new Game().withId("1d1").withHomeTeam("team1").withAwayTeam("team2")
                .withHomeTeamPts(100.19).withAwayTeamPts(101.20);
        Game game2 = new Game().withId("1d2").withHomeTeam("team2").withAwayTeam("team1")
                .withHomeTeamPts(105.19).withAwayTeamPts(101.20);
        Game game3 = new Game().withId("1d2").withHomeTeam("team2").withAwayTeam("team1")
                .withHomeTeamPts(100.19).withAwayTeamPts(101.20);

        HeadToHead expectedResult = new HeadToHead(team1, team2, asList(game1, game2, game2), 1);

        when(service.listOfGamesByTeams("team1", "team2")).thenReturn(asList(game1, game2, game3));
        ResponseEntity<HeadToHead> result = controller.headToHead(asList(team1, team2));

        assertThat(result.getStatusCode(), is(OK));
        assertThat(result.getBody().record, is(expectedResult.record));
        assertThat(result.getBody().getTeam1(), is(expectedResult.getTeam1()));
        assertThat(result.getBody().getTeam2(), is(expectedResult.getTeam2()));
        assertThat(result.getBody().getGames(), is(containsInAnyOrder(game1, game2, game3)));
    }

    @Test
    public void headToHeadShouldRespondWithNotFoundIfHeadToHeadDoesNotExist() throws Exception {
        String team1 = "team3";
        String team2 = "team2";

        when(service.listOfGamesByTeams("team1", "team2")).thenReturn(emptyList());
        ResponseEntity<HeadToHead> result = controller.headToHead(asList(team1, team2));

        assertThat(result.getStatusCode(), is(NO_CONTENT));
    }

    @Test
    void calculateTeam1Wins() {
        Game game1 = new Game().withId("1d1").withHomeTeam("team1").withAwayTeam("team2")
                .withHomeTeamPts(100.19).withAwayTeamPts(101.20);
        Game game2 = new Game().withId("1d2").withHomeTeam("team2").withAwayTeam("team1")
                .withHomeTeamPts(105.19).withAwayTeamPts(101.20);
        Game game3 = new Game().withId("1d2").withHomeTeam("team2").withAwayTeam("team1")
                .withHomeTeamPts(100.19).withAwayTeamPts(101.20);
        int wins = controller.calculateTeamWins("team1", asList(game1,game2,game3));
        assertThat(wins, is(1));
    }

    @Test
    void calculateTeam2Wins() {
        Game game1 = new Game().withId("1d1").withHomeTeam("team1").withAwayTeam("team2")
                .withHomeTeamPts(100.19).withAwayTeamPts(101.20);
        Game game2 = new Game().withId("1d2").withHomeTeam("team2").withAwayTeam("team1")
                .withHomeTeamPts(105.19).withAwayTeamPts(101.20);
        Game game3 = new Game().withId("1d2").withHomeTeam("team2").withAwayTeam("team1")
                .withHomeTeamPts(100.19).withAwayTeamPts(101.20);
        int wins = controller.calculateTeamWins("team2", asList(game1,game2,game3));
        assertThat(wins, is(2));
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