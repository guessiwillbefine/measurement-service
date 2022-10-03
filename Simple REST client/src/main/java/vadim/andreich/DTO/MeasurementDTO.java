package vadim.andreich.DTO;


public class MeasurementDTO {
    private final int value;
    private final int sensor;

    public MeasurementDTO(int value, int sensor) {
        this.value = value;
        this.sensor = sensor;
    }

    public int getValue() {
        return value;
    }

    public int getSensor() {
        return sensor;
    }

    @Override
    public String toString() {
        return "MeasurementDTO{" +
                "value=" + value +
                ", sensor=" + sensor +
                '}';
    }
}
