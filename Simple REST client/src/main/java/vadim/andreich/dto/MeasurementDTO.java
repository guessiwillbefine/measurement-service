package vadim.andreich.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
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

    public MeasurementDTO(int value, LocalDateTime localDateTime) {
        this.value = value;
        this.localDateTime = localDateTime;
    }

    @Override
    public String toString() {
        return String.format("sensor[%d] - %d C°",sensor, value);
    }
}
