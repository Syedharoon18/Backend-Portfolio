package com.example.myportfolio.repository;

import com.example.myportfolio.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {

    Resume findTopByOrderByIdDesc();

}
