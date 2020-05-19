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

        } catch (Exception e) {
            throw new CustomException(ResponseError.ErrorType.JSON_PARSING, e.getClass().toString(), "there is some problem with  reading json tree");
        }
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
