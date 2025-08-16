package com.rolly.practice.hospitalmgt.repository;


import com.rolly.practice.hospitalmgt.entity.Insurance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsuranceRepository extends JpaRepository<Insurance, Long> {
}