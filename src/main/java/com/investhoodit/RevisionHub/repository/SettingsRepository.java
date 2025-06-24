package com.investhoodit.RevisionHub.repository;

import com.investhoodit.RevisionHub.model.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingsRepository extends JpaRepository<Settings, String> {
}