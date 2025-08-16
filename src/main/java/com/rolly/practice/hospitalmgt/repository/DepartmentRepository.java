package com.rolly.practice.hospitalmgt.repository;


import com.rolly.practice.hospitalmgt.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}