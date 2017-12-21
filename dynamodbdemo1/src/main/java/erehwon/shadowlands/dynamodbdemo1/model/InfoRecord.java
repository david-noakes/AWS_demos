package erehwon.shadowlands.dynamodbdemo1.model;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import erehwon.shadowlands.dynamodbdemo1.converter.ZonedDateTimeConverter;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.fasterxml.jackson.annotation.JsonProperty;

@DynamoDBDocument
public class InfoRecord {
    private List<String> directors;
    private String releaseDate;
    private ZonedDateTime releaseDateTime;
    private BigDecimal rating;
    private List<String> genres;
    private String imageUrl;
    private String plot;
    private BigDecimal rank;
    private BigDecimal runningTimeSecs;
    private List<String> actors;

    public InfoRecord() {
    	directors = new ArrayList<String>();
//    	releaseDateTime = ZonedDateTime.now();
    	releaseDateTime = ZonedDateTime.of(1900, 00,00,00,00,00,00, ZoneId.of("Australia/Brisbane"));
    	releaseDate = "";
    	rating = BigDecimal.ZERO;
    	genres = new ArrayList<String>();
    	imageUrl = "";
    	plot = "";
    	rank = BigDecimal.ZERO;
    	runningTimeSecs  = BigDecimal.ZERO;
    	actors = new ArrayList<String>();
    }

    public InfoRecord(HashMap<String, Object> infoMap) {
        infoMap.entrySet().stream().forEach(item ->
                System.out.println(item.getKey() + ": " + item.getValue())
        );
        String std = infoMap.get("directors").toString();
        String str[] = std.split(",");
        directors = Arrays.asList(str);
//    	releaseDateTime = ZonedDateTime.now();
        releaseDateTime = ZonedDateTime.of(1900, 00,00,00,00,00,00, ZoneId.of("Australia/Brisbane"));
        releaseDate = infoMap.get("release_date").toString();
        rating = new BigDecimal(infoMap.get("rating").toString()); //BigDecimal.ZERO;
        genres = new ArrayList<String>();
        imageUrl = "";
        plot = "";
        rank = BigDecimal.ZERO;
        runningTimeSecs  = BigDecimal.ZERO;
        actors = new ArrayList<String>();

    }

    public InfoRecord(ObjectNode jObj) {
        JsonNode jNode;
        ZonedDateTimeConverter zdtc = new ZonedDateTimeConverter();
        ArrayNode jArray = (ArrayNode) jObj.get("directors");
        String s;
        String str[];
        if (jArray != null) {
            s = jArray.toString();
            str = s.split(",");
            directors = Arrays.asList(str);
        }
        jNode = jObj.get("release_date");
        if (jNode != null && !jNode.isMissingNode()) {
            s = jObj.get("release_date").asText();
            if (s != null && s.trim().length() > 4) {
                releaseDateTime = zdtc.unconvert(s);
            }
        }
        jNode = jObj.get("rating");
        if (jNode != null) {
            rating = jNode.decimalValue();
        }
        jArray = (ArrayNode) jObj.get("genres");
        if (jArray != null) {
            s = jArray.toString();
            str = s.split(",");
            genres = Arrays.asList(str);
        }
        jNode = jObj.get("image_url");
        if (jNode != null && !jNode.isMissingNode()) {
            imageUrl = jNode.asText();
        }
        jNode = jObj.get("plot");
        if (jNode != null && !jNode.isMissingNode()) {
            plot = jNode.asText();
        }
        jNode = jObj.get("rank");
        if (jNode != null) {
            rank = jNode.decimalValue();
        }
        jNode = jObj.get("running_time_secs");
        if (jNode != null) {
            runningTimeSecs = jNode.decimalValue();
        }
        jArray = (ArrayNode) jObj.get("actors");
        if (jArray != null) {
            s = jArray.toString();
            str = s.split(",");
            actors = Arrays.asList(str);
        }

    }

    @DynamoDBAttribute(attributeName = "directors")
    @JsonProperty("directors")
	public List<String> getDirectors() {
		return directors;
	}

    @DynamoDBAttribute(attributeName = "directors")
    @JsonProperty("directors")
	public void setDirectors(List<String> directors) {
		this.directors = directors;
	}

    @DynamoDBAttribute(attributeName = "release_date_str")
    @JsonProperty("release_date")
	public String getReleaseDate() {
		return releaseDate;
	}

    @DynamoDBAttribute(attributeName = "release_date_str")
    @JsonProperty("release_date")
	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}

    @DynamoDBTypeConverted(converter = ZonedDateTimeConverter.class)
	@DynamoDBAttribute(attributeName = "release_date")
	@JsonProperty("release_date_time")
	public ZonedDateTime getReleaseDateTime() {
		if (releaseDateTime != null) {
            return releaseDateTime;
        }
        else {
		    return ZonedDateTime.of(1900, 00,00,00,00,00,00, ZoneId.of("Australia/Brisbane"));
        }
	}

    @DynamoDBTypeConverted(converter = ZonedDateTimeConverter.class)
	@DynamoDBAttribute(attributeName = "release_date")
	@JsonProperty("release_date")
	public void setReleaseDateTime(ZonedDateTime releaseDateTime) {
		this.releaseDateTime = releaseDateTime;
	}

	@DynamoDBAttribute(attributeName = "rating")
    @JsonProperty("rating")
	public BigDecimal getRating() {
		return rating;
	}

    @DynamoDBAttribute(attributeName = "rating")
    @JsonProperty("rating")
	public void setRating(BigDecimal rating) {
		this.rating = rating;
	}

    @DynamoDBAttribute(attributeName = "genres")
    @JsonProperty("genres")
	public List<String> getGenres() {
		return genres;
	}

    @DynamoDBAttribute(attributeName = "genres")
    @JsonProperty("genres")
	public void setGenres(List<String> genres) {
		this.genres = genres;
	}

    @DynamoDBAttribute(attributeName = "image_url")
    @JsonProperty("image_url")
	public String getImageUrl() {
		return imageUrl;
	}

    @DynamoDBAttribute(attributeName = "image_url")
    @JsonProperty("image_url")
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

    @DynamoDBAttribute(attributeName = "plot")
    @JsonProperty("plot")
	public String getPlot() {
		return plot;
	}

    @DynamoDBAttribute(attributeName = "plot")
    @JsonProperty("plot")
	public void setPlot(String plot) {
		this.plot = plot;
	}

    @DynamoDBAttribute(attributeName = "rank")
    @JsonProperty("rank")
	public BigDecimal getRank() {
		return rank;
	}

    @DynamoDBAttribute(attributeName = "rank")
    @JsonProperty("rank")
	public void setRank(BigDecimal rank) {
		this.rank = rank;
	}

    @DynamoDBAttribute(attributeName = "running_time_secs")
    @JsonProperty("running_time_secs")
	public BigDecimal getRunningTimeSecs() {
		return runningTimeSecs;
	}

    @DynamoDBAttribute(attributeName = "running_time_secs")
    @JsonProperty("running_time_secs")
	public void setRunningTimeSecs(BigDecimal runningTimeSecs) {
		this.runningTimeSecs = runningTimeSecs;
	}

    @DynamoDBAttribute(attributeName = "actors")
    @JsonProperty("actors")
	public List<String> getActors() {
		return actors;
	}

	public void setActors(List<String> actors) {
		this.actors = actors;
	}
    
}
