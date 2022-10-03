package vadim.andreich.services;


import java.util.List;
import java.util.Objects;
import java.util.Optional;
import vadim.andreich.model.Measure;
import org.springframework.stereotype.Service;
import vadim.andreich.model.Sensor;
import vadim.andreich.repositories.MeasureRepository;
import vadim.andreich.repositories.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SensorService {
    private final SensorRepository sensorRepository;
    private final MeasureRepository measureRepository;

    @Autowired
    public SensorService(SensorRepository sensorRepository, MeasureRepository measureRepository) {
        this.sensorRepository = sensorRepository;
        this.measureRepository = measureRepository;
    }

    @Transactional(readOnly = true)
    public List<Measure> getAllMeasurementsByIdSensor(int id) {
        return Objects.requireNonNull(sensorRepository.findById(id).orElse(null)).getMeasures();
    }

    @Transactional
    public void saveMeasurement(Measure measure) {
        Optional<Sensor> temporalSensor = sensorRepository.findById(measure.getSensor().getId());
        temporalSensor.ifPresent(sens -> {
            List<Measure> last = measureRepository.findMeasureBySensorOrderByDateTimeDesc(new Sensor(measure.getSensor().getId()));
            if (last.isEmpty() || last.get(0).getValue() != measure.getValue()) {
                sens.getMeasures().add(measure);
                measure.setSensor(temporalSensor.get());
                sensorRepository.save(temporalSensor.get());
                measureRepository.save(measure);
            }
        });
    }
}
