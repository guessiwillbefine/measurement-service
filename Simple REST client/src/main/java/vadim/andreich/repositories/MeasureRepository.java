package vadim.andreich.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import vadim.andreich.model.Measure;
import vadim.andreich.model.Sensor;

import java.util.List;

@Repository
public interface MeasureRepository extends CrudRepository<Measure, Integer> {
    List<Measure> findMeasureBySensorOrderByDateTimeDesc(Sensor sensor);
}
