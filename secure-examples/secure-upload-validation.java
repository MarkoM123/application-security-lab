package secure.examples;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

/**
 * Secure upload validation example.
 *
 * Before:
 * - Original filename was trusted and used directly in storage path.
 * - Type and size validation were missing.
 *
 * Improved:
 * - Strict file allowlist and size cap.
 * - Server-generated filename and normalized destination path.
 */
public class SecureUploadValidationExample {

    private static final long MAX_SIZE_BYTES = 5 * 1024 * 1024; // 5 MB
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("application/pdf", "image/png", "image/jpeg");
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".pdf", ".png", ".jpg", ".jpeg");

    private final Path uploadDir;

    public SecureUploadValidationExample(Path uploadDir) throws IOException {
        this.uploadDir = uploadDir;
        Files.createDirectories(uploadDir);
    }

    public StoredFile store(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }
        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new IllegalArgumentException("File exceeds maximum size");
        }
        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Unsupported content type");
        }

        String safeExt = resolveAllowedExtension(file.getOriginalFilename());
        String generatedName = UUID.randomUUID() + safeExt;
        Path destination = uploadDir.resolve(generatedName).normalize();

        if (!destination.startsWith(uploadDir)) {
            throw new IllegalArgumentException("Invalid upload path");
        }

        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return new StoredFile(generatedName, file.getSize());
    }

    private String resolveAllowedExtension(String originalName) {
        if (originalName == null) {
            throw new IllegalArgumentException("Filename is missing");
        }

        int dotIndex = originalName.lastIndexOf('.');
        String ext = dotIndex >= 0 ? originalName.substring(dotIndex).toLowerCase() : "";
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new IllegalArgumentException("Unsupported file extension");
        }
        return ext;
    }

    public static class StoredFile {
        private final String fileName;
        private final long size;

        public StoredFile(String fileName, long size) {
            this.fileName = fileName;
            this.size = size;
        }

        public String getFileName() {
            return fileName;
        }

        public long getSize() {
            return size;
        }
    }
}
