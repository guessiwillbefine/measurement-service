package vadim.andreich;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import vadim.andreich.model.Measure;
import vadim.andreich.model.Sensor;
import vadim.andreich.services.SensorService;
import vadim.andreich.util.exceptions.SensorNotFoundException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource("/application.yaml")
@AutoConfigureMockMvc
class SensorServiceTest {

    private final SensorService sensorService;

    @Autowired
    public SensorServiceTest(SensorService sensorService) {
        this.sensorService = sensorService;
        sensorService.saveNew(); // register sensor for test cases
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void testSavingMeasures(int sensorId, int measureValue, boolean needToBeSaved) {
        Measure measure = new Measure();
        measure.setSensor(new Sensor(sensorId));
        measure.setMeasureValue(measureValue);
        measure.setDateTime(LocalDateTime.now());
        assertThat(sensorService.saveMeasurement(measure)).isEqualTo(needToBeSaved);
    }

    @Test
    void sensorNotFoundTestCaseWhileGettingMeasures(){
        assertThrows(SensorNotFoundException.class, () -> sensorService.getAllMeasurementsBySensorId(-1));
        assertThrows(SensorNotFoundException.class, () -> sensorService.getAllMeasurementsBySensorId(0));
        assertThrows(SensorNotFoundException.class, () -> sensorService.getAllMeasurementsBySensorId(123));
    }
    @Test
    void sensorNotFoundTestCaseWhileSavingMeasures(){
        Measure measure = new Measure(21, new Sensor(new Random().nextInt(-100, 0)));
        assertThrows(SensorNotFoundException.class, () -> sensorService.saveMeasurement(measure));
    }

    public static Stream<Arguments> testCases() { //первым идёт аргумент для тестируемого метода, вторым ожидаемый результат
        return Stream.of(
                Arguments.of(1, 20, true),
                Arguments.of(1, 20, false),
                Arguments.of(1, 21, true),
                Arguments.of(1, 20, true),
                Arguments.of(1, 22, true),
                Arguments.of(1, 22, false),
                Arguments.of(1, 20, true));
    }
}
