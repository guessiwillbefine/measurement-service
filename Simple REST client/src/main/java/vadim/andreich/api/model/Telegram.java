package vadim.andreich.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "telegram")
public class Telegram {

    @Id
    @Column(name = "chat_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int chatId;

    @Column(name = "username")
    private String username;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "chat_id", referencedColumnName = "chat_id")
    private User user;

    @Column(name = "alerts")
    boolean alertEnabled;

    public Telegram(){/*empty constructor for hibernate*/}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Telegram telegram = (Telegram) o;
        return chatId == telegram.chatId && Objects.equals(username, telegram.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId, username);
    }
}
