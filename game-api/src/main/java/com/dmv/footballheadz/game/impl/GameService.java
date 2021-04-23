package com.dmv.footballheadz.game.impl;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.dmv.footballheadz.game.IService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.amazonaws.util.StringUtils.isNullOrEmpty;

@Service
public class GameService implements IService<Game> {
    
    private final Logger log = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private GameRepository repository;

    @Override
    public Optional<Game> read(String id) {
        log.trace("Entering read() with {}", id);
        return repository.read(id);
    }

    @Override
    public Optional<Game> create(Game game) {
        log.trace("Entering create() with {}", game);
        repository.save(game);
        return Optional.of(game);
    }

    @Override
    public Optional<Game> replace(Game newData) {
        log.trace("Entering replace() with {}", newData);
        Optional<Game> existingGame = repository.read(newData.getId());
        if (!existingGame.isPresent()) {
            log.warn("Game {} not found", newData.getId());
            return Optional.empty();
        }
        Game game = existingGame.get();
        game.setYear(newData.getYear());
        game.setWeek(newData.getWeek());
        game.setHomeTeam(newData.getHomeTeam());
        game.setAwayTeam(newData.getAwayTeam());
        game.setHomeTeamPts(newData.getHomeTeamPts());
        game.setAwayTeamPts(newData.getAwayTeamPts());
        repository.save(game);
        return Optional.of(game);
    }

    @Override
    public Optional<Game> update(Game newData) {
        log.trace("Entering update() with {}", newData);
        Optional<Game> existingCustomer = repository.read(newData.getId());
        if (!existingCustomer.isPresent()) {
            log.warn("Customer {} not found", newData.getId());
            return Optional.empty();
        }
        Game game = existingCustomer.get();
        if (!isNullOrEmpty(newData.getYear())){
            game.setYear(newData.getYear());
        }
        if (!isNullOrEmpty(newData.getWeek())){
            game.setWeek(newData.getWeek());
        }
        if (!isNullOrEmpty(newData.getHomeTeam())){
            game.setHomeTeam(newData.getHomeTeam());
        }
        if (!isNullOrEmpty(newData.getAwayTeam())){
            game.setAwayTeam(newData.getAwayTeam());
        }
        if (null != newData.getHomeTeamPts()){
            game.setHomeTeamPts(newData.getHomeTeamPts());
        }
        if (null != newData.getAwayTeamPts()){
            game.setAwayTeamPts(newData.getAwayTeamPts());
        }
        repository.save(game);
        return Optional.of(game);
    }

    @Override
    public boolean delete(String key) {
        log.trace("Entering delete() with {}", key);
        if (!repository.read(key).isPresent()) {
            log.warn("Customer {} not found", key);
            return false;
        }
        repository.delete(key);
        return true;
    }

    @Override
    public List<Game> list() {
        log.trace("Entering list()");
        return repository.readAll();
    }

    @Override
    public List<Game> listOfGamesByTeam(String key) {
        log.trace("Entering listExpression()");
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":val", new AttributeValue().withS(key));

        DynamoDBScanExpression dynamoDBScanExpression = new DynamoDBScanExpression()
                .withFilterExpression("HomeTeam = :val or AwayTeam = :val")
                .withExpressionAttributeValues(eav);

        return repository.readExpression(dynamoDBScanExpression);
    }

    @Override
    public List<Game> listOfGamesByTeams(String key1, String key2) {
        log.trace("Entering listOfGamesByTeams()");
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":val", new AttributeValue().withS(key1));
        eav.put(":val2", new AttributeValue().withS(key2));

        DynamoDBScanExpression dynamoDBScanExpression = new DynamoDBScanExpression()
                .withFilterExpression("(HomeTeam = :val or AwayTeam = :val) and (HomeTeam = :val2 or AwayTeam = :val2)")
                .withExpressionAttributeValues(eav);

        return repository.readExpression(dynamoDBScanExpression);
    }
}
