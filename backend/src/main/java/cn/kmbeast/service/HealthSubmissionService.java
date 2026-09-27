package cn.kmbeast.service;

import cn.kmbeast.context.LocalThreadHolder;
import cn.kmbeast.mapper.HealthSubmissionMapper;
import cn.kmbeast.pojo.api.ApiResult;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.entity.HealthSubmission;
import cn.kmbeast.pojo.entity.UserHealth;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/** A claim, its records, and its alerts commit or roll back as one database transaction. */
@Service
public class HealthSubmissionService {
    @Resource
    private HealthSubmissionMapper healthSubmissionMapper;
    @Resource
    private UserHealthService userHealthService;

    @Transactional(rollbackFor = Exception.class, timeout = 15)
    public Result<Void> submit(String requestKey, List<UserHealth> records) {
        if (requestKey == null || !requestKey.matches("[A-Za-z0-9_-]{16,128}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A valid Idempotency-Key is required");
        }
        if (records == null || records.isEmpty() || records.size() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Submit between 1 and 100 readings");
        }
        // Snapshot only accepted business fields: client user IDs and timestamps cannot alter ownership.
        List<UserHealth> snapshot = new ArrayList<>();
        StringBuilder canonical = new StringBuilder("health-submission-v1;");
        for (UserHealth record : records) {
            if (record == null || record.getHealthModelConfigId() == null
                    || record.getHealthModelConfigId() <= 0 || record.getValue() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Each reading needs a metric ID and value");
            }
            String value = record.getValue().trim();
            if (value.isEmpty() || value.length() > 50) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid reading value");
            }
            try {
                if (!Double.isFinite(Double.parseDouble(value))) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reading values must be finite numbers");
            }
            UserHealth copy = new UserHealth();
            copy.setHealthModelConfigId(record.getHealthModelConfigId());
            copy.setValue(value);
            snapshot.add(copy);
            // Length framing prevents ambiguous concatenations. Record order is part of the request.
            canonical.append(copy.getHealthModelConfigId()).append(':')
                    .append(value.length()).append(':').append(value).append(';');
        }
        Integer userId = LocalThreadHolder.getUserId();
        if (userId == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
        String hash = sha256(canonical.toString());
        healthSubmissionMapper.claim(userId, requestKey, hash);
        HealthSubmission submission = healthSubmissionMapper.lock(userId, requestKey);
        if (!hash.equals(submission.getPayloadHash())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Idempotency-Key was already used for different readings");
        }
        if (Boolean.TRUE.equals(submission.getCompleted())) return ApiResult.success();
        Result<Void> result = userHealthService.save(snapshot);
        if (!Integer.valueOf(200).equals(result.getCode())) {
            throw new IllegalStateException("Health record save failed");
        }
        healthSubmissionMapper.complete(userId, requestKey);
        return result;
    }

    private String sha256(String payload) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(64);
            for (byte b : digest) hex.append(String.format("%02x", b & 0xff));
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is unavailable", e);
        }
    }
}
