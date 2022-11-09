package vadim.andreich.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vadim.andreich.api.model.Telegram;
import vadim.andreich.api.repositories.TelegramRepository;
import vadim.andreich.util.exceptions.TelegramException;

import java.util.Optional;

@Service
public class TelegramService {
    private final TelegramRepository telegramRepository;

    @Autowired
    public TelegramService(TelegramRepository telegramRepository) {
        this.telegramRepository = telegramRepository;
    }


    @Transactional
    public int setAlerts(Long id) {
        return telegramRepository.setUpAlertsByChatId(id);
    }

    @Transactional(readOnly = true)
    public Boolean getAlertStatus(Long chatId) {
        Optional<Telegram> tg = telegramRepository.findDistinctByChatId(chatId);
        if (tg.isPresent()) return tg.get().isAlertEnabled();
        throw new TelegramException("Telegram user not found");
    }
}
