package vadim.andreich.api.repositories;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import vadim.andreich.api.model.Measure;
import vadim.andreich.api.model.Sensor;

import java.util.Collection;
import java.util.List;

@Repository
public interface MeasureRepository extends CrudRepository<Measure, Integer> {
    List<Measure> findMeasureBySensorOrderByDateTimeDesc(Sensor sensor);

    @Query("select m from Measure m where m.measureValue > ?1 and m.sensor.id = ?2")
    Collection<Measure> findAllMeasureBiggerThan(Integer value, Integer sensorId);
}
