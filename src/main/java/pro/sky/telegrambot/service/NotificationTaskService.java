package pro.sky.telegrambot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDate;
import java.util.Collection;

@Service
public class NotificationTaskService {

    private final NotificationTaskRepository notificationTaskRepository;

    Logger logger = LoggerFactory.getLogger(NotificationTaskService.class);

    public NotificationTaskService(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }
    public void addNotificationTask(NotificationTask notificationTask) {
        notificationTaskRepository.save(notificationTask);
        logger.info("NotificationTask created: {}", notificationTask);
    }

    public Collection<NotificationTask> getNotificationsByDate(LocalDate date) {
        return notificationTaskRepository.getNotificationTaskByDate(date);
    }

    public void updateNotification(NotificationTask notificationTask) {
        notificationTaskRepository.save(notificationTask);
        logger.info("NotificationTask updated: {}", notificationTask);
    }


}
