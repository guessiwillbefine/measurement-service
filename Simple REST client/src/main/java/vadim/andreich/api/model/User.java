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
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_name")
    private String name;

    @OneToOne(mappedBy = "user")
    private Telegram chatId;


    public User() {/*empty constructor for hibernate*/}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && chatId == user.chatId && name.equals(user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, chatId);
    }
}
