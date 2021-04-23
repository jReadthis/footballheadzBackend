package com.dmv.footballheadz.game.impl;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedList;
import com.dmv.footballheadz.game.IRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class GameRepository implements IRepository<Game> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private DynamoDBMapper dbMapper;

    @Override
    public List<Game> readExpression(DynamoDBScanExpression dynamoDBScanExpression) {
        log.trace("Entering readQuery()");
        PaginatedList<Game> results = dbMapper.scan(Game.class, dynamoDBScanExpression);
        results.loadAllResults();
        return results;
    }

    @Override
    public List<Game> readAll() {
        log.trace("Entering readAll()");
        PaginatedList<Game> results = dbMapper.scan(Game.class, new DynamoDBScanExpression());
        results.loadAllResults();
        return results;
    }

    @Override
    public Optional<Game> read(String key) {
        log.trace("Entering read() with {}", key);
        return Optional.ofNullable(dbMapper.load(Game.class, key));
    }

    @Override
    public void save(Game game) {
        log.trace("Entering save() with {}", game);
        dbMapper.save(game);
    }

    @Override
    public void delete(String key) {
        dbMapper.delete(new Game().withId(key), new DynamoDBMapperConfig(DynamoDBMapperConfig.SaveBehavior.CLOBBER));

    }
}
