package com.wampert.wampert.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    @Value("${file.upload.dir:/var/www/sites/wampertcar/uploads}")
    private String uploadDir;

    @Value("${file.base.url:https://api.warmpertcar.site/uploads}")
    private String baseUrl;

    // Upload a single file
    public String uploadFile(MultipartFile file, String folder) {
        try {
            // Validate file
            if (file.isEmpty()) {
                throw new RuntimeException("File is empty");
            }

            // Create folder if it doesn't exist
            String folderPath = uploadDir + "/" + folder;
            File folderDir = new File(folderPath);
            if (!folderDir.exists()) {
                folderDir.mkdirs();
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID() + fileExtension;

            // Save file
            Path filePath = Paths.get(folderPath, uniqueFilename);
            Files.write(filePath, file.getBytes());

            // Return the URL
            return baseUrl + "/" + folder + "/" + uniqueFilename;

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload the file: " + e.getMessage());
        }
    }

    // Upload multiple files
    public List<String> uploadMultipleFiles(List<MultipartFile> files, String folder) {
        List<String> urls = new ArrayList<>();

        for (MultipartFile file : files) {
            String url = uploadFile(file, folder);
            urls.add(url);
        }
        return urls;
    }

    // Delete a file
    public void deleteFile(String fileUrl) {
        try {
            // Extract filename from URL
            String filename = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            String folder = fileUrl.substring(fileUrl.lastIndexOf("/", fileUrl.lastIndexOf("/") - 1) + 1, fileUrl.lastIndexOf("/"));

            // Delete the file
            Path filePath = Paths.get(uploadDir, folder, filename);
            Files.deleteIfExists(filePath);

        } catch (IOException e) {
            throw new RuntimeException("Failed to delete the file: " + e.getMessage());
        }
    }
}
