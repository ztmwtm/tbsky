package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambot.model.NotificationTask;

import java.time.LocalDate;
import java.util.Collection;

public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Long> {
    Collection<NotificationTask> getNotificationTaskByDate(LocalDate date);
}
