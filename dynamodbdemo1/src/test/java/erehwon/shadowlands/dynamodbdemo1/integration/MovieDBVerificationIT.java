package erehwon.shadowlands.dynamodbdemo1.integration;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import erehwon.shadowlands.dynamodbdemo1.configuration.BaseConfiguration;
import erehwon.shadowlands.dynamodbdemo1.model.InfoRecord;
import erehwon.shadowlands.dynamodbdemo1.model.MovieRecord;
import erehwon.shadowlands.dynamodbdemo1.testingutils.DynamoTableDefinition;
import erehwon.shadowlands.dynamodbdemo1.testingutils.DynamoTestUtils;
import erehwon.shadowlands.dynamodbdemo1.testingutils.JsonUtils;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;

import static erehwon.shadowlands.dynamodbdemo1.testingutils.DynamoTestUtils.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;



public class MovieDBVerificationIT {
    private static final EnvironmentVariables ENVIRONMENT_VARIABLES = new EnvironmentVariables();
    private static final DynamoTableDefinition MOVIE_TABLE = new DynamoTableDefinition()
            .withTableName("Movies").withKey("year").withSortKeyName("title").withKeyType("N");

    private static final List<DynamoTableDefinition> TABLES = Lists.newArrayList(MOVIE_TABLE);

    @BeforeClass
    public static void setupClass() {
        ENVIRONMENT_VARIABLES.set("AWS_ACCESS_KEY_ID", "accesskey");
        ENVIRONMENT_VARIABLES.set("AWS_SECRET_ACCESS_KEY", "dummy");

        setupDynamoEnvironment(TABLES);

        ENVIRONMENT_VARIABLES.set("dynamo_host", dynamoEndpointUrl);
        ENVIRONMENT_VARIABLES.set("dynamo_region", dynamoRegion);
    }

    @Before
    public void setupTest() {
        DynamoTestUtils.purgeTables(TABLES);
    }

    @Test
    public void testTableCreated() {
        assertThat(recordCount(MOVIE_TABLE.getTableName()), equalTo(0L));
    }

    @Test
    public void testWriteItem01() {
        MovieRecord mr = new MovieRecord();
        mr.setYear(1911);
        ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of(BaseConfiguration.TIME_ZONE));
        mr.setTitle("Needle Nardle Noo");
        mr.getInfo().setReleaseDateTime(zdt);
        mr.getInfo().setRating(Integer.valueOf(0));
        createRow(getDynamoDBMapper(), mr);

        MovieRecord mr1 =  readRow(getDynamoDBMapper(), mr.getYear(), mr.getTitle()) ;
        assertNotNull(mr1);
        assertThat(mr1.getYear(), equalTo(1911));
        assertThat(mr1.getTitle(),
                equalTo("Needle Nardle Noo"));
    }

    @Test
    public void testFromJsonStringConstructorTest() {
        MovieRecord vg = null;
        JsonParser parser;
        try {
            parser = new JsonFactory().createParser(THOR_DARK_WORLD);
            ObjectNode jObjectNode = new ObjectMapper().readTree(parser);
            vg = new MovieRecord(jObjectNode);

        } catch (Exception e) {
            System.err.println("Unable to create verificationGroup " );
            System.err.println(e.getMessage());
        }
//        vg = JsonUtils.INSTANCE.fromJsonString(THOR_DARK_WORLD, MovieRecord.class);
        try {
            createRow(getDynamoDBMapper(), vg);
        } catch (Exception e) {
            System.err.print(e.getStackTrace());
        }

//        MovieRecord vg1 =  getDynamoDBMapper().load(MovieRecord.class, vg.getYear(), vg.getTitle()) ;
        MovieRecord vg1 =  readRow(getDynamoDBMapper(), vg.getYear(), vg.getTitle()) ;
        assertNotNull(vg1);
        assertThat(vg1.getYear(), equalTo(vg.getYear()));
        assertThat(vg1.getInfo().getReleaseDateTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                equalTo(vg.getInfo().getReleaseDateTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));

        assertThat(vg1.getInfo().getActors().size(), equalTo(3));

        InfoRecord underTest = (InfoRecord) vg1.getInfo();
        assertThat(underTest.getActors().get(2), equalTo("\"Tom Hiddleston\""));
        assertThat(underTest.getDirectors().get(0), equalTo("\"Alan Taylor\""));
        assertThat(underTest.getGenres().get(2), equalTo("\"Fantasy\""));
        assertThat(underTest.getRank(), equalTo(5));
        assertThat(underTest.getReleaseDateTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                equalTo(ZonedDateTime.parse("2013-10-30T00:00:00Z").format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));

    }

  public static String THOR_DARK_WORLD =
     "    { " +
     "       \"year\": 2013, " +
     "       \"title\": \"Thor - The Dark World\",  " +
     "       \"info\": {  " +
     "           \"directors\": [\"Alan Taylor\"],  " +
     "           \"release_date\": \"2013-10-30T00:00:00Z\",  " +
     "           \"genres\": [  " +
     "               \"Action\",  " +
     "               \"Adventure\", " +
     "               \"Fantasy\"  " +
     "           ], " +
     "           \"image_url\": \"http://ia.media-imdb.com/images/M/MV5BMTQyNzAwOTUxOF5BMl5BanBnXkFtZTcwMTE0OTc5OQ@@._V1_SX400_.jpg\", " +
     "           \"plot\": \"Faced with an enemy that even Odin and Asgard cannot withstand, Thor must embark on his most perilous and personal journey yet, one that will reunite him with Jane Foster and force him to sacrifice everything to save us all.\",  " +
     "           \"rank\": 5,  " +
     "           \"rating\": 0,   " +
     "           \"running_time_secs\": 0,      " +
     "           \"actors\": [ " +
     "              \"Chris Hemsworth\", " +
     "              \"Natalie Portman\", " +
     "              \"Tom Hiddleston\"  " +
     "           ] " +
    "        } " +
     "    }  " ;

    private void createRow(DynamoDBMapper dynamoDBMapper, MovieRecord movieRec) {

        int year = movieRec.getYear().intValue();
        String title = movieRec.getTitle();


        try {
            System.out.println("Adding a new item...");

            dynamoDBMapper.save(movieRec);

            System.out.println("PutItem succeeded:\n" +  year + ", :" + title +":");

        }
        catch (Exception e) {
            System.err.println("Unable to add item: " + year + " " + title);
            System.err.println(e.getMessage());
        }



    }
    private MovieRecord readRow(DynamoDBMapper dynamoDBMapper, int year, String title) {
        Table table = getDynamoDB().getTable("Movies");
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("year", year, "title", title);

        try {
            System.out.println("Attempting to read the item...");
            Item outcome = table.getItem(spec);
            System.out.println("GetItem succeeded: " + outcome.toJSONPretty());
            MovieRecord vg = JsonUtils.INSTANCE.fromJsonString(outcome.toJSONPretty(), MovieRecord.class);
            return vg;
        }
        catch (Exception e) {
            System.err.println("Unable to read item: " + year + " " + title);
            System.err.println(e.getMessage());
        }

        try {
            MovieRecord vg = dynamoDBMapper.load(MovieRecord.class, year, title);
            return vg;
        }
        catch (Exception e) {
            System.err.println("Unable to read item: " + year + " " + title);
            System.err.println(e.getMessage());
        }
        return null;
    }


}
