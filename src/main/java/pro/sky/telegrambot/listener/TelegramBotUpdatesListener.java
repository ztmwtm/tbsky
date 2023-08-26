package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.model.User;
import pro.sky.telegrambot.service.NotificationTaskService;
import pro.sky.telegrambot.service.UserService;
import pro.sky.telegrambot.types.Period;
import pro.sky.telegrambot.types.UserState;
import pro.sky.telegrambot.utils.MessageParser;
import pro.sky.telegrambot.utils.Scheduler;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private static final String NOT_SUPPORTED_TEXT = "Sorry this command not supported yet.";

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final UserService userService;
    private final NotificationTaskService notificationTaskService;
    private final Scheduler scheduler;
    private final TelegramBot telegramBot;

    public TelegramBotUpdatesListener(UserService userService, NotificationTaskService notificationTaskService, Scheduler scheduler, TelegramBot telegramBot) {
        this.userService = userService;
        this.notificationTaskService = notificationTaskService;
        this.scheduler = scheduler;
        this.telegramBot = telegramBot;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            long chatId;
            logger.info("Processing update: {}", update);
            if (update.message() != null && update.message().text() != null) {
                chatId = update.message().chat().id();
                User user = userService.getUserByChatId(chatId);
                SendMessage reply;

                if (Objects.nonNull(user) && user.getState() == UserState.CREATE_NOTIFICATION) {

                    String message = update.message().text();
                    if (MessageParser.isHaveValidDateTimeMessage(message)) {
                        LocalDateTime localDateTime = MessageParser.parseDate(message);
                        NotificationTask notificationTask = new NotificationTask(chatId);
                        notificationTask.setPeriod(Period.ONETIME);
                        notificationTask.setMessage(MessageParser.parseMessage(message));
                        notificationTask.setDate(localDateTime.toLocalDate());
                        notificationTask.setTime(localDateTime.toLocalTime());
                        notificationTaskService.addNotificationTask(notificationTask);
                        scheduler.addNotificationTaskToCache(notificationTask);
                        reply = getMenuMessage(chatId, "New notification added:\n" + notificationTask);
                        userService.setUserState(chatId, UserState.DEFAULT);

                    } else {
                        reply = new SendMessage(chatId, "You input incorrect date, please try again\n" +
                                "Input notification in format: dd.mm.yyyy hh.mm {Notification message}");

                    }
                } else {
                    reply = switch (update.message().text()) {
                        case "/start" -> getStartMessage(chatId);
                        case "Let's begin!", "Back" -> getMenuMessage(chatId);
                        case "Create notification" -> getCreateMessage(chatId);
                        case "One time notification" -> getNotificationMessage(chatId, Period.ONETIME);
                        case "Daily notification" -> getNotificationMessage(chatId, Period.DAILY); //TODO
                        case "Weekly notification" -> getNotificationMessage(chatId, Period.WEEKLY); //TODO
                        case "Monthly notification" -> getNotificationMessage(chatId, Period.MONTHLY); //TODO
                        case "Yearly notification" -> getNotificationMessage(chatId, Period.YEARLY); //TODO
                        default -> new SendMessage(chatId, NOT_SUPPORTED_TEXT);

                    };
                }
                telegramBot.execute(reply);
            }

        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private SendMessage getNotificationMessage(long chatId, Period onetime) {
        userService.setUserState(chatId, UserState.CREATE_NOTIFICATION);
        return new SendMessage(chatId,
                "Please input notification in format: dd.mm.yyyy hh.mm {Notification message}");
    }

    private SendMessage getStartMessage(long chatId) {

        if (Objects.isNull(userService.getUserByChatId(chatId))) {
            userService.addUser(new User(chatId));
        }
        String[][] keyboard = new String[1][1];
        keyboard[0][0] = "Let's begin!";
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup(keyboard, true, true, false, false);
        SendMessage message = new SendMessage(chatId, "Hello");
        message.replyMarkup(keyboardMarkup);

        return message;
    }

    private SendMessage getMenuMessage(long chatId) {
        return getMenuMessage(chatId, "Menu");
    }

    private SendMessage getMenuMessage(long chatId, String message) {
        String[][] keyboard = new String[5][1];
        keyboard[0][0] = "Create notification";
        keyboard[1][0] = "Show current notifications";
        keyboard[2][0] = "Delete notification";
        keyboard[3][0] = "Help";
        keyboard[4][0] = "Bot settings";

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup(keyboard, true, true, false, false);
        SendMessage reply = new SendMessage(chatId, message);
        reply.replyMarkup(keyboardMarkup);

        return reply;
    }

    private SendMessage getCreateMessage(long chatId) {
        String[][] keyboard = new String[3][2];
        keyboard[0][0] = "One time notification";
        keyboard[0][1] = "Daily notification";
        keyboard[1][0] = "Weekly notification";
        keyboard[1][1] = "Monthly notification";
        keyboard[2][0] = "Yearly notification";
        keyboard[2][1] = "Back";
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup(keyboard, true, true, false, false);
        SendMessage reply = new SendMessage(chatId, "Choose period for notification");
        reply.replyMarkup(keyboardMarkup);

        return reply;
    }
}