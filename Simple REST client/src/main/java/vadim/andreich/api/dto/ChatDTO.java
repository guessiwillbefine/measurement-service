package vadim.andreich.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.io.Serializable;

@Getter
@AllArgsConstructor
public class ChatDTO implements Serializable {
    private Long chatId;
}
