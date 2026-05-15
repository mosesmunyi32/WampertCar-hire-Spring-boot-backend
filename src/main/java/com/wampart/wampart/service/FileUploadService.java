package com.wampart.wampart.service;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileUploadService {
    private final Cloudinary cloudinary;

    //uploading a single file
    public String uploadFile(MultipartFile file, String folder) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder,
                            "resource_type", "auto"
                    )
            );
            return uploadResult.get("secure_url").toString();

        } catch (IOException e) {
            throw new RuntimeException (
                    "Failed to upload the file: " + e.getMessage()
            );
        }

    }

    public List<String> uploadMultipleFiles(List<MultipartFile> files, String folder ) {
        List<String> urls = new ArrayList<>();

        for(MultipartFile file : files) {
            String url = uploadFile(file, folder);
            urls.add(url);
        }
        return urls;
    }

    public void deleteFile(String fileUrl) {
        try {
            String publicId = extractPublicId(fileUrl);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

        } catch (IOException e) {
            throw new RuntimeException (
                    "Failed to delete the file: " + e.getMessage()
            );


        }



    }

    //you have a file of this nature ====https://res.cloudinary.com/demo/image/upload/v1746780000/cars/toyota-corolla.jpg======

    private String extractPublicId(String fileUrl) {

        String[] parts = fileUrl.split("/");

        String fileWithExtension = parts[parts.length - 1];
        String folder = parts[parts.length - 2];

        String fileName = fileWithExtension.split("\\.")[0];

        return folder + "/" + fileName;

    }




}
