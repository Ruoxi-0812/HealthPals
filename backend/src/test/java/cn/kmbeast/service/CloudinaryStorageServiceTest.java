package cn.kmbeast.service;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import java.io.IOException;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;

class CloudinaryStorageServiceTest {
    private final MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", new byte[]{1,2,3});

    @Test void returnsDurableHttpsUrlAndDoesNotOverwriteOtherUploads() throws Exception {
        CloudinaryStorageService storage = new CloudinaryStorageService((bytes, options) -> {
            assertArrayEquals(file.getBytes(), bytes);
            assertEquals("image", options.get("resource_type"));
            assertEquals("healthpals", options.get("asset_folder"));
            assertEquals(false, options.get("overwrite"));
            assertTrue(options.get("public_id").toString().startsWith("healthpals/"));
            return Collections.singletonMap("secure_url", "https://res.cloudinary.com/demo/image/upload/test.png");
        });
        assertEquals("https://res.cloudinary.com/demo/image/upload/test.png", storage.upload(file, "image"));
    }

    @Test void invalidResponsesAndProviderFailuresAreNotSuccessfulUploads() {
        CloudinaryStorageService invalid = new CloudinaryStorageService((bytes, options) -> Collections.singletonMap("url", "http://example.com/test"));
        assertThrows(IOException.class, () -> invalid.upload(file, "image"));
        CloudinaryStorageService failed = new CloudinaryStorageService((bytes, options) -> { throw new IllegalStateException("secret provider detail"); });
        assertEquals("Media storage upload failed", assertThrows(IOException.class, () -> failed.upload(file, "image")).getMessage());
    }

    @Test void emptyFilesAreRejectedBeforeCallingProvider() {
        CloudinaryStorageService storage = new CloudinaryStorageService((bytes, options) -> { fail("must not upload empty file"); return null; });
        assertThrows(IOException.class, () -> storage.upload(new MockMultipartFile("file",new byte[0]), "image"));
    }

    @Test void cloudModeRequiresCredentialsAndLocalModeIsExplicit() {
        assertFalse(new CloudinaryStorageService("local", "", "", "").isEnabled());
        assertThrows(IllegalArgumentException.class, () -> new CloudinaryStorageService("cloudinary", "", "", ""));
        assertThrows(IllegalArgumentException.class, () -> new CloudinaryStorageService("typo", "", "", ""));
    }
    @Test void controllerReturnsCloudUrlAndNeverWritesLocalFallback() throws Exception {
        cn.kmbeast.controller.FileController controller = new cn.kmbeast.controller.FileController();
        java.lang.reflect.Field field = controller.getClass().getDeclaredField("cloudStorage");
        field.setAccessible(true);
        field.set(controller, new CloudinaryStorageService((bytes, options) -> Collections.singletonMap("secure_url", "https://res.cloudinary.com/demo/image/upload/test.png")));
        assertEquals("https://res.cloudinary.com/demo/image/upload/test.png", controller.uploadFile(file).get("data"));
        field.set(controller, new CloudinaryStorageService((bytes, options) -> { throw new IOException("provider unavailable"); }));
        assertEquals(400, controller.uploadFile(file).get("code"));
        assertEquals(400, controller.videoUpload(file).get("code"));
    }
}
