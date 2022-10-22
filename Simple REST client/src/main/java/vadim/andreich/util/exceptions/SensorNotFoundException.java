package vadim.andreich.util.exceptions;

import vadim.andreich.model.Sensor;

public class SensorNotFoundException extends RuntimeException {
    public SensorNotFoundException(String msg){
        super(msg);
    }

}
