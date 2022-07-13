package lab05.http;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data(staticConstructor = "of")
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class SuccessResponse {

    private String response;
    private int id;

}