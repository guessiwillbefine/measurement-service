package vadim.andreich.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import vadim.andreich.model.Sensor;

@Repository
public interface SensorRepository extends CrudRepository<Sensor, Integer> {

}
