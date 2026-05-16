package cn.kmbeast.controller;

import cn.kmbeast.utils.IdFactoryUtil;
import cn.kmbeast.utils.PathUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * File Controller
 */
@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${my-server.api-context-path}")
    private String API;

    /**
     * File upload
     *
     * @param multipartFile File stream
     * @return Response
     */
    @PostMapping("/upload")
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        String fileName = buildStoredFileName(multipartFile);
        Map<String, Object> rep = new HashMap<>();
        try {
            if (uploadFile(multipartFile, fileName)) {
                rep.put("code", 200);
                rep.put("data", API+ "/file/getFile?fileName=" + fileName);
                return rep;
            }
        } catch (IOException e) {
            rep.put("code", 400);
            rep.put("msg", "File upload failed");
            return rep;
        }
        rep.put("code", 400);
        rep.put("msg", "File upload failed");
        return rep;
    }

    /**
     * Video upload
     *
     * @param multipartFile File stream
     * @return Response
     */
    @PostMapping("/video/upload")
    public Map<String, Object> videoUpload(@RequestParam("file") MultipartFile multipartFile) {
        String fileName = buildStoredFileName(multipartFile);
        Map<String, Object> rep = new HashMap<>();

        try {
            if (uploadFile(multipartFile, fileName)) {
                rep.put("code", 200);
                rep.put("data", API+ "/file/getFile?fileName=" + fileName);
                return rep;
            }
        } catch (IOException e) {
            rep.put("code", 400);
            rep.put("msg", "File upload failed");
            return rep;
        }
        rep.put("code", 400);
        rep.put("msg", "File upload failed");
        return rep;
    }

    /**
     * Upload file
     *
     * @param multipartFile File stream
     * @param fileName      File name
     * @return boolean
     * @throws IOException Exception
     */
    public boolean uploadFile(MultipartFile multipartFile, String fileName) throws IOException {
        return fileName(multipartFile, fileName);
    }

    /**
     * Stored name: {@code <id>-<ascii-slug>.<ext>} (no non-ASCII in filename).
     */
    static String buildStoredFileName(MultipartFile multipartFile) {
        String original = multipartFile.getOriginalFilename();
        String ext = safeExtension(original);
        String slug = asciiSlug(baseName(original));
        if (slug.isEmpty()) {
            slug = "file";
        }
        return IdFactoryUtil.getFileId() + "-" + slug + ext;
    }

    private static String baseName(String original) {
        if (original == null || original.isEmpty()) {
            return "";
        }
        String name = original.replace("\\", "/");
        int slash = name.lastIndexOf('/');
        if (slash >= 0) {
            name = name.substring(slash + 1);
        }
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(0, dot) : name;
    }

    private static String safeExtension(String original) {
        if (original == null) {
            return ".bin";
        }
        int dot = original.lastIndexOf('.');
        if (dot < 0 || dot == original.length() - 1) {
            return ".bin";
        }
        String ext = original.substring(dot).toLowerCase();
        if (ext.matches("\\.(png|jpe?g|gif|webp|bmp|svg|mp4|webm|pdf)")) {
            return ext;
        }
        return ".bin";
    }

    private static String asciiSlug(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }
        String slug = name.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
        if (slug.length() > 48) {
            slug = slug.substring(0, 48);
        }
        return slug;
    }

    public static boolean fileName(MultipartFile multipartFile, String fileName) throws IOException {
        File fileDir = new File(PathUtils.getClassLoadRootPath() + "/pic");
        if (!fileDir.exists()) {
            if (!fileDir.mkdirs()) {
                return false;
            }
        }
        File file = new File(fileDir.getAbsolutePath() + "/" + fileName);
        if (file.exists()) {
            if (!file.delete()) {
                return false;
            }
        }
        if (file.createNewFile()) {
            multipartFile.transferTo(file);
            return true;
        }
        return false;
    }

    /**
     * Retrieve image resource
     *
     * @param imageName File name
     * @param response  HTTP response
     * @throws IOException Exception
     */
    @GetMapping("/getFile")
    public void getImage(@RequestParam("fileName") String imageName,
                         HttpServletResponse response) throws IOException {
        File fileDir = new File(PathUtils.getClassLoadRootPath() + "/pic");
        File image = new File(fileDir.getAbsolutePath() + "/" + imageName);
        if (image.exists()) {
            FileInputStream fileInputStream = new FileInputStream(image);
            byte[] bytes = new byte[fileInputStream.available()];
            if (fileInputStream.read(bytes) > 0) {
                OutputStream outputStream = response.getOutputStream();
                outputStream.write(bytes);
                outputStream.close();
            }
            fileInputStream.close();
        }
    }

}

