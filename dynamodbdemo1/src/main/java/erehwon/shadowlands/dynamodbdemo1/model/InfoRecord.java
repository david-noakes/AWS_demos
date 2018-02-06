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
    //private String releaseDate;
    private ZonedDateTime releaseDateTime;
    private List<String> genres;
    private String imageUrl;
    private String plot;
//    private BigDecimal rating;
//    private BigDecimal rank;
//    private BigDecimal runningTimeSecs;
    private Integer rating;
    private Integer rank;
    private Integer runningTimeSecs;
    private List<String> actors;

    public InfoRecord() {
    	directors = new ArrayList<String>();
//    	releaseDateTime = ZonedDateTime.now();
//    	releaseDateTime = ZonedDateTime.of(1900, 00,00,00,00,00,00, ZoneId.of("Australia/Brisbane"));
        releaseDateTime = null;
    	//releaseDate = "";
//    	rating = BigDecimal.ZERO;
        rating = 0;
    	genres = new ArrayList<String>();
    	imageUrl = "";
    	plot = "";
//    	rank = BigDecimal.ZERO;
//    	runningTimeSecs  = BigDecimal.ZERO;
        rank = 0;
        runningTimeSecs  = 0;
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
        releaseDateTime = null;
 //       releaseDate = infoMap.get("release_date").toString();
//        rating = new BigDecimal(infoMap.get("rating").toString()); //BigDecimal.ZERO;
        rating = Integer.valueOf(infoMap.get("rating").toString()); //BigDecimal.ZERO;
        genres = new ArrayList<String>();
        imageUrl = "";
        plot = "";
        rank = 0;
        runningTimeSecs  = 0;
        actors = new ArrayList<String>();

    }

    public InfoRecord(ObjectNode jObj) {
        JsonNode jNode;
        ZonedDateTimeConverter zdtc = new ZonedDateTimeConverter();
        String s;

        ArrayNode jArray = (ArrayNode) jObj.get("directors");
        directors = new ArrayList();
        if (jArray != null) {
            Iterator ita = jArray.iterator();
            while (ita.hasNext()) {
                s = ita.next().toString();
                directors.add(s);
            }
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
            rating = Integer.valueOf(jNode.asInt());
        }
        jArray = (ArrayNode) jObj.get("genres");
        genres = new ArrayList<>();
        if (jArray != null) {
            Iterator ita = jArray.iterator();
            while (ita.hasNext()) {
                s = ita.next().toString();
                genres.add(s);
            }
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
            rank = Integer.valueOf(jNode.asInt());
        }
        jNode = jObj.get("running_time_secs");
        if (jNode != null) {
            runningTimeSecs = Integer.valueOf(jNode.asInt());
        }
        jArray = (ArrayNode) jObj.get("actors");
        actors = new ArrayList<>();
        if (jArray != null) {
            Iterator ita = jArray.iterator();
            while (ita.hasNext()) {
                s = ita.next().toString();
                actors.add(s);
            }
        }

    }

    @DynamoDBAttribute(attributeName = "directors")
    @JsonProperty("directors")
	public List<String> getDirectors() {
		return directors;
	}

	public void setDirectors(List<String> directors) {
		this.directors = directors;
	}

//    @DynamoDBAttribute(attributeName = "release_date_str")
//    @JsonProperty("release_date")
//	public String getReleaseDate() {
//		return releaseDate;
//	}
//
//	public void setReleaseDate(String releaseDate) {
//		this.releaseDate = releaseDate;
//	}

    @DynamoDBTypeConverted(converter = ZonedDateTimeConverter.class)
	@DynamoDBAttribute(attributeName = "release_date")
	@JsonProperty("release_date")
	public ZonedDateTime getReleaseDateTime() {
		if (releaseDateTime != null) {
            return releaseDateTime;
        }
        else {
		    return null;
        }
	}

	public void setReleaseDateTime(ZonedDateTime releaseDateTime) {
		this.releaseDateTime = releaseDateTime;
	}

	@DynamoDBAttribute(attributeName = "rating")
    @JsonProperty("rating")
	public Integer getRating() {

        if (rating != null) {
            return rating;
        } else {
            return Integer.valueOf(-1);
        }
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

    @DynamoDBAttribute(attributeName = "genres")
    @JsonProperty("genres")
	public List<String> getGenres() {
		return genres;
	}

	public void setGenres(List<String> genres) {
		this.genres = genres;
	}

    @DynamoDBAttribute(attributeName = "image_url")
    @JsonProperty("image_url")
	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

    @DynamoDBAttribute(attributeName = "plot")
    @JsonProperty("plot")
	public String getPlot() {
		return plot;
	}

	public void setPlot(String plot) {
		this.plot = plot;
	}

    @DynamoDBAttribute(attributeName = "rank")
    @JsonProperty("rank")
	public Integer getRank() {
		if (rank != null) {
		    return rank;
        } else {
		    return Integer.valueOf(-1);
        }
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

    @DynamoDBAttribute(attributeName = "running_time_secs")
    @JsonProperty("running_time_secs")
	public Integer getRunningTimeSecs() {
		if (runningTimeSecs != null) {
		    return runningTimeSecs;
        } else {
		    return Integer.valueOf(-1);
        }
	}

	public void setRunningTimeSecs(Integer runningTimeSecs) {
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
