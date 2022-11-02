package vadim.andreich.util.response;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class ErrorResponse implements Response, Serializable {
    private String response;

    @Override
    public String toString() {
        return "SensorNotFoundResponse [" + response + "] ";
    }

    @Override
    public Response setResponseMessage(String msg) {
        this.response = msg;
        return this;
    }
}
