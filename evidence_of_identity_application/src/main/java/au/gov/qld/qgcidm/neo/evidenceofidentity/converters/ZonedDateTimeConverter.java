package au.gov.qld.qgcidm.neo.evidenceofidentity.converters;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;

/**
 * Project cidm_neo_evidenceofidentity_application
 * Created on 22 Dec 2017.
 */
public class ZonedDateTimeConverter  implements DynamoDBTypeConverter<String, ZonedDateTime> {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Override
    public String convert(ZonedDateTime dateTime) {
        return dateTime.format(DATE_FORMATTER);
    }

    @Override
    public ZonedDateTime unconvert(String dateTimeString) {
        return ZonedDateTime.parse(dateTimeString, DATE_FORMATTER);
    }

}
