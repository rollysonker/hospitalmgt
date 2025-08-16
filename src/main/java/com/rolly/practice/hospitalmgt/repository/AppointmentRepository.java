package com.rolly.practice.hospitalmgt.repository;

import com.rolly.practice.hospitalmgt.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
}