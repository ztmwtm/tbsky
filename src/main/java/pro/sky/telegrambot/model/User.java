package pro.sky.telegrambot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import pro.sky.telegrambot.types.UserState;

import javax.persistence.*;
import java.util.Collection;

@Entity(name = "bot_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private Long chatId;
    @Enumerated(EnumType.STRING)
    private UserState state;

    @OneToMany(mappedBy = "chatId")
    @JsonIgnore
    private Collection<NotificationTask> tasks;

    public User() {
    }

    public User(long chatId) {
        this.chatId = chatId;
        this.state = UserState.DEFAULT;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }
}
