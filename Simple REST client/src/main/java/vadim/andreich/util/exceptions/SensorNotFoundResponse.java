package vadim.andreich.util.exceptions;

import java.time.LocalDateTime;

public class SensorNotFoundResponse {
    private String msg;
    private LocalDateTime dateTime;

    public static SensorNotFoundResponse makeResponse() {
        return new SensorNotFoundResponse();
    }

    public SensorNotFoundResponse withMessage(String msg) {
        this.msg = msg;
        return this;
    }

    @Override
    public String toString() {
        return "SensorNotFoundResponse [" + dateTime + "]: " + msg;
    }
}
