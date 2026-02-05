package com.mini_crm.main.repository;

import com.mini_crm.main.model.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long>, JpaSpecificationExecutor<Lead> {
    List<Lead> findByStatus(String status);

    List<Lead> findByExpectedCloseDateBetween(LocalDateTime start, LocalDateTime end);
}
