package vadim.andreich.services;


import java.util.*;
import java.util.stream.Collectors;

import org.springframework.web.client.RestTemplate;
import vadim.andreich.model.Sensor;
import vadim.andreich.model.Measure;
import org.springframework.stereotype.Service;
import vadim.andreich.repositories.SensorRepository;
import vadim.andreich.repositories.MeasureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import vadim.andreich.util.exceptions.SensorNotFoundException;

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
        Optional<Sensor> sensor = sensorRepository.findById(id);
        if (sensor.isPresent()) {
            return sensor.get().getMeasures();
        }
        throw new SensorNotFoundException(String.format("Sensor with id[%d] was not found", id));
    }

    @Transactional
    public boolean saveMeasurement(Measure measure) {
        Optional<Sensor> temporalSensor = sensorRepository.findById(measure.getSensor().getId());
        if (temporalSensor.isPresent()) {
            List<Measure> last = measureRepository.findMeasureBySensorOrderByDateTimeDesc(new Sensor(measure.getSensor().getId()));
            if (last.isEmpty() || last.get(0).getValue() != measure.getValue()) {
                temporalSensor.get().getMeasures().add(measure);
                measure.setSensor(temporalSensor.get());
                sensorRepository.save(temporalSensor.get());
                measureRepository.save(measure);
                return true;
            }
            return false;
        }
       throw new SensorNotFoundException(String.format("Sensor with id[%d] does not exist", measure.getSensor().getId()));
    }

    @Transactional
    public Map<String, Object> saveNew(String name) {
        Sensor sensor = sensorRepository.saveAndFlush(new Sensor(name));
        return Map.of("sensor_id", sensor.getId(), "sensor name", sensor.getName());
    }

    @Transactional
    public Map<String, Object> saveNew() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://names.drycodes.com/1";
        String response = restTemplate.getForObject(url, String.class);
        if (response == null) {
            throw new NullPointerException();
        }
        String name = Arrays.stream(response.split("")).filter(letter -> !letter.matches("[\\[\\]\"]")).collect(Collectors.joining());
        Sensor sensor = sensorRepository.saveAndFlush(new Sensor(name));
        return Map.of("sensor_id", sensor.getId(), "sensor name", sensor.getName());
    }
}
