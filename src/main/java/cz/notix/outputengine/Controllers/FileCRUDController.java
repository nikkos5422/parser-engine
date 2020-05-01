package cz.notix.outputengine.Controllers;


import cz.notix.outputengine.ExceptionHandling.CustomException;
import cz.notix.outputengine.ExceptionHandling.ResponseError;
import cz.notix.outputengine.Model.TemplateFile;
import cz.notix.outputengine.Repository.FileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/file")
@CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT})
public class FileCRUDController {

    protected FileRepository fileRepository;


    public FileCRUDController(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @GetMapping()
    @ResponseBody
    public List<TemplateFile> getAllFiles() {
        return fileRepository.findAll();
    }

    @GetMapping(value = "/{id}")
    public TemplateFile getById(@PathVariable("id") Long id) throws CustomException {
        Optional<TemplateFile> file = fileRepository.findById(id);

        if (file.isPresent()) {
            return file.get();
        } else {
            throw new CustomException(ResponseError.ErrorType.OBJECT_NOT_FOUND, "object does not exist", "current object is not present in databasee ");
        }
    }

    @DeleteMapping("/{id}")
    public void deleteFile(@PathVariable(value = "id") Long fileId) throws CustomException {

        Optional<TemplateFile> file = fileRepository.findById(fileId);
        if (file.isPresent()) {
            fileRepository.deleteById(fileId);
        } else {
            throw new CustomException(ResponseError.ErrorType.OBJECT_NOT_FOUND, "object does not exist", "current object is not present in databasee ");
        }
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<TemplateFile> updateFile(@PathVariable("id") Long id, @RequestBody TemplateFile inputFile) throws CustomException {

        Optional<TemplateFile> fileToChange = fileRepository.findById(id);


        if (fileToChange.isPresent()) {
            TemplateFile updatedFile = fileToChange.get();
            updatedFile.setName(inputFile.getName());
            updatedFile.setDescription(inputFile.getDescription());

            return new ResponseEntity<>(fileRepository.save(updatedFile), HttpStatus.OK);

        } else {
            throw new CustomException(ResponseError.ErrorType.OBJECT_NOT_FOUND, "object does not exist", "current object is not present in databasee ");
        }
    }

    @PostMapping()
    public ResponseEntity<String> fileUpload(@RequestParam("file") MultipartFile file,
                                             @RequestParam("name") String name,
                                             @RequestParam("description") String description
    ) throws CustomException {

        try {
            TemplateFile templateFile = new TemplateFile(name, description, file.getBytes());
            fileRepository.save(templateFile);

        } catch (Exception e) {
            throw new CustomException(ResponseError.ErrorType.INTERNAL_SERVER_ERROR, e.getClass().toString(), "there is some problem with creating new template file.");
        }
        return new ResponseEntity<>(" FILE WAS SUCCESSFULLY ADDED", HttpStatus.OK);
    }

}
