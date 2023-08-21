package pro.sky.telegrambot.utils;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.service.NotificationTaskService;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Service
public class Scheduler {

    private final static int APPROX_VALUE = 5; //5 sec

    private final NotificationTaskService notificationTaskService;

    private Set<NotificationTask> dayCache;

    private final Logger logger = LoggerFactory.getLogger(Scheduler.class);

    private final TelegramBot telegramBot;


    public Scheduler(NotificationTaskService notificationTaskService, TelegramBot telegramBot) {
        this.notificationTaskService = notificationTaskService;
        this.telegramBot = telegramBot;
    }

    @PostConstruct
    private void setDayCache() {
        dayCache = new HashSet<>(notificationTaskService.getNotificationsByDate(LocalDate.now()));
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void notificationsSend() {
        logger.info("Scheduler notifications check");
        dayCache.stream().filter(task -> compareTimeWithCurrentTime(task.getTime()))
                .forEach(task -> {
                    telegramBot.execute(new SendMessage(task.getChatId(), task.getMessage()));
                            dayCache.remove(task);
                });


    }

    @Scheduled(cron = "0 0 0 * * *")
    public void updateDayCache() {
        logger.info("Scheduler new day cache update");
        setDayCache();
    }

    public void addNotificationTaskToCache(NotificationTask notificationTask) {
        logger.info("Scheduler cache update with new notification");
        dayCache.add(notificationTask);
    }

    private boolean compareTimeWithCurrentTime(LocalTime time) {
        return LocalTime.now().getHour() == time.getHour() &&
                LocalTime.now().getMinute() == time.getMinute() &&
                Math.abs(LocalTime.now().getSecond() - time.getSecond()) <= APPROX_VALUE;
    }
}
