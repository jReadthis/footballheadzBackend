package com.dmv.footballheadz.game;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;

import java.util.List;
import java.util.Optional;

public interface IRepository<T> {

    List<T> readExpression(DynamoDBScanExpression dynamoDBScanExpression);

    List<T> readAll();

    Optional<T> read(String key);

    void save(T t);

    void delete(String key);

}
