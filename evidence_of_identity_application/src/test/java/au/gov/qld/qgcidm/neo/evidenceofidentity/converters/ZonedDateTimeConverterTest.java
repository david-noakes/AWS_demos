package au.gov.qld.qgcidm.neo.evidenceofidentity.converters;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Before;
import org.junit.Test;

/**
 * Project cidm_neo_evidence_of_identity_application
 * Created on 4 Jan 2018.
 */
public class ZonedDateTimeConverterTest {

    private static final String EXPECTED_DATE_STRING = "2017-11-30T14:15:00+10:00";
    private ZonedDateTime zonedDateTime;


    private ZonedDateTimeConverter underTest;

    @Before
    public void setUp() throws Exception {
        underTest = new ZonedDateTimeConverter();

        zonedDateTime = ZonedDateTime.of(2017, 11, 30, 14, 15, 0, 0, ZoneId.of("+1000"));
    }

    @Test
    public void convertHasStringAsUTC() throws Exception {
        String dateString = underTest.convert(zonedDateTime);
        assertThat(dateString, equalTo(EXPECTED_DATE_STRING));
    }

    @Test
    public void unconvertConvertsUTCString() throws Exception {
        ZonedDateTime date = underTest.unconvert(EXPECTED_DATE_STRING);
        assertNotNull(date);
        assertThat(date.getYear(), equalTo(2017));
        assertThat(date.getMonthValue(), equalTo(11));
        assertThat(date.getDayOfMonth(), equalTo(30));
        assertThat(date.getHour(), equalTo(14));
        assertThat(date.getMinute(), equalTo(15));
        assertThat(date.getSecond(), equalTo(0));
    }
}
