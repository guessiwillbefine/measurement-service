package vadim.andreich.repositories;

import vadim.andreich.model.Sensor;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Integer> {
}
