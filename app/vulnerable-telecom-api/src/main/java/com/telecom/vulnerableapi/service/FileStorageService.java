package com.telecom.vulnerableapi.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    private final Path uploadDir = Paths.get("uploads");

    public FileStorageService() {
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to create upload directory", ex);
        }
    }

    public String store(MultipartFile file) {
        try {
            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null || originalFileName.isBlank()) {
                originalFileName = "unnamed-upload.bin";
            }

            // VULNERABILITY: No MIME/type/size/content validation for uploaded files.
            // VULNERABILITY: Untrusted filename is used directly to build destination path.
            Path destination = uploadDir.resolve(originalFileName);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            return destination.toAbsolutePath().toString();
        } catch (IOException ex) {
            throw new IllegalStateException("File upload failed", ex);
        }
    }
}

