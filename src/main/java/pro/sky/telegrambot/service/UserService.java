package pro.sky.telegrambot.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.User;
import pro.sky.telegrambot.repository.UserRepository;
import pro.sky.telegrambot.types.UserState;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByChatId(long chatId) {
        return userRepository.getUserByChatId(chatId).orElse(null);
    }

    public void addUser(User user) {
        userRepository.save(user);
    }

    public void setUserState(long chatId, UserState createNotification) {
        User user = getUserByChatId(chatId);
        user.setState(createNotification);
        userRepository.save(user);
    }
}
