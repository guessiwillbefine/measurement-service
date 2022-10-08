package vadim.andreich;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@TestPropertySource("/application.yaml")
@AutoConfigureMockMvc
class MainControllerTest {
//JUnit5 is more tolerant regarding the visibilities of Test classes than JUnit4, which required everything to be public.
//In this context, JUnit5 test classes can have any visibility but private, however,
// it is recommended to use the default package visibility, which improves readability of code.

    @Autowired
    MockMvc mock;

    @Test
    void contextLoads() {
        assertThat(mock).isNotNull();
    }
}
