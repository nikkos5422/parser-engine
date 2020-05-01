package cz.notix.outputengine.ExceptionHandling;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;


@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(value = CustomException.class)
    @ResponseBody
    public Response handleException(CustomException exception) {
        LOG.warn(exception.getMessage(), exception);
        HttpStatus status = exception.getType().equals(ResponseError.ErrorType.INTERNAL_SERVER_ERROR) ? INTERNAL_SERVER_ERROR : BAD_REQUEST;

        return new Response(status.value(),
                new ResponseError(exception.getType().name(), exception.getCode()
                        , exception.getMessage()));
    }
}
