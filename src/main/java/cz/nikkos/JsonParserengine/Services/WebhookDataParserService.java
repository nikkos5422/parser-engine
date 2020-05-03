package cz.nikkos.JsonParserengine.Services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.nikkos.JsonParserengine.ExceptionHandling.CustomException;
import cz.nikkos.JsonParserengine.ExceptionHandling.ResponseError;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WebhookDataParserService {


    private static final String CREATED_BY = "createdBy";
    private static final String CREATED_ON = "createdOn";
    private static final String DUE_DATE = "dueDate";
    private static final String SUBJECT = "subject";


    public static Map<String, Object> getData(String jsonStuff) throws CustomException {

        JSONParser parser = new JSONParser();

        Map<String, Object> outputParametersMap = new HashMap<>();

        Object jsonObject;
        try {
            jsonObject = parser.parse(jsonStuff);
        } catch (ParseException e) {
            throw new CustomException(ResponseError.ErrorType.JSON_PARSING, e.getClass().toString(), "there is some problem with json object parsing");

        }
        fillParameterMap((JSONObject) jsonObject, outputParametersMap);

        return outputParametersMap;

    }

    private static void fillParameterMap(JSONObject inputJsonObject, Map<String, Object> outputParametersMap) throws CustomException {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode attributeNodes = null;
        try {
            if (objectMapper.readTree(inputJsonObject.toJSONString()).has("attributes")) {
                attributeNodes = objectMapper.readTree(inputJsonObject.toJSONString()).get("attributes");
            }

            if (attributeNodes != null) {
                for (int i = 0; i < attributeNodes.size(); i++) {
                    String value;

                    String outputJasperName = attributeNodes.get(i).get("code").asText();

                    value = (attributeNodes.get(i).get("value") == null) ? "" : attributeNodes.get(i).get("value").asText();

                    boolean enumType = attributeNodes.get(i).get("type").asText().equals("enumeration");

                    if (enumType) {
                        JsonNode enumerationValues = attributeNodes.get(i).get("enumerationValues");
                        String embeddedValue = getValueFromEnumerationValuesArray(enumerationValues, value);
                        embeddedValue = (embeddedValue == null) ? "" : embeddedValue;
                        outputParametersMap.put(outputJasperName, embeddedValue);
                        continue;
                    }

                    if (attributeNodes.get(i).get("type").asText().equals("datetime")) {
                        String timeValue = value.substring(0, 10);
                        String dateResult = (parseProperDateFormat(timeValue) == null) ? "" : parseProperDateFormat(timeValue);
                        outputParametersMap.put(outputJasperName, dateResult);
                        continue;
                    }

                    if (attributeNodes.get(i).get("type").asText().equals("file")) {
                        JsonNode CompositeNode = attributeNodes.get(i).get("value");
                        JsonNode fileNameNode = objectMapper.readTree(CompositeNode.asText()).get("originalName");
                        String fileNameResult = (fileNameNode.asText() == null) ? "" : fileNameNode.asText();
                        outputParametersMap.put(outputJasperName, fileNameResult);
                        continue;
                    }
                    outputParametersMap.put(outputJasperName, value);
                }
            }

            String createdBy = (objectMapper.readTree(inputJsonObject.toJSONString()).has(CREATED_BY)) ? objectMapper.readTree(inputJsonObject.toJSONString()).get("createdBy").get("fullName").asText() : "";
            outputParametersMap.put(CREATED_BY, createdBy);

            String createdOn = objectMapper.readTree(inputJsonObject.toJSONString()).has(CREATED_ON) ? parseProperDateFormat(objectMapper.readTree(inputJsonObject.toJSONString()).get("createdOn").asText().substring(0, 10)) : "";
            outputParametersMap.put(CREATED_ON, createdOn);

            String dueDate = objectMapper.readTree(inputJsonObject.toJSONString()).has(DUE_DATE) ? parseProperDateFormat(objectMapper.readTree(inputJsonObject.toJSONString()).get("dueDate").asText().substring(0, 10)) : "";
            outputParametersMap.put(DUE_DATE, dueDate);

            String subject = objectMapper.readTree(inputJsonObject.toJSONString()).has(SUBJECT) ? objectMapper.readTree(inputJsonObject.toJSONString()).get("subject").asText() : "";
            outputParametersMap.put(SUBJECT, subject);


            // case entity array
            JsonNode caseEntityAttributesArray = null;
            if (objectMapper.readTree(inputJsonObject.toJSONString()).has("caseEntity"))
                caseEntityAttributesArray = objectMapper.readTree(inputJsonObject.toJSONString()).get("caseEntity")
                        .get("attributes");

            if (caseEntityAttributesArray != null) {
                for (int j = 0; j < caseEntityAttributesArray.size(); j++) {
                    String caseEntityJasperOutputName = caseEntityAttributesArray.get(j).get("code").asText();
                    String value = (caseEntityAttributesArray.get(j).get("value") == null) ? ""
                            : caseEntityAttributesArray.get(j).get("value").asText();

                    outputParametersMap.put(caseEntityJasperOutputName, value);
                }
            }
        } catch (Exception e) {
            throw new CustomException(ResponseError.ErrorType.JSON_PARSING, e.getClass().toString(), "there is some problem with  reading json tree");
        }
    }

    private static String getValueFromEnumerationValuesArray(JsonNode enumerationValues, String value) {

        String result = "";
        for (int i = 0; i < enumerationValues.size(); i++) {

            if (enumerationValues.get(i).get("id").asText().equals(value)) {
                result = enumerationValues.get(i).get("value").get("cs-CZ").asText();
            }
        }
        return result;
    }

    private static String parseProperDateFormat(String oldDateString) throws CustomException {

        final String OLD_FORMAT = "yyyy-MM-dd";
        final String NEW_FORMAT = "dd.MM.yyyy";
        String newDateString;

        SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
        Date d;
        try {
            d = sdf.parse(oldDateString);
        } catch (java.text.ParseException e) {
            throw new CustomException(ResponseError.ErrorType.DATE_PARSING, e.getClass().toString(), "there is some problem with date parsing");
        }
        sdf.applyPattern(NEW_FORMAT);
        newDateString = sdf.format(d);

        return newDateString;
    }
}
