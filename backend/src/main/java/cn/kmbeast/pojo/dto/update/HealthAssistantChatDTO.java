package cn.kmbeast.pojo.dto.update;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthAssistantChatDTO {
    /**
     * Latest user question (required).
     */
    private String message;
    /**
     * Prior turns; optional. Only "user" and "assistant" roles are used.
     */
    private List<HealthAssistantHistoryDTO> history;
}
