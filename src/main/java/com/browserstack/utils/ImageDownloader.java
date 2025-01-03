package com.browserstack.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ImageDownloader {
    public static void downloadImage(String imageUrl, String fileName) {
        try {
            URL url = new URL(imageUrl);
            String timestamp = String.valueOf(System.currentTimeMillis());
            String sanitizedFileName = fileName.replaceAll("[^a-zA-Z0-9]", "_");
            Path destinationPath = Paths.get("images", sanitizedFileName + "_" + timestamp + ".jpg");

            // Create images directory if it doesn't exist
            Files.createDirectories(destinationPath.getParent());

            // Download the image
            try (InputStream in = url.openStream()) {
                Files.copy(in, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            System.err.println("Error downloading image: " + e.getMessage());
        }
    }
}
