package vadim.andreich;

import java.util.List;
import java.util.Random;
import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import vadim.andreich.api.model.Sensor;
import vadim.andreich.api.model.Measure;
import vadim.andreich.api.services.MeasureService;
import vadim.andreich.api.services.SensorService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application.yaml")
class MainControllerTest {
    private final MockMvc mock;
    private final SensorService sensorService;
    private final MeasureService measureService;
    @Autowired
    MainControllerTest(MockMvc mock, SensorService sensorService, MeasureService measureService) {
        this.mock = mock;
        this.sensorService = sensorService;
        this.measureService = measureService;
    }

    @Test
    void contextLoads() {
        assertThat(mock).isNotNull();
    }

    @Test
    void registrationTest() throws Exception {
        mock.perform(post("/api/register")).andExpect(status().is(200));
        mock.perform(post("/api/register")
                        .param("name", "Dowakin"))
                .andExpect(status().is(200));
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void gettingMeasurementsListTest(int id, int[] measurements) throws Exception {
        sensorService.saveNew();
        Arrays.stream(measurements)
                .forEach(x -> sensorService.saveMeasurement(new Measure(x, id)));
        mock.perform(get("/api/getMeasure/" + id)).andExpect(status().is(200));
    }

    @Test
    void testExceptionHandlerWhileGettingMeasure() throws Exception {
        mock.perform(get("/api/getMeasure/-2"))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                .andExpect(content().string("{\"response\":\"Sensor with id[-2] was not found\"}"));
    }

    @Test
    void testExceptionHandlerWhileSavingMeasure() throws Exception {
        mock.perform(post("/api/save").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        mock.perform(post("/api/save").contentType(MediaType.APPLICATION_JSON).content("{\"sensor\": -1,\"value\": 20}"))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void pictureShouldBeReturnedWithCorrectData() throws Exception {
        Sensor sensor = sensorService.saveNew();
        sensorService.saveMeasurement(new Measure(21, sensor));
        sensorService.saveMeasurement(new Measure(18, sensor));
        sensorService.saveMeasurement(new Measure(21, sensor));

        mock.perform(get(String.format("/api/stats/%s/3", sensor.getName())))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentType(MediaType.IMAGE_JPEG_VALUE));
    }

    @Test
    void testExceptionHandlerWhileGetStats() throws Exception {
        Random random = new Random();
        mock.perform(get("/api/stats/illegal_argument1/13254213")).andExpect(status().isNotFound());
        Sensor sensor = sensorService.saveNew();

        final int measureCount = 150;
        for (int i = 0; i < measureCount; i++) {
            sensorService.saveMeasurement(new Measure(random.nextInt(30), sensor));
        }

        List<Measure> measureList = measureService.findAllMeasuresBySensor(sensor);
        for (int i = 0; i < 5; i++) {
            mock.perform(get(String.format("/api/stats/%s/%d", sensor.getName(), measureList.size())))
                    .andExpect(status().isOk());
        }
    }

    static Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(1, new int[]{}),
                Arguments.of(2, new int[]{20, 21, 22, 23, 24, 25})
        );
    }
}
