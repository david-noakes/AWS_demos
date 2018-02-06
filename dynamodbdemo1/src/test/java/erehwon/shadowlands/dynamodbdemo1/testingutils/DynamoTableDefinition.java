package  erehwon.shadowlands.dynamodbdemo1.testingutils;

/**
 * Project qgc-idm-application
 * Created on 08 Jan 2018.
 */
public class DynamoTableDefinition {
    private String tableName;
    private Long readCapacity = 10L;
    private Long writeCapacity = 5L;
    private String keyType = "S";
    private String key = "record_id";
    private String sortKeyName;
    private String indexKeyName;
    private String indexName;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public DynamoTableDefinition withTableName(String name) {
        this.tableName = name;
        return this;
    }

    public Long getReadCapacity() {
        return readCapacity;
    }

    public void setReadCapacity(Long readCapacity) {
        this.readCapacity = readCapacity;
    }

    public DynamoTableDefinition withReadCapacity(Long readCapacity) {
        setReadCapacity(readCapacity);
        return this;
    }

    public Long getWriteCapacity() {
        return writeCapacity;
    }

    public void setWriteCapacity(Long writeCapacity) {
        this.writeCapacity = writeCapacity;
    }

    public DynamoTableDefinition withWriteCapacity(Long writeCapacity) {
        setWriteCapacity(writeCapacity);
        return this;
    }

    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    public DynamoTableDefinition withKeyType(String keyType) {
        setKeyType(keyType);
        return this;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public DynamoTableDefinition withKey(String key) {
        setKey(key);
        return this;
    }

    public String getSortKeyName() {
        return sortKeyName;
    }

    public void setSortKeyName(String sortKeyName) {
        this.sortKeyName = sortKeyName;
    }

    public DynamoTableDefinition withSortKeyName(String sortKeyName) {
        setSortKeyName(sortKeyName);
        return this;
    }

    public String getIndexKeyName() {
        return indexKeyName;
    }

    public void setIndexKeyName(String indexKeyName) {
        this.indexKeyName = indexKeyName;
    }

    public DynamoTableDefinition withIndexKeyName(String indexKeyName) {
        this.indexKeyName = indexKeyName;
        return this;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public DynamoTableDefinition withIndexName(String indexName) {
        setIndexName(indexName);
        return this;
    }
}
