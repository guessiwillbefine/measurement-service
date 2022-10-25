package vadim.andreich;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import vadim.andreich.model.Sensor;
import vadim.andreich.services.ChartService;
import vadim.andreich.services.SensorService;
import vadim.andreich.util.exceptions.MeasuresException;
import vadim.andreich.util.exceptions.SensorNotFoundException;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application.yaml")
public class ChartServiceTest {
    @Autowired
    private ChartService chartService;
    @Autowired
    private SensorService sensorService;

    @Test
    void NullMeasuresShouldBeThrown() {
        Sensor sensor = sensorService.saveNew();
        assertThrows(MeasuresException.class, () -> chartService.getChartBySensor(sensor.getName(), new Random().nextInt()));
        assertThrows(SensorNotFoundException.class, () -> chartService.getChartBySensor("nonExistingSensor", new Random().nextInt()));
    }
}
