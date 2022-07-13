package lab05.http;

import lombok.Data;

@Data(staticConstructor = "of")
public class ErrorResponse {

    private final String message;

}