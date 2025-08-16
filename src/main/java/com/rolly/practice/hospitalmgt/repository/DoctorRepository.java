package com.rolly.practice.hospitalmgt.repository;


import com.rolly.practice.hospitalmgt.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}