package cz.nikkos.outputengine.ExceptionHandling;

import lombok.AllArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
public class ResponseError implements Serializable {

    private static final long serialVersionUID = 1L;

    public static enum ErrorType {
        OBJECT_NOT_FOUND,
        INTERNAL_SERVER_ERROR,
        JSON_PARSING, DATE_PARSING,
        INVALID_DECRYPTION,
        INVALID_ENCRYPTION,
        JASPER_JRE_EXCEPTION,
        MESSAGING_EXCEPTION,
        EXPORT_REPORT_TO_PDF_STREAM,

    }

    private final String type;
    private final String code;
    private final String message;

    public ResponseError(final String type, final String code) {
        this(type, code, null);
    }

    @Override
    public String toString() {
        return "ResponseError [type=" + type + ", code=" + code + ", message=" + message + "]";
    }
}
