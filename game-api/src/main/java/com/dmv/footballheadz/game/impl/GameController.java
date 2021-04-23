package com.dmv.footballheadz.game.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@CrossOrigin
@RestController
@RequestMapping("/v1")
public class GameController {
    
    private final Logger log = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private GameService service;

    @RequestMapping(path = "/game", method = RequestMethod.GET)
    public ResponseEntity<List<Game>> list() {
        log.trace("Entering list()");
        List<Game> games = service.list();
        if (games.isEmpty()) {
            return new ResponseEntity<>(NO_CONTENT);
        }
        return new ResponseEntity<>(games, OK);
    }

    @RequestMapping(path = "/game/", method = RequestMethod.GET)
    public ResponseEntity<List<Game>> listOfGamesByTeam(@RequestParam(value="teamName") String teamName) {
        log.trace("Entering listOfYear() for {}", teamName);
        List<Game> games = service.listOfGamesByTeam(teamName);
        if (games.isEmpty()) {
            return new ResponseEntity<>(NO_CONTENT);
        }
        return new ResponseEntity<>(games, OK);
    }

    @RequestMapping(path = "/game/head2head", method = RequestMethod.GET)
    public ResponseEntity<HeadToHead> headToHead(
            @RequestParam(value="teamName") List<String> teams) {
        String team1 = teams.get(0);
        String team2 = teams.get(1);
        log.trace("Entering listOfYear() for {} and {}", team1, team2 );
        List<Game> games = service.listOfGamesByTeams(team1,team2);
        if (games.isEmpty()) {
            return new ResponseEntity<>(NO_CONTENT);
        }

        HeadToHead headToHead = new HeadToHead(team1,team2, games, calculateTeamWins(team1, games));
        log.info(headToHead.getRecord());
        return new ResponseEntity<>(headToHead, null,OK);
    }

    public int calculateTeamWins(String team1, List<Game> games) {
        List<Game> teamWins = games.stream()
                .filter(game ->
                        (team1.equals(game.getHomeTeam()) &&
                                game.getHomeTeamPts() > game.getAwayTeamPts()) ||
                                (team1.equals(game.getAwayTeam()) &&
                                        game.getAwayTeamPts() > game.getHomeTeamPts())
                ).collect(Collectors.toList());
        return teamWins.size();
    }

    @RequestMapping(path = "/game/{id}", method = RequestMethod.GET)
    public ResponseEntity<Game> read(@PathVariable String id) {
        log.trace("Entering read() with {}", id);
        return service.read(id)
                .map(game -> new ResponseEntity<>(game, OK))
                .orElse(new ResponseEntity<>(NOT_FOUND));
    }

    @RequestMapping(path = "/game", method = RequestMethod.POST)
    public ResponseEntity<Game> create(@RequestBody @Valid Game game) {
        log.trace("Entering create() with {}", game);
        return service.create(game)
                .map(newGameData -> new ResponseEntity<>(newGameData, CREATED))
                .orElse(new ResponseEntity<>(CONFLICT));
    }

    @RequestMapping(path = "/game/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Game> put(@PathVariable String id, @RequestBody Game game) {
        log.trace("Entering put() with {}, {}", id, game);
        return service.replace(game.withId(id))
                .map(newGameData -> new ResponseEntity<>(newGameData, OK))
                .orElse(new ResponseEntity<>(NOT_FOUND));
    }

    @RequestMapping(path = "/game/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<Game> patch(@PathVariable String id, @RequestBody Game game) {
        log.trace("Entering patch() with {}, {}", id, game);
        return service.update(game.withId(id))
                .map(newGameData -> new ResponseEntity<>(newGameData, OK))
                .orElse(new ResponseEntity<>(NOT_FOUND));
    }

    @RequestMapping(path = "/game/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable String id) {
        log.trace("Entering delete() with {}", id);
        return service.delete(id) ?
                new ResponseEntity<>(NO_CONTENT) :
                new ResponseEntity<>(NOT_FOUND);
    }
}
