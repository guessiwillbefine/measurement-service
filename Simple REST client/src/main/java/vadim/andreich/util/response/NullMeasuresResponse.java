package vadim.andreich.util.response;

public class NullMeasuresResponse {
    private String response;
    private final String sensoreName;

    public NullMeasuresResponse (String sensorName, String msg, String sensoreName) {
        this.response = msg;
        this.sensoreName = sensoreName;
    }

    public NullMeasuresResponse(String sensoreName) {
        this.sensoreName = sensoreName;
    }

    @Override
    public String toString() {
        return "Sensor[%s] has zero measures ";
    }
}
