package vadim.andreich.util.exceptions;

import lombok.Getter;

@Getter
public class SensorNotFoundResponse {
    private final String response;

    public SensorNotFoundResponse (String msg) {
        this.response = msg;
    }

    @Override
    public String toString() {
        return "SensorNotFoundResponse [" + response + "] ";
    }
}
