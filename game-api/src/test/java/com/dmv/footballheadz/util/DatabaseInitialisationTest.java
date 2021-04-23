package com.dmv.footballheadz.util;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.*;
import com.dmv.footballheadz.game.impl.Game;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.amazonaws.services.dynamodbv2.model.TableStatus.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class DatabaseInitialisationTest {

    @Mock
    private DynamoDBMapper dbMapper;

    @Mock
    private AmazonDynamoDB dynamoDB;

    @InjectMocks
    private DatabaseInitialisation databaseInitialisation;

    private final String tableName = "Season";
    private final CreateTableRequest createTableRequest = new CreateTableRequest().withTableName(tableName);

    @BeforeEach
    public void setUp() throws Exception {

        when(dbMapper.generateCreateTableRequest(Game.class)).thenReturn(createTableRequest);
        when(dynamoDB.createTable(createTableRequest))
                .thenReturn(new CreateTableResult().withTableDescription(tableDescriptionWithStatus(CREATING)));
    }

    @Test
    public void shouldNotCreateTableIfAlreadyExists() throws Exception {

        when(dynamoDB.describeTable(tableName))
                .thenReturn(new DescribeTableResult().withTable(tableDescriptionWithStatus(ACTIVE)));
        databaseInitialisation.onApplicationEvent(null);
        verify(dynamoDB, never()).createTable(any(CreateTableRequest.class));
    }

    @Test
    public void shouldNotCreateTableIfTableCreationInProgress() throws Exception {

        when(dynamoDB.describeTable(tableName))
                .thenReturn(new DescribeTableResult().withTable(tableDescriptionWithStatus(CREATING)));
        databaseInitialisation.onApplicationEvent(null);
        verify(dynamoDB, never()).createTable(any(CreateTableRequest.class));
    }

    @Test
    public void shouldNotCreateTableIfTableDeletionInProgress() throws Exception {

        when(dynamoDB.describeTable(tableName))
                .thenReturn(new DescribeTableResult().withTable(tableDescriptionWithStatus(DELETING)));
        databaseInitialisation.onApplicationEvent(null);
        verify(dynamoDB, never()).createTable(any(CreateTableRequest.class));
    }

    @Test
    public void shouldNotCreateTableIfTableUpdateInProgress() throws Exception {

        when(dynamoDB.describeTable(tableName))
                .thenReturn(new DescribeTableResult().withTable(tableDescriptionWithStatus(UPDATING)));
        databaseInitialisation.onApplicationEvent(null);
        verify(dynamoDB, never()).createTable(any(CreateTableRequest.class));
    }

    @Test
    public void shouldCreateTableIfTableDoesNotExist() throws Exception {

        when(dynamoDB.describeTable(tableName)).thenThrow(new ResourceNotFoundException("Simulated failure"));
        databaseInitialisation.onApplicationEvent(null);
        verify(dynamoDB).createTable(createTableRequest);
    }

    private TableDescription tableDescriptionWithStatus(TableStatus status) {

        return new TableDescription().withTableStatus(status).withTableName(tableName);
    }
}