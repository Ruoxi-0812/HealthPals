package cn.kmbeast.controller;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Collections;
import java.util.Map;
@RestController
public class DatabaseReadinessController {
    private final JdbcTemplate jdbc;
    public DatabaseReadinessController(javax.sql.DataSource dataSource) {
        jdbc = new JdbcTemplate(dataSource);
        jdbc.setQueryTimeout(5);
    }
    @GetMapping("/health/ready")
    public ResponseEntity<Map<String, String>> ready() {
        try {
            Integer result = jdbc.queryForObject("SELECT 1", Integer.class);
            if (!Integer.valueOf(1).equals(result)) throw new IllegalStateException();
            return ResponseEntity.ok(Collections.singletonMap("status", "ready"));
        } catch (Exception exception) {
            return ResponseEntity.status(503).body(Collections.singletonMap("status", "unavailable"));
        }
    }
}
