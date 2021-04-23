package com.dmv.footballheadz.manager.impl;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.dmv.footballheadz.manager.IService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ManagerService implements IService<Manager> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ManagerRepository repository;

    @Override
    public Optional<Manager> read(String id) {
        log.trace("Entering read() with {}", id);
        return repository.read(id);
    }

    @Override
    public Optional<Manager> create(Manager manager) {
        log.trace("Entering create() with {}", manager);
        if (repository.read(manager.getManagerName()).isPresent()) {
            log.warn("Manager {} already exists", manager.getManagerName());
            return Optional.empty();
        }
        repository.save(manager);
        return Optional.of(manager);
    }

    @Override
    public Optional<Manager> replace(Manager newData) {
        log.trace("Entering replace() with {}", newData);
        Optional<Manager> existingManager = repository.read(newData.getManagerName());
        if (!existingManager.isPresent()) {
            log.warn("Manager {} not found", newData.getManagerName());
            return Optional.empty();
        }
        Manager manager = existingManager.get();
        manager.setTeams(newData.getTeams());
        manager.setActiveDate(newData.getActiveDate());
        manager.setActiveStatus(newData.getActiveStatus());
        repository.save(manager);
        return Optional.of(manager);
    }

    @Override
    public Optional<Manager> update(Manager newManagerData) {
        log.trace("Entering update() with {}", newManagerData);
        Optional<Manager> existingManager = repository.read(newManagerData.getManagerName());
        if (!existingManager.isPresent()) {
            log.warn("Manager {} not found", newManagerData.getManagerName());
            return Optional.empty();
        }
        Manager manager = existingManager.get();
        if (newManagerData.getTeams() != null ) {
            manager.setTeams(newManagerData.getTeams());
        }
        if (newManagerData.getActiveDate() != null) {
            manager.setActiveDate(newManagerData.getActiveDate());
        }
        if (newManagerData.getActiveStatus() != null) {
            manager.setActiveStatus(newManagerData.getActiveStatus());
        }
        repository.save(manager);
        return Optional.of(manager);
    }

    @Override
    public boolean delete(String key) {
        log.trace("Entering delete() with {}", key);
        if (!repository.read(key).isPresent()) {
            log.warn("Manager {} not found", key);
            return false;
        }
        repository.delete(key);
        return true;
    }

    @Override
    public List<Manager> list() {
        log.trace("Entering list()");
        return repository.readAll();
    }

    @Override
    public List<Manager> retrieveTeams(String key) {
        log.trace("Entering retrieveTeams()");
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":val", new AttributeValue().withS(key));

        DynamoDBScanExpression dynamoDBScanExpression = new DynamoDBScanExpression()
                .withFilterExpression("Manager = :val")
                .withExpressionAttributeValues(eav);

        return repository.readExpression(dynamoDBScanExpression);
    }
}
