package cn.kmbeast.security;

import org.mindrot.jbcrypt.BCrypt;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/** Stores salted BCrypt hashes of the existing client's 32-character MD5 credential.
 * This preserves deployed clients; the client digest remains password-equivalent and requires TLS.
 */
public final class PasswordStorage {
    public boolean validCredential(String value) {
        return value != null && value.matches("[0-9a-f]{32}");
    }
    public boolean isLegacy(String stored) { return validCredential(stored); }
    public String encode(String credential) {
        if (!validCredential(credential)) throw new IllegalArgumentException("Invalid credential format");
        return BCrypt.hashpw(credential, BCrypt.gensalt(10));
    }
    public boolean matches(String credential, String stored) {
        if (!validCredential(credential) || stored == null) return false;
        if (isLegacy(stored)) return MessageDigest.isEqual(credential.getBytes(StandardCharsets.UTF_8), stored.getBytes(StandardCharsets.UTF_8));
        if (!stored.matches("\\$2a\\$10\\$[./A-Za-z0-9]{53}")) return false;
        try { return BCrypt.checkpw(credential, stored); }
        catch (IllegalArgumentException invalidHash) { return false; }
    }
}
