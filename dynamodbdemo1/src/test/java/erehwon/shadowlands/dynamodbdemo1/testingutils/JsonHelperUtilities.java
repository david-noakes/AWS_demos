package erehwon.shadowlands.dynamodbdemo1.testingutils;

import erehwon.shadowlands.dynamodbdemo1.converter.ZonedDateTimeConverter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class JsonHelperUtilities {
    public static String getJsonAsText(ObjectNode jObjectNode, String keyName) {
        JsonNode jNode;
        String s;
        jNode = jObjectNode.get(keyName);
        if (!(jNode == null || jNode.isNull() || jNode.isMissingNode())) {
            if (jNode.isValueNode()) {
                return jNode.asText();
            } else 	 {
                return jNode.toString();
            }
        }
        return "";
    }

    public static BigDecimal getJsonAsBigDecimal(ObjectNode jObjectNode, String keyName){
        JsonNode jNode = jObjectNode.get(keyName);
        if (!(jNode == null || jNode.isNull() || jNode.isMissingNode())) {
            if (jNode.isBigDecimal() || jNode.isBigInteger() || jNode.isNumber() )
            return jNode.decimalValue();
        }
        return null;
    }

    public static Integer getJsonAsInteger(ObjectNode jObjectNode, String keyName){
        JsonNode jNode = jObjectNode.get(keyName);
        if (!(jNode == null || jNode.isNull() || jNode.isMissingNode())) {
            if (jNode.isNumber() && jNode.isIntegralNumber() && jNode.canConvertToInt())
                return new Integer(jNode.intValue());
        }
        return null;
    }

    public static ZonedDateTime getJsonAsZonedDateTime(ObjectNode jObjectNode, String keyName){
        ZonedDateTimeConverter zdtc = new ZonedDateTimeConverter();
        JsonNode jNode = jObjectNode.get(keyName);
        if (!(jNode == null || jNode.isNull() || jNode.isMissingNode())) {
            String s = jNode.textValue();
            if (s != null && s.trim().length() > 9) {
                return zdtc.unconvert(s);
            }
        }

        return null;
    }
}
