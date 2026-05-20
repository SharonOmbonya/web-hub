package com.web_hub.web_hub.projects.repository;

import com.web_hub.web_hub.projects.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
