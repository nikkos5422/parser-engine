package cz.notix.outputengine.ExceptionHandling;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Response implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer status;
    private ResponseError error;

    public Response(final Integer status) {
        this(status, null);
    }
}
