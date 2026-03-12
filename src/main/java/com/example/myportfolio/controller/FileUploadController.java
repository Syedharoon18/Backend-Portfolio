package com.example.myportfolio.controller;

import com.example.myportfolio.entity.Resume;
import com.example.myportfolio.entity.ResumeSettings;
import com.example.myportfolio.repository.ResumeRepository;
import com.example.myportfolio.repository.ResumeSettingsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    private final ResumeSettingsRepository resumeSettingsRepository;
    private final ResumeRepository resumeRepository;

    public FileUploadController(ResumeSettingsRepository resumeSettingsRepository, ResumeRepository resumeRepository) {
        this.resumeSettingsRepository = resumeSettingsRepository;
        this.resumeRepository = resumeRepository;
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

        // Save legacy single-link settings (keeps backward compatibility)
        ResumeSettings settings = resumeSettingsRepository.findById(1L).orElse(new ResumeSettings());
        settings.setId(1L);
        settings.setResumeUrl(resumeUrl);
        resumeSettingsRepository.save(settings);

        // If additional resume fields are provided, store a full Resume record
        String name = payload.get("name");
        String title = payload.get("title");
        String education = payload.get("education");
        String internship = payload.get("internship");
        String skills = payload.get("skills");

        if ((name != null && !name.isBlank()) || (title != null && !title.isBlank()) ||
                (education != null && !education.isBlank()) || (internship != null && !internship.isBlank()) ||
                (skills != null && !skills.isBlank())) {

            Resume resume = new Resume();
            resume.setName(name);
            resume.setTitle(title);
            resume.setEducation(education);
            resume.setInternship(internship);
            resume.setSkills(skills);
            resume.setResumeUrl(resumeUrl);
            resume.setUpdatedAt(LocalDateTime.now());

            resumeRepository.save(resume);
        }

        return ResponseEntity
                .ok(Map.of("message", "Resume Link updated permanently to Cloudinary", "resumeUrl", resumeUrl));
    }

    @GetMapping("/resume")
    public ResponseEntity<?> getGlobalResumeUrl() {
        // Prefer the latest Resume record if available
        try {
            Resume latest = resumeRepository.findTopByOrderByIdDesc();
            if (latest != null) {
                return ResponseEntity.ok(Map.of(
                        "id", latest.getId(),
                        "name", latest.getName(),
                        "title", latest.getTitle(),
                        "education", latest.getEducation(),
                        "internship", latest.getInternship(),
                        "skills", latest.getSkills(),
                        "resumeUrl", latest.getResumeUrl(),
                        "updatedAt", latest.getUpdatedAt()
                ));
            }
        } catch (Exception e) {
            // ignore and fall back to settings
        }

        ResumeSettings settings = resumeSettingsRepository.findById(1L).orElse(null);
        if (settings == null || settings.getResumeUrl() == null || settings.getResumeUrl().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No resume found."));
        }
        return ResponseEntity.ok(Map.of("resumeUrl", settings.getResumeUrl()));
    }
}
