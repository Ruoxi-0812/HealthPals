package cn.kmbeast.service;

import cn.kmbeast.pojo.api.ApiResult;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.update.HealthAssistantHistoryDTO;
import cn.kmbeast.pojo.vo.HealthAssistantReplyVO;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Proxies wellness Q&A to an OpenAI-compatible Chat Completions API.
 * Configure with APP_AI_API_KEY (or app.ai.api-key) on the server—never expose the key to the browser.
 */
@Service
public class HealthAssistantService {

    private static final int MAX_MESSAGE_LEN = 4000;
    private static final int MAX_HISTORY_TURNS = 10;
    private static final int MAX_TOKENS = 900;

    private static final String SYSTEM_PROMPT =
            "You are a friendly wellness information assistant inside the HealthPals app. "
                    + "You may discuss general healthy habits: sleep, nutrition, movement, stress, and emotional wellbeing, "
                    + "in clear everyday language. "
                    + "You are NOT a licensed clinician: never diagnose, prescribe, or claim certainty about someone's condition. "
                    + "If the user describes emergencies (chest pain, trouble breathing, stroke signs, severe bleeding, suicidal thoughts), "
                    + "tell them to call local emergency services or go to the nearest ER immediately. "
                    + "For ongoing or serious symptoms, encourage seeing a qualified healthcare professional. "
                    + "Prefer concise answers (about 2–5 short paragraphs) unless the user asks for more detail. "
                    + "Match the user's language when possible.";

    @Value("${app.ai.api-key:}")
    private String apiKey;

    @Value("${app.ai.model:gpt-4o-mini}")
    private String model;

    @Value("${app.ai.base-url:https://api.openai.com/v1}")
    private String baseUrl;

    private RestTemplate buildRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(15_000);
        factory.setReadTimeout(120_000);
        RestTemplate template = new RestTemplate();
        template.setRequestFactory(factory);
        return template;
    }

    /**
     * @param message  latest user message
     * @param history  previous turns (same session); may be null
     */
    public Result<HealthAssistantReplyVO> chat(String message, List<HealthAssistantHistoryDTO> history) {
        if (message == null || message.trim().isEmpty()) {
            return ApiResult.error("Please type a question first.");
        }
        String trimmed = message.trim();
        if (trimmed.length() > MAX_MESSAGE_LEN) {
            return ApiResult.error("Please shorten your question (max " + MAX_MESSAGE_LEN + " characters).");
        }
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return ApiResult.error(
                    "The health assistant is not configured. Ask your administrator to set APP_AI_API_KEY "
                            + "(or app.ai.api-key in application.yml).");
        }

        List<Map<String, Object>> messages = new ArrayList<>();
        Map<String, Object> system = new HashMap<>();
        system.put("role", "system");
        system.put("content", SYSTEM_PROMPT);
        messages.add(system);

        if (history != null && !history.isEmpty()) {
            int start = Math.max(0, history.size() - MAX_HISTORY_TURNS);
            for (int i = start; i < history.size(); i++) {
                HealthAssistantHistoryDTO turn = history.get(i);
                if (turn == null || turn.getRole() == null || turn.getContent() == null) {
                    continue;
                }
                String role = turn.getRole().trim().toLowerCase();
                if (!"user".equals(role) && !"assistant".equals(role)) {
                    continue;
                }
                String content = trimContent(turn.getContent(), MAX_MESSAGE_LEN);
                if (content.isEmpty()) {
                    continue;
                }
                Map<String, Object> m = new HashMap<>();
                m.put("role", role);
                m.put("content", content);
                messages.add(m);
            }
        }

        Map<String, Object> user = new HashMap<>();
        user.put("role", "user");
        user.put("content", trimmed);
        messages.add(user);

        Map<String, Object> body = new HashMap<>();
        body.put("model", model.trim());
        body.put("messages", messages);
        body.put("max_tokens", MAX_TOKENS);
        body.put("temperature", 0.55);

        String url = trimTrailingSlash(baseUrl) + "/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey.trim());

        HttpEntity<String> entity = new HttpEntity<>(JSONObject.toJSONString(body), headers);

        try {
            RestTemplate restTemplate = buildRestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            String reply = extractReplyText(response.getBody());
            if (reply == null || reply.isEmpty()) {
                return ApiResult.error("The assistant returned an empty response. Please try again.");
            }
            return ApiResult.success(new HealthAssistantReplyVO(reply.trim()));
        } catch (HttpStatusCodeException e) {
            String detail = parseProviderError(e.getResponseBodyAsString());
            return ApiResult.error("AI service error: " + detail);
        } catch (RestClientException e) {
            return ApiResult.error("Could not reach the AI service. Check the network or base URL, then try again.");
        }
    }

    private static String trimTrailingSlash(String u) {
        if (u == null) {
            return "";
        }
        String s = u.trim();
        while (s.endsWith("/")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    private static String trimContent(String s, int max) {
        if (s == null) {
            return "";
        }
        String t = s.trim();
        return t.length() <= max ? t : t.substring(0, max);
    }

    private static String extractReplyText(String responseBody) {
        if (responseBody == null || responseBody.isEmpty()) {
            return null;
        }
        try {
            JSONObject root = JSONObject.parseObject(responseBody);
            JSONArray choices = root.getJSONArray("choices");
            if (choices == null || choices.isEmpty()) {
                return null;
            }
            JSONObject first = choices.getJSONObject(0);
            JSONObject message = first.getJSONObject("message");
            if (message == null) {
                return null;
            }
            return message.getString("content");
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String parseProviderError(String body) {
        if (body == null || body.isEmpty()) {
            return "unknown error";
        }
        try {
            JSONObject root = JSONObject.parseObject(body);
            JSONObject err = root.getJSONObject("error");
            if (err != null) {
                String msg = err.getString("message");
                if (msg != null && !msg.isEmpty()) {
                    return msg;
                }
            }
        } catch (Exception ignored) {
            /* fall through */
        }
        return body.length() > 220 ? body.substring(0, 220) + "…" : body;
    }
}
