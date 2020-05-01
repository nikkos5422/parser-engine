package cz.notix.outputengine.ExceptionHandling;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomException extends Exception {

    private ResponseError.ErrorType type;
    private String code;
    private String message;
}