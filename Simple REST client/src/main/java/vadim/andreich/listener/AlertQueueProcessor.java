package vadim.andreich.listener;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vadim.andreich.api.dto.MeasurementDTO;
import vadim.andreich.telegram.configuration.BotConfig;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
@EnableRabbit
public class AlertQueueProcessor {

    private final BotConfig config;


    @Autowired
    public AlertQueueProcessor(BotConfig config) {
        this.config = config;
    }

    @RabbitListener(queues = {"queue"})
    public void sendNotification(MeasurementDTO dto) throws IOException, InterruptedException {
        String id = "290734915";
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .version(HttpClient.Version.HTTP_2)
                .build();

        UriBuilder builder = UriBuilder
                .fromUri("https://api.telegram.org")
                .path("/{token}/sendMessage")
                .queryParam("chat_id", id)
                .queryParam("text", String.format("---Alert!--- %s %d C° on sensor[%d]", System.lineSeparator(), dto.getValue(), dto.getSensor()));

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(builder.build("bot" + config.getToken()))
                .timeout(Duration.ofSeconds(5))
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
