package vadim.andreich.listener.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlertDTO {
    private String text;
    private String chatId;
}
