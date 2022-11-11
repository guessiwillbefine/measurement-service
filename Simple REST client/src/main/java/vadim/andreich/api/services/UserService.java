package vadim.andreich.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vadim.andreich.api.model.User;
import vadim.andreich.api.repositories.UserRepository;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final TelegramService telegramService;

    @Autowired
    public UserService(UserRepository userRepository, TelegramService telegramService) {
        this.userRepository = userRepository;
        this.telegramService = telegramService;
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        return Optional.ofNullable(userRepository.findDistinctByName(username));
    }

    @Transactional
    public boolean saveUser(User user) {
        if (getUserByUsername(user.getName()).isEmpty()) {
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Transactional
    public void deleteUser(User user) {
            userRepository.delete(user);
    }

    @Transactional
    public void updateUser(User user){
        userRepository.save(user);
    }

    public int setAlertsByChatId(int id) {
        return telegramService.setAlerts(id);
    }
}