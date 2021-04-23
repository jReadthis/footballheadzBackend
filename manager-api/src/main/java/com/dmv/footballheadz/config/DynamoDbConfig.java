package com.dmv.footballheadz.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.StringUtils;

@Configuration
@PropertySource(value = "classpath:application.properties")
public class DynamoDbConfig {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Value("${amazon.dynamodb.endpoint}")
    private String amazonDynamoDBEndpoint;

    @Value("${amazon.aws.accesskey}")
    private String amazonAWSAccessKey;

    @Value("${amazon.aws.secretkey}")
    private String amazonAWSSecretKey;

    @Bean
    public AmazonDynamoDB amazaonDynamoDB() {
        log.trace("Entering amazonDynamoDb()");
        AmazonDynamoDB client = new AmazonDynamoDBClient();
        log.info("Using DynamoDb endpoint {}", amazonDynamoDBEndpoint);
        if (!StringUtils.hasLength(amazonDynamoDBEndpoint)){
            client.setEndpoint(amazonDynamoDBEndpoint);
        }
        return client;
    }

    @Bean
    public AWSCredentials amazonAWSCredentials() {
        return new BasicAWSCredentials(amazonAWSAccessKey, amazonAWSSecretKey);
    }

    @Bean
    public DynamoDBMapper dynamoDbMapper(AmazonDynamoDB amazonDynamoDB) {
        log.trace("Entering dynamoDbMapper()");
        return new DynamoDBMapper(amazonDynamoDB);
    }
}
