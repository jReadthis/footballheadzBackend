package com.dmv.footballheadz.manager.impl;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedList;
import com.dmv.footballheadz.manager.IRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ManagerRepository implements IRepository<Manager> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private DynamoDBMapper dbMapper;

    @Override
    public List<Manager> readExpression(DynamoDBScanExpression dynamoDBScanExpression) {
        log.trace("Entering readQuery()");
        PaginatedList<Manager> results = dbMapper.scan(Manager.class, dynamoDBScanExpression);
        results.loadAllResults();
        return results;
    }

    @Override
    public List<Manager> readAll() {
        log.trace("Entering readAll()");
        PaginatedList<Manager> results = dbMapper.scan(Manager.class, new DynamoDBScanExpression());
        results.loadAllResults();
        return results;
    }

    @Override
    public Optional<Manager> read(String key) {
        log.trace("Entering read() with {}", key);
        return Optional.ofNullable(dbMapper.load(Manager.class, key));
    }

    @Override
    public void save(Manager manager) {
        log.trace("Entering save() with {}", manager);
        dbMapper.save(manager);
    }

    @Override
    public void delete(String key) {
        dbMapper.delete(new Manager().withManagerName(key), new DynamoDBMapperConfig(DynamoDBMapperConfig.SaveBehavior.CLOBBER));
    }
}
