package vadim.andreich.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vadim.andreich.model.Measure;
import vadim.andreich.model.Sensor;
import vadim.andreich.repositories.MeasureRepository;

import java.util.Collection;
import java.util.List;

@Service
public class MeasureService {

    private final MeasureRepository measureRepository;
    @Autowired
    public MeasureService(MeasureRepository measureRepository) {
        this.measureRepository = measureRepository;
    }

    @Transactional(readOnly = true)
    public List<Measure> findAllMeasuresBySensor(Sensor sensor) {
        return measureRepository.findMeasureBySensorOrderByDateTimeDesc(sensor);
    }
    @Transactional(readOnly = true)
    public Collection<Measure> measureBiggerThan(int value, int sensorId) {
       return measureRepository.findAllMeasureBiggerThan(value, sensorId);
    }
}
