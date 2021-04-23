package com.dmv.footballheadz.game;

import com.dmv.footballheadz.Application;
import com.dmv.footballheadz.game.impl.Game;
import com.dmv.footballheadz.game.impl.HeadToHead;
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
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GameIntegrationTest {

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void postShouldCreateGameAndRespondWithCreated() throws Exception {
        Game game = new Game().withId(randomUUID().toString());
        ResponseEntity<Game> result = restTemplate.postForEntity(url("/v1/game"), game, Game.class);
        assertThat(result.getStatusCode(), CoreMatchers.is(HttpStatus.CREATED));
        assertThat(result.getBody(), CoreMatchers.is(CoreMatchers.equalTo(game)));
    }

    @Test
    public void postShouldNotCreateGameIfAlreadyExistsAndRespondWithConflict() throws Exception {
        Game game = new Game().withId(randomUUID().toString());
        restTemplate.postForEntity(url("/v1/game"), game, Game.class);
        ResponseEntity<Game> result = restTemplate.postForEntity(url("/v1/game"), game, Game.class);
        assertThat(result.getStatusCode(), CoreMatchers.is(HttpStatus.CONFLICT));
    }

    @Test
    public void postShouldRespondWithBadRequestIfGameNameNotPassed() throws Exception {
        Game game = new Game().withWeek(randomUUID().toString());
        restTemplate.postForEntity(url("/v1/game"), game, Game.class);
        ResponseEntity<Game> result = restTemplate.postForEntity(url("/v1/game"), game, Game.class);
        assertThat(result.getStatusCode(), CoreMatchers.is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void getShouldReturnPreviouslyCreatedGames() throws Exception {
        Game game1 = new Game().withId(randomUUID().toString());
        Game game2 = new Game().withId(randomUUID().toString());
        restTemplate.postForEntity(url("/v1/game"), game1, Game.class);
        restTemplate.postForEntity(url("/v1/game"), game2, Game.class);
        ResponseEntity<Game[]> result = restTemplate.getForEntity(url("/v1/game"), Game[].class);
        assertThat(result.getStatusCode(), CoreMatchers.is(HttpStatus.OK));
        assertThat(Arrays.asList(result.getBody()), CoreMatchers.hasItems(game1, game2));
    }

    @Test
    public void getByNameShouldRespondWithNotFoundForGameThatDoesNotExist() throws Exception {
        String gameName = randomUUID().toString();
        ResponseEntity<Game> result = restTemplate.getForEntity(url("/v1/game/" + gameName), Game.class);
        assertThat(result.getStatusCode(), CoreMatchers.is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void getByNameShouldReturnPreviouslyCreatedGame() throws Exception {
        String gameId = randomUUID().toString();
        Game game = new Game().withId(gameId).withAwayTeam("GimmyDatLoot");
        restTemplate.postForEntity(url("/v1/game"), game, Game.class);
        ResponseEntity<Game> result = restTemplate.getForEntity(url("/v1/game/" + gameId), Game.class);
        assertThat(result.getStatusCode(), CoreMatchers.is(HttpStatus.OK));
        assertThat(result.getBody(), CoreMatchers.is(CoreMatchers.equalTo(game)));
    }

    @Test
    public void headToHeadShouldRespondWithNotFoundForHeadToHeadThatDoesNotExist() throws Exception {
        String team1 = "team1";
        String team2 = "team2";
        ResponseEntity<HeadToHead> result = restTemplate.getForEntity(
                url("/v1/game/head2head?teamName=" + team1 + "," + team2), HeadToHead.class);
        assertThat(result.getStatusCode(), CoreMatchers.is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void headToHeadShouldReturnHeadToHeadGames() throws Exception {
        String gameId = randomUUID().toString();
        String gameId2 = randomUUID().toString();
        String team1 = "team1";
        String team2 = "team2";
        Game game = new Game().withId(gameId).withHomeTeam(team1).withAwayTeam(team2)
                .withHomeTeamPts(110.20).withAwayTeamPts(99.05);
        Game game2 = new Game().withId(gameId2).withHomeTeam(team1).withAwayTeam(team2)
                .withHomeTeamPts(97.24).withAwayTeamPts(89.55);
        restTemplate.postForEntity(url("/v1/game"), game, Game.class);
        restTemplate.postForEntity(url("/v1/game"), game2, Game.class);

        ResponseEntity<HeadToHead> result = restTemplate.getForEntity(
                url("/v1/game/head2head?teamName=" + team1 + "," + team2), HeadToHead.class);
        assertThat(result.getStatusCode(), CoreMatchers.is(HttpStatus.OK));
        assertThat(result.getBody().getGames(), CoreMatchers.is(containsInAnyOrder(game, game2)));
        assertThat(result.getBody().getRecord(), CoreMatchers.is(CoreMatchers.equalTo("team1 : 2 - team2 : 0")));
    }

    @Test
    public void putShouldReplyWithNotFoundForGameThatDoesNotExist() throws Exception {
        String gameName = randomUUID().toString();
        Game game = new Game().withId(gameName);
        RequestEntity<Game> request = new RequestEntity<>(game, HttpMethod.PUT, url("/v1/game/" + gameName));
        ResponseEntity<Game> result = restTemplate.exchange(request, Game.class);
        assertThat(result.getStatusCode(), CoreMatchers.is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void putShouldReplaceExistingGameValues() throws Exception {
        String gameName = randomUUID().toString();
        Game oldGameData = new Game().withId(gameName).withWeek("10");
        Game newGameData = new Game().withId(gameName).withAwayTeam("GimmyDaLoot");
        restTemplate.postForEntity(url("/v1/game"), oldGameData, Game.class);
        RequestEntity<Game> request = new RequestEntity<>(newGameData, HttpMethod.PUT, url("/v1/game/" + gameName));
        ResponseEntity<Game> result = restTemplate.exchange(request, Game.class);
        assertThat(result.getStatusCode(), CoreMatchers.is(HttpStatus.OK));
        assertThat(result.getBody(), CoreMatchers.is(CoreMatchers.equalTo(newGameData)));
    }

    @Test
    public void patchShouldReplyWithNotFoundForGameThatDoesNotExist() throws Exception {
        String gameName = randomUUID().toString();
        Game game = new Game().withId(gameName);
        RequestEntity<Game> request = new RequestEntity<>(game, HttpMethod.PATCH, url("/v1/game/" + gameName));
        ResponseEntity<Game> result = restTemplate.exchange(request, Game.class);
        assertThat(result.getStatusCode(), CoreMatchers.is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void patchShouldAddNewValuesToExistingGameValues() throws Exception {
        String gameName = randomUUID().toString();
        Game oldGameData = new Game().withId(gameName).withWeek("10");
        Game newGameData = new Game().withId(gameName).withAwayTeam("GimmyDaLoot");
        Game expectedNewGameData = new Game().withId(gameName).withAwayTeam("GimmyDaLoot").withWeek("10");
        restTemplate.postForEntity(url("/v1/game"), oldGameData, Game.class);
        RequestEntity<Game> request = new RequestEntity<>(newGameData, HttpMethod.PATCH, url("/v1/game/" + gameName));
        ResponseEntity<Game> result = restTemplate.exchange(request, Game.class);
        assertThat(result.getStatusCode(), CoreMatchers.is(HttpStatus.OK));
        assertThat(result.getBody(), CoreMatchers.is(CoreMatchers.equalTo(expectedNewGameData)));
    }

    @Test
    public void deleteShouldReturnNotFoundWhenGameDoesNotExist() throws Exception {
        String gameName = randomUUID().toString();
        RequestEntity<Void> request = new RequestEntity<>(HttpMethod.DELETE, url("/v1/game/" + gameName));
        ResponseEntity<Void> result = restTemplate.exchange(request, Void.class);
        assertThat(result.getStatusCode(), CoreMatchers.is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void deleteShouldRemoveExistingGameAndRespondWithNoContent() throws Exception {
        String gameName = randomUUID().toString();
        Game game = new Game().withId(gameName);
        restTemplate.postForEntity(url("/v1/game"), game, Game.class);
        RequestEntity<Void> request = new RequestEntity<>(HttpMethod.DELETE, url("/v1/game/" + gameName));
        ResponseEntity<Void> result = restTemplate.exchange(request, Void.class);
        assertThat(result.getStatusCode(), CoreMatchers.is(HttpStatus.NO_CONTENT));
    }

    private URI url(String url) {

        return URI.create("http://localhost:" + port + url);
    }
}
