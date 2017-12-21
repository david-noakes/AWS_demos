package erehwon.shadowlands.dynamodbdemo1.model;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DynamoDBTable(tableName = "Movies")
@DynamoDBDocument
public class MovieRecord {
    private Integer year;
    private String title;
    private InfoRecord info;
    
    public MovieRecord() {
    	year = new Integer(0);
    	title = "";
    	info = new InfoRecord();
    }
    public MovieRecord(int year, String title, InfoRecord info) {
    	this.year = new Integer(year);
    	this.title = title;
    	this.info = info;
    }

    public MovieRecord(int year, String title, HashMap<String, Object> infoMap){
        this.year = new Integer(year);
        this.title = title;
        this.info = new InfoRecord(infoMap);
    }

    public MovieRecord(ObjectNode jObj) {
        this.year = new Integer(jObj.get("year").asInt());
        this.title = jObj.get("title").asText();
        ObjectNode jInfo = (ObjectNode) jObj.get("info");
        this.info = new InfoRecord(jInfo);
    }

    @DynamoDBHashKey(attributeName = "year")
    @JsonProperty("year")
    public Integer getYear() {
        return year;
    }

    @DynamoDBHashKey(attributeName = "year")
    @JsonProperty("year")
    public void setYear(Integer year) {
        this.year = year;
    }

    @DynamoDBRangeKey (attributeName = "title")
    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @DynamoDBRangeKey (attributeName = "title")
    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @DynamoDBAttribute(attributeName = "info")
    @JsonProperty("info")
    public InfoRecord getInfo() {
        return info;
    }

    @DynamoDBAttribute(attributeName = "info")
    @JsonProperty("info")
    public void setInfo(InfoRecord info) {
        this.info = info;
    }
}
