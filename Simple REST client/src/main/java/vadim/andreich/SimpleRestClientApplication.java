package vadim.andreich;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
public class SimpleRestClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(SimpleRestClientApplication.class, args);
    }
}
