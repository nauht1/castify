package com.castify.backend.controller;

import com.castify.backend.models.podcast.CreatePodcastModel;
import com.castify.backend.models.podcast.PodcastModel;
import com.castify.backend.models.user.UserModel;
import com.castify.backend.service.IUserService;
import com.castify.backend.service.UserServiceImpl;
import com.castify.backend.service.podcast.IPodcastService;
import com.castify.backend.service.podcast.PodcastServiceImpl;
import com.castify.backend.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

@RestController
@RequestMapping("/api/v1/podcast")
public class PodcastController {
    @Autowired
    public IPodcastService podcastService = new PodcastServiceImpl();

    @Autowired
    public IUserService userService = new UserServiceImpl();

    @Value("${file.upload-dir}")
    private String baseUploadDir;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPodcast(
            @RequestPart("title") String title,
            @RequestPart("content") String content,
            @RequestPart("video") MultipartFile videoFile) {
        try {
            // Check if null
            if (videoFile == null || videoFile.isEmpty()) {
                throw new RuntimeException("Empty video file");
            }

             // Validate file type
            String fileType = videoFile.getContentType();
            if (!fileType.equals("video/mp4") && !fileType.equals("video/x-msvideo") && !fileType.equals("video/x-matroska")) {
                throw new RuntimeException("Unsupported video format");
            }

            if (videoFile.getSize() > 1024L * 1024L * 1024L) { // 1GB size limit
                throw new RuntimeException("File size exceeds limit of 1GB");
            }

            UserModel userModel = userService.getUserByToken();

            // Create user-specific directory
            Path userPodcastDir = FileUtils.createUserDirectory(baseUploadDir, userModel.getId(), userModel.getEmail(), "podcast");

            // Format fileName
            String formattedFileName = FileUtils.formatFileName(videoFile.getOriginalFilename());

            // Save video temporarily
            Path videoPath = userPodcastDir.resolve(formattedFileName);
            videoFile.transferTo(videoPath.toFile());

            CreatePodcastModel createPodcastModel = new CreatePodcastModel(title, content);

            // Call service and pass video file path
            PodcastModel podcastModel = podcastService.createPodcast(createPodcastModel, videoPath.toString());

            return ResponseEntity.ok(podcastModel);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }
}