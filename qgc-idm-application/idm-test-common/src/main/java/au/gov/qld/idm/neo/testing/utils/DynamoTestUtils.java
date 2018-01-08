package au.gov.qld.idm.neo.testing.utils;

import au.gov.qld.idm.neo.model.BaseDynamoModel;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Project notifications-v2_application
 * Created on 16 Nov 2017.
 */
public abstract class DynamoTestUtils {

    private static final Logger LOG = LoggerFactory.getLogger(DynamoTestUtils.class);
    protected static String dynamoEndpointUrl;
    protected static String dynamoRegion = Regions.AP_SOUTHEAST_2.getName();

    private static AmazonDynamoDB client;
    private static DynamoDB dynamoDB;
    private static DynamoDBMapper dynamoDBMapper;

    protected static void setupDynamoEnvironment(List<DynamoTableDefinition> tableDefinitions) {

        instantiateDynamoEndpoint();
        setupDynamoClient();
        setupTables(tableDefinitions);
    }

    protected void purgeTables(List<DynamoTableDefinition> tableDefinitions) {
        tableDefinitions.forEach(table -> purgeTableContents(table.getTableName(), table.getKey()));
    }


    public Long recordCount(String tableName) {
        Table table = dynamoDB.getTable(tableName);
        return table.describe().getItemCount();
    }

    public List<Map<String, AttributeValue>> readRecordById(String tableName, String idName, String id) {

        Map<String, Condition> conditions = ImmutableMap.of(
                idName, new Condition().withComparisonOperator(ComparisonOperator.EQ)
                        .withAttributeValueList(new AttributeValue(id))
        );
        QueryRequest request = new QueryRequest()
                .withTableName(tableName)
                .withKeyConditions(conditions);
        QueryResult queryResult = client.query(request);
        return queryResult.getItems();
    }

    public List<Map<String, AttributeValue>> readTable(String tableName) {

        ScanRequest request = new ScanRequest()
                .withTableName(tableName);
        ScanResult result = client.scan(request);
        return result.getItems();
    }

    public void purgeTableContents(String tableName, String key) {
        Table table = dynamoDB.getTable(tableName);

        ScanRequest scanRequest = new ScanRequest().withTableName(tableName);
        ScanResult result = client.scan(scanRequest);
        StreamUtils.asStream(result.getItems()).forEach(item -> {
            DeleteItemSpec deleteItemSpec = new DeleteItemSpec().withPrimaryKey(key, item.get(key).getS());
            table.deleteItem(deleteItemSpec);
        });
    }

    public <T extends BaseDynamoModel> T generateRecord(Class<T> clazz, String request, Object... params) {
        String message = request;
        if (params != null) {
            message = String.format(request, params);
        }
        return JsonUtils.INSTANCE.fromJsonString(message, clazz);
    }

    public <T extends BaseDynamoModel> String createDynamoRecord(T dynamoRecord) {
        dynamoDBMapper.save(dynamoRecord);
        return dynamoRecord.getRecordId();
    }

    public <T extends BaseDynamoModel> T retrieveByRecordId(Class<T> clazz, String recordId) {
        return dynamoDBMapper.load(clazz, recordId);
    }

    private static void deleteTable(String tableName) {
        Table table = dynamoDB.getTable(tableName);
        try {
            LOG.info("Issuing DeleteTable request for " + tableName);
            table.delete();

            LOG.info("Waiting for " + tableName + " to be deleted...this may take a while...");
            table.waitForDelete();

        } catch (ResourceNotFoundException rnfe) {
            // Ignore - doesn't matter if it doesn't already exist
        } catch (Exception e) {
            LOG.error("DeleteTable request failed for " + tableName, e);
        }
    }

    private static void setupTables(List<DynamoTableDefinition> tables) {
        StreamUtils.asStream(tables).map(DynamoTableDefinition::getTableName).forEach(DynamoTestUtils::deleteTable);
        StreamUtils.asStream(tables).forEach(table -> {
            createTable(table.getTableName(), table.getReadCapacity(), table.getWriteCapacity(), table.getKeyType(),
                    table.getKey(), table.getSortKeyName(), table.getIndexKeyName(), table.getIndexName());

        });
    }

    private static void createTable(String tableName, long readCapacityUnits, long writeCapacityUnits, String keyType,
                                    String partitionKeyName, String sortKeyName, String indexKeyName, String indexName) {

        ArrayList<KeySchemaElement> keySchema = new ArrayList<>();
        ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<>();

        try {
            ProvisionedThroughput throughput = new ProvisionedThroughput()
                    .withReadCapacityUnits(readCapacityUnits)
                    .withWriteCapacityUnits(writeCapacityUnits);

            constructKeySchemaElements(keySchema, KeyType.HASH, partitionKeyName);
            constructKeySchemaElements(keySchema, KeyType.RANGE, sortKeyName);

            constructAttributeDefinitions(attributeDefinitions, keyType, partitionKeyName, sortKeyName, indexKeyName);

            CreateTableRequest request = new CreateTableRequest()
                    .withTableName(tableName)
                    .withProvisionedThroughput(throughput)
                    .withKeySchema(keySchema)
                    .withAttributeDefinitions(attributeDefinitions);

            if (indexKeyName != null) {
                GlobalSecondaryIndex index = new GlobalSecondaryIndex().withIndexName(indexName)
                        .withProvisionedThroughput(throughput)
                        .withKeySchema(new KeySchemaElement().withAttributeName(indexKeyName).withKeyType(KeyType.HASH))
                        .withProjection(new Projection().withProjectionType("ALL"));

                request.setGlobalSecondaryIndexes(Collections.singletonList(index));
            }

            LOG.info("Issuing CreateTable request for " + tableName);
            Table table = dynamoDB.createTable(request);

            LOG.info("Waiting for " + tableName + " to be created...this may take a while...");
            table.waitForActive();

        } catch (Exception e) {
            LOG.error("CreateTable request failed for " + tableName, e);
        }
    }

    private static void constructKeySchemaElements(List<KeySchemaElement> keySchema, KeyType keyType, String... keyNames) {
        for (String name : keyNames) {
            if (name != null) {
                keySchema.add(new KeySchemaElement().withAttributeName(name).withKeyType(keyType));
            }
        }
    }

    private static void constructAttributeDefinitions(List<AttributeDefinition> attributes, String type, String... keyNames) {
        for (String name : keyNames) {
            if (name != null) {
                attributes.add(new AttributeDefinition().withAttributeName(name).withAttributeType(type));
            }
        }
    }

    private static void instantiateDynamoEndpoint() {
        String dynamoEndpoint = System.getProperty("dynamo.endpoint");
        if (StringUtils.isBlank(dynamoEndpoint)) {
            dynamoEndpoint = "http://localhost:8000";
        }
        dynamoEndpointUrl = dynamoEndpoint;
        LOG.info("Dynamo endpoint " + dynamoEndpointUrl);
    }

    private static void setupDynamoClient() {
        client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(dynamoEndpointUrl, dynamoRegion))
                .withClientConfiguration(new ClientConfiguration().withMaxErrorRetry(0))
                .build();
        dynamoDB = new DynamoDB(client);
        dynamoDBMapper = new DynamoDBMapper(client);
    }
}
