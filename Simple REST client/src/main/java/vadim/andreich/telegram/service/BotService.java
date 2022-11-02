package vadim.andreich.telegram.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;

@Service
public class BotService extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private final String FILE_NAME = "img.jpeg";
    private final Logger logger;

    @Autowired
    public BotService(BotConfig botConfig) {
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
        String uri = String.format("http://localhost:8080/api/stats/%s/%s", args[0], args[1]);
        RestTemplate restTemplate = new RestTemplate();
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