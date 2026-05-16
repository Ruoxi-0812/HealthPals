package cn.kmbeast.controller;

import cn.kmbeast.pojo.api.ApiResult;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.update.HealthAssistantChatDTO;
import cn.kmbeast.pojo.vo.HealthAssistantReplyVO;
import cn.kmbeast.service.HealthAssistantService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/health-assistant")
public class HealthAssistantController {

    @Resource
    private HealthAssistantService healthAssistantService;

    /**
     * Wellness Q&amp;A (OpenAI-compatible backend). Requires user JWT.
     */
    @PostMapping("/chat")
    public Result<HealthAssistantReplyVO> chat(@RequestBody HealthAssistantChatDTO dto) {
        if (dto == null) {
            return ApiResult.error("Invalid request body.");
        }
        return healthAssistantService.chat(dto.getMessage(), dto.getHistory());
    }
}
