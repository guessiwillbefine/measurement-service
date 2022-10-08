package vadim.andreich.service;

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

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource("/application.yaml")
@AutoConfigureMockMvc
class SensorServiceTest {

    SensorService sensorService;
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
        measure.setValue(measureValue);
        measure.setDateTime(LocalDateTime.now());
        assertThat(sensorService.saveMeasurement(measure)).isEqualTo(needToBeSaved);
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