package com.investhoodit.RevisionHub.repository;

import com.investhoodit.RevisionHub.model.Resources;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<Resources, Long> {
}
