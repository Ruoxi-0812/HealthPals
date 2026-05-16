package cn.kmbeast.pojo.dto.update;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One turn in an ongoing health-assistant chat (for API context).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthAssistantHistoryDTO {
    /**
     * "user" or "assistant"
     */
    private String role;
    private String content;
}
