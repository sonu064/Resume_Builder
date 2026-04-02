package com.resume.builder.service;

import com.resume.builder.api.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;

@Service
public class FileStorageService {

  private static final long MAX_BYTES = 2 * 1024 * 1024; // 2MB

  private final Path uploadsRoot;

  public FileStorageService(@Value("${app.uploads.dir}") String uploadsDir) {
    this.uploadsRoot = Path.of(uploadsDir);
  }

  public String storeProfileImage(Long userId, MultipartFile image) {
    if (image == null || image.isEmpty()) {
      throw new BadRequestException("Image is required");
    }
    if (image.getSize() > MAX_BYTES) {
      throw new BadRequestException("Image must be <= 2MB");
    }

    String contentType = image.getContentType() == null ? "" : image.getContentType().toLowerCase(Locale.ROOT);
    String ext = guessExtension(contentType, image.getOriginalFilename());

    try {
      String fileName = "profile-" + userId + "-" + UUID.randomUUID() + "." + ext;
      Path dir = uploadsRoot.resolve("profile-images").resolve(String.valueOf(userId));
      Files.createDirectories(dir);

      try (InputStream in = image.getInputStream()) {
        Files.copy(in, dir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
      }

      // Static mapping is configured in application.yml for the `uploads` directory.
      // The file will be accessible at `/profile-images/{userId}/{fileName}`.
      return "/profile-images/" + userId + "/" + fileName;
    } catch (IOException e) {
      throw new BadRequestException("Failed to store image");
    }
  }

  private static String guessExtension(String contentType, String originalFilename) {
    if (contentType.contains("png")) return "png";
    if (contentType.contains("jpeg") || contentType.contains("jpg")) return "jpg";
    if (contentType.contains("gif")) return "gif";

    if (originalFilename != null && originalFilename.contains(".")) {
      String tail = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
      if (tail.length() <= 5) return tail;
    }
    return "png";
  }
}

