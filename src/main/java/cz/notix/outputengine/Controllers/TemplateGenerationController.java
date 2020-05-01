package cz.notix.outputengine.Controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.notix.outputengine.ExceptionHandling.CustomException;
import cz.notix.outputengine.ExceptionHandling.ResponseError;
import cz.notix.outputengine.Model.TemplateFile;
import cz.notix.outputengine.Repository.FileRepository;
import cz.notix.outputengine.Services.PdfMailService;
import cz.notix.outputengine.Services.WebhookDataParserService;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class TemplateGenerationController {


    final PdfMailService pdfMailService;

    private final FileRepository repository;
    private final String CREATED_BY= "$createdBy";


    public TemplateGenerationController(FileRepository repository, PdfMailService pdfMailService) {
        this.repository = repository;
        this.pdfMailService = pdfMailService;
    }


    @GetMapping("")
    public String retrieveEmbeddedEmail(String jsonStuff ) throws CustomException {

        String result = "";
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode createdByNode ;
        JSONParser parser = new JSONParser();

        JSONObject inputJsonObject;
        try {
            inputJsonObject = (JSONObject) parser.parse(jsonStuff);


            if (objectMapper.readTree(inputJsonObject.toJSONString()).has("createdBy")) {
                createdByNode = objectMapper.readTree(inputJsonObject.toJSONString()).get("createdBy");
                result = createdByNode.get("email").asText();
            }
        }catch (ParseException | IOException e) {
            throw new CustomException(ResponseError.ErrorType.JSON_PARSING, e.getClass().toString(), "there is some problem with  reading json tree");
        }
        return  result;
    }


    @PostMapping("/generatepdf")
    ResponseEntity<String> createAndSendPdf(@RequestHeader("x-api-key") String key,
                                            @RequestBody String jsonStuff) throws CustomException {
        List<String> emailList;
        String[] emailArray = key.substring(key.indexOf("=") + 1, key.indexOf("&")).split(",");
        emailList = Arrays.asList(emailArray);

        if(emailList.contains(CREATED_BY)){
String creatorEmail=  retrieveEmbeddedEmail(jsonStuff);
            emailList.set(emailList.indexOf(CREATED_BY),creatorEmail);
        }

        final String requestId = retrieveRegularExpression(key, "id", "id=(?<id>[\\w\\d@\\.]+)(&|$)");
        Long templateId = Long.parseLong(requestId);
        TemplateFile currentFile;
        Map<String, Object> outputParametersMap = WebhookDataParserService.getData(jsonStuff);

        Optional<TemplateFile> file = repository.findById(templateId);

        if (file.isPresent()) {
            currentFile = file.get();
        } else {
            throw new CustomException(ResponseError.ErrorType.OBJECT_NOT_FOUND, "Requested template does not exist", "Requested template  is not present in databasee ");
        }

        JasperPrint jasperPrint;
        try {
            jasperPrint = JasperFillManager.fillReport(currentFile.returnFileAsStream(), outputParametersMap, new JREmptyDataSource());
        } catch (JRException e) {
            throw new CustomException(ResponseError.ErrorType.JASPER_JRE_EXCEPTION, e.getClass().toString(), "there is some problem with filling jasper report");
        }

        for (String email: emailList){
            pdfMailService.sendPdf(jasperPrint,email);
        }
        return new ResponseEntity<>("{\"message\" : \"PDF FILE WAS SUCCESSFULLY GENERATED AND SENDED.\"}", HttpStatus.OK);
    }

    private String retrieveRegularExpression(String key, String fieldName, String expression) {
        final Pattern pattern = Pattern.compile(expression);
        final Matcher emailMatcher = pattern.matcher(key);
        emailMatcher.find();
        return emailMatcher.group(fieldName);
    }
}
