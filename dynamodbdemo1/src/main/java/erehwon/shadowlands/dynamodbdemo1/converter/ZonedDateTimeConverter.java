package erehwon.shadowlands.dynamodbdemo1.converter;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeConverter implements DynamoDBTypeConverter<String, ZonedDateTime> {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Override
    public String convert(ZonedDateTime dateTime) {
        if (dateTime != null) {
            return dateTime.format(DATE_FORMATTER);
        } else {
            return null;
        }
    }

    @Override
    public ZonedDateTime unconvert(String dateTimeString) {
        if (dateTimeString != null) {
            return ZonedDateTime.parse(dateTimeString, DATE_FORMATTER);
        } else {
            return null;
        }
    }
}
