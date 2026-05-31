package cn.kmbeast.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

/**
 * Lightweight health endpoint for Docker, ECS, and load balancer checks.
 */
@RestController
public class HealthCheckController {

    @GetMapping("/health")
    public Map<String, String> health() {
        return Collections.singletonMap("status", "ok");
    }
}
