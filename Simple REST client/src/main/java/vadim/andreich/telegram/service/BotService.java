package vadim.andreich.telegram.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import vadim.andreich.telegram.configuration.BotConfig;

import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;

@Service
public class BotService extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private final String FILE_NAME = "img.jpeg";
    private final Logger logger;
    private final RestTemplate restTemplate;

    @Autowired
    public BotService(BotConfig botConfig) {
        this.restTemplate = new RestTemplate();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(1000);
        requestFactory.setReadTimeout(1000);
        restTemplate.setRequestFactory(requestFactory);

        this.botConfig = botConfig;
        this.logger = LoggerFactory.getLogger(BotService.class);
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        String[] args = update.getMessage().getText().split(" ");
        switch (args[0]) {
            case "/stats" -> stats(args, update);
            case "/alert" -> setAlert(Math.toIntExact(update.getMessage().getChatId()));
            default ->
                    executeMessage(new SendMessage(String.valueOf(update.getMessage().getChatId()), "command not found"));
        }
    }
 //todo руинится об каст лонга и инта, хз где именно
    private void setAlert(int chatId) {
        String uri = "http://localhost:8080/alert/set";
        ResponseEntity<Boolean> response;
        try {
            RequestEntity<Integer> request = RequestEntity.patch(new URI(uri)).body(chatId);
            response = restTemplate.exchange(request, Boolean.class);
        } catch (URISyntaxException e) {
            logger.error(String.format("Request with URL like this cannot be done: %s", uri));
            return;
        }
        SendMessage messageResponse = new SendMessage();
        messageResponse.setChatId(String.valueOf(chatId));
        if (Boolean.TRUE.equals(response.getBody())) {
            try {
                boolean alertEnabled = alertStatus(chatId);
                messageResponse.setText(alertEnabled ? "Alert turned on" : "Alert turned of");
            } catch (URISyntaxException e) {
                messageResponse.setText("Something get wrong, try again");
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        executeMessage(messageResponse);
    }

    private boolean alertStatus(int chatId) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .version(HttpClient.Version.HTTP_2)
                .build();

        UriBuilder builder = UriBuilder
                .fromUri("http://localhost:8080")
                .path("/alert")
                .queryParam("chat_id", chatId);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(builder.build())
                .timeout(Duration.ofSeconds(5))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        return false;
    }

    private void stats(String[] args, Update update) {
        String uri = String.format("http://localhost:8080/api/stats/%s/%s", args[1], args[2]);
        RequestEntity<Void> request;
        ResponseEntity<Resource> response;

        try {
            request = RequestEntity
                    .get(new URI(uri))
                    .build();
            response = restTemplate.exchange(request, Resource.class);
        } catch (URISyntaxException e) {
            logger.error(String.format("Request with URL like this cannot be done: %s", uri));
            executeMessage(errorResponse(update, args));
            return;
        } catch (HttpClientErrorException e) {
            response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }


        SendPhoto message = new SendPhoto();
        message.setChatId(String.valueOf(update.getMessage().getChatId()));
        InputFile inputFile = new InputFile();
        if (response.getStatusCode().is2xxSuccessful()) {
            File file = readImage(response);
            inputFile.setMedia(file);
            message.setPhoto(inputFile);
            executeMessage(message);
            try {
                if (file != null) {
                    Files.delete(file.toPath());
                }
            } catch (IOException e) {
                logger.error("Error while deleting file");
            }
        } else {
            executeMessage(errorResponse(response.getStatusCode().toString(),
                    String.valueOf(update.getMessage().getChatId())));
        }
    }

    private File readImage(ResponseEntity<Resource> response) {
        try (RandomAccessFile raf = new RandomAccessFile(FILE_NAME, "rw")) {
            ByteArrayResource fileAsResource = new ByteArrayResource(Objects.requireNonNull(response.getBody()).getInputStream().readAllBytes());
            raf.write(fileAsResource.getByteArray());
            return new File(FILE_NAME);
        } catch (FileNotFoundException e) {
            logger.error(String.format("File not found : %s", FILE_NAME));
            return null;
        } catch (IOException e) {
            logger.error("Something went wrong while processing file");
            return null;
        }
    }

    private SendMessage errorResponse(Update update, String[] args) {
        StringBuilder errorResponse = new StringBuilder("Error processing you request with this params : [");
        Arrays.stream(args).forEach(x -> errorResponse.append(x).append(","));
        errorResponse.delete(errorResponse.length() - 1, errorResponse.length()).append("]");
        return new SendMessage(String.valueOf(update.getMessage().getChatId()), errorResponse.toString());
    }

    private SendMessage errorResponse(String response, String chatId) {
        return new SendMessage(chatId, response);
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error(String.format("error sending msg : %s", e.getMessage()));
        }
    }

    private void executeMessage(SendPhoto message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error(String.format("error sending msg : %s", e.getMessage()));
        }
    }
}