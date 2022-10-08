package vadim.andreich;

import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import vadim.andreich.model.Measure;
import vadim.andreich.services.SensorService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource("/application.yaml")
@AutoConfigureMockMvc
class MainControllerTest {
//JUnit5 is more tolerant regarding the visibilities of Test classes than JUnit4, which required everything to be public.
//In this context, JUnit5 test classes can have any visibility but private, however,
// it is recommended to use the default package visibility, which improves readability of code.
    private final MockMvc mock;
    private final SensorService sensorService;
    @Autowired
    MainControllerTest(MockMvc mock, SensorService sensorService) {
        this.mock = mock;
        this.sensorService = sensorService;
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

    static Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(1, new int[]{}),
                Arguments.of(2, new int[]{20, 21, 22, 23, 24, 25})
        );
    }
}
