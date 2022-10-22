package vadim.andreich.repositories;

import vadim.andreich.model.Sensor;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Integer> {
    Optional<Sensor> findDistinctByName(String sensorName);
}
