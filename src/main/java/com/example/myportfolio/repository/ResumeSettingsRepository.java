package com.example.myportfolio.repository;

import com.example.myportfolio.entity.ResumeSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeSettingsRepository extends JpaRepository<ResumeSettings, Long> {
}
