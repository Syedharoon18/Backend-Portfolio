package com.example.myportfolio.controller;

import com.example.myportfolio.entity.ResumeSettings;
import com.example.myportfolio.repository.ResumeSettingsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://your-vercel-domain.vercel.app"
})
public class FileUploadController {

    private final ResumeSettingsRepository resumeSettingsRepository;

    public FileUploadController(ResumeSettingsRepository resumeSettingsRepository) {
        this.resumeSettingsRepository = resumeSettingsRepository;
    }

    // Projects handle their own Cloudinary Image URLs already (they get saved in
    // the Project entity directly)
    // So we only need to manage the Resume URL here globally.

    @PostMapping("/resume")
    public ResponseEntity<?> setGlobalResumeUrl(@RequestBody Map<String, String> payload) {
        String resumeUrl = payload.get("resumeUrl");
        if (resumeUrl == null || resumeUrl.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing resumeUrl string");
        }

        ResumeSettings settings = resumeSettingsRepository.findById(1L).orElse(new ResumeSettings());
        settings.setId(1L);
        settings.setResumeUrl(resumeUrl);

        resumeSettingsRepository.save(settings);

        return ResponseEntity
                .ok(Map.of("message", "Resume Link updated permanently to Cloudinary", "resumeUrl", resumeUrl));
    }

    @GetMapping("/resume")
    public ResponseEntity<?> getGlobalResumeUrl() {
        ResumeSettings settings = resumeSettingsRepository.findById(1L).orElse(null);
        if (settings == null || settings.getResumeUrl() == null || settings.getResumeUrl().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No resume found."));
        }
        return ResponseEntity.ok(Map.of("resumeUrl", settings.getResumeUrl()));
    }
}
