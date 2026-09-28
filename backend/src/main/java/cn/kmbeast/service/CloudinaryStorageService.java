package cn.kmbeast.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/** Uploads to durable media storage. A provider failure never falls back to local disk. */
@Service
public class CloudinaryStorageService {
    interface UploadClient {
        Map upload(byte[] bytes, Map options) throws IOException;
    }

    private final boolean enabled;
    private final UploadClient client;

    @Autowired
    public CloudinaryStorageService(
            @Value("${app.storage.provider:local}") String provider,
            @Value("${cloudinary.cloud-name:}") String cloudName,
            @Value("${cloudinary.api-key:}") String apiKey,
            @Value("${cloudinary.api-secret:}") String apiSecret) {
        if (!"local".equals(provider) && !"cloudinary".equals(provider)) {
            throw new IllegalArgumentException("Unsupported media storage provider");
        }
        enabled = "cloudinary".equals(provider);
        if (enabled) {
            if (cloudName.trim().isEmpty() || apiKey.trim().isEmpty() || apiSecret.trim().isEmpty()) {
                throw new IllegalArgumentException("Cloudinary credentials are required for cloud storage");
            }
            Cloudinary cloud = new Cloudinary(ObjectUtils.asMap("cloud_name", cloudName,
                    "api_key", apiKey, "api_secret", apiSecret, "secure", true));
            client = (bytes, options) -> cloud.uploader().upload(bytes, options);
        } else {
            client = null;
        }
    }

    CloudinaryStorageService(UploadClient client) {
        this.enabled = true;
        this.client = client;
    }

    public boolean isEnabled() { return enabled; }

    public String upload(MultipartFile file, String resourceType) throws IOException {
        if (!enabled || file.isEmpty() || file.getSize() > 10L * 1024 * 1024) {
            throw new IOException("Invalid upload");
        }
        if (!"image".equals(resourceType) && !"video".equals(resourceType)) {
            throw new IOException("Unsupported media type");
        }
        try {
            Map response = client.upload(file.getBytes(), ObjectUtils.asMap(
                    "resource_type", resourceType,
                    "public_id", "healthpals/" + UUID.randomUUID(),
                    "overwrite", false,
                    "timeout", 60000));
            Object url = response.get("secure_url");
            if (!(url instanceof String) || !((String) url).startsWith("https://res.cloudinary.com/")) {
                throw new IOException("Media storage returned an invalid URL");
            }
            return (String) url;
        } catch (RuntimeException ex) {
            // Do not return provider responses or credentials to the client.
            throw new IOException("Media storage upload failed");
        }
    }
}
