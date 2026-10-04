package cn.kmbeast.controller;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
class DatabaseReadinessControllerTest {
    @Test void readyRequiresSuccessfulDatabaseQuery() {
        DriverManagerDataSource ds = new DriverManagerDataSource("jdbc:h2:mem:ready", "sa", "");
        ResponseEntity<Map<String,String>> result = new DatabaseReadinessController(ds).ready();
        assertEquals(200, result.getStatusCodeValue());
        assertEquals("ready", result.getBody().get("status"));
    }
    @Test void connectionFailureReturns503WithoutConnectionDetails() {
        DriverManagerDataSource ds = new DriverManagerDataSource("jdbc:invalid:secret-host", "secret-user", "secret-password");
        ResponseEntity<Map<String,String>> result = new DatabaseReadinessController(ds).ready();
        assertEquals(503, result.getStatusCodeValue());
        assertEquals("{status=unavailable}", result.getBody().toString());
    }
}
