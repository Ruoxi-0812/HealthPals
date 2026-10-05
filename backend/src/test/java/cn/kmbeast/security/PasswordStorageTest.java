package cn.kmbeast.security;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class PasswordStorageTest {
    private final PasswordStorage storage = new PasswordStorage();
    private final String credential = "0123456789abcdef0123456789abcdef";
    @Test void hashesAreSaltedAndVerifyOnlyTheMatchingCredential() {
        String first=storage.encode(credential), second=storage.encode(credential);
        assertNotEquals(first,second); assertEquals(60,first.length());
        assertTrue(storage.matches(credential,first));
        assertFalse(storage.matches("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",first));
        assertFalse(storage.isLegacy(first));
    }
    @Test void legacyAndMalformedValuesFailClosed() {
        assertTrue(storage.matches(credential,credential));
        assertFalse(storage.matches(null,credential));
        assertFalse(storage.matches(credential,null));
        assertFalse(storage.matches(credential,"$2a$31$invalid"));
        assertThrows(IllegalArgumentException.class,()->storage.encode(""));
        assertThrows(IllegalArgumentException.class,()->storage.encode(credential+credential+credential));
    }
}
