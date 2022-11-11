package vadim.andreich.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.objects.Chat;
import vadim.andreich.api.dto.AlertDTO;
import vadim.andreich.api.dto.ChatDTO;
import vadim.andreich.api.model.Sensor;
import vadim.andreich.api.model.User;
import vadim.andreich.api.services.SensorService;
import vadim.andreich.api.services.TelegramService;
import vadim.andreich.api.services.UserService;


@RestController
@RequestMapping("/")
public class UserController {
    private final SensorService sensorService;
    private final UserService userService;
    private final TelegramService telegramService;

    @Autowired
    public UserController(SensorService sensorService, UserService userService, TelegramService telegramService) {
        this.sensorService = sensorService;
        this.userService = userService;
        this.telegramService = telegramService;
    }

    @PostMapping("/registration")
    public Sensor saveNewSensor(@RequestParam(value = "name", required = false) String name) {
        if (name == null) {
            return sensorService.saveNew();
        }
        return sensorService.saveNew(name);
    }

    @PatchMapping("/update")
    public ResponseEntity.BodyBuilder updateUser(@RequestBody User user) {
        userService.updateUser(user);
        return ResponseEntity.ok();
    }

    @PatchMapping("alert/set")
    public @ResponseBody Boolean setAlert(@RequestBody int chatId) {
          return userService.setAlertsByChatId(chatId) == 1;
    }

    @GetMapping("/alert")
    public @ResponseBody Boolean alertStatus(@RequestParam("chat_id") int chatId) {
        return telegramService.getAlertStatus(chatId);
    }
}
