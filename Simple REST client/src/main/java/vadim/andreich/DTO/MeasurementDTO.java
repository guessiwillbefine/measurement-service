package vadim.andreich.DTO;


import java.time.LocalDateTime;

public class MeasurementDTO {
    private int value;
    private LocalDateTime localDateTime;
    private int sensor;

    public MeasurementDTO(int value, int sensor) {
        this.value = value;
        this.sensor = sensor;
    }

    public MeasurementDTO() {
        //def contr
    }

    public int getValue() {
        return value;
    }

    public int getSensor() {
        return sensor;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setSensor(int sensor) {
        this.sensor = sensor;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    @Override
    public String toString() {
        return String.format("sensor[%d] - %d C°",sensor, value);
    }
}
