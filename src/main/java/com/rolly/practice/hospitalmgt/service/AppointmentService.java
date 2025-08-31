package com.rolly.practice.hospitalmgt.service;
;
import com.rolly.practice.hospitalmgt.dto.AppointmentResponseDto;
import com.rolly.practice.hospitalmgt.dto.CreateAppointmentRequestDto;
import com.rolly.practice.hospitalmgt.entity.Appointment;
import com.rolly.practice.hospitalmgt.entity.Doctor;
import com.rolly.practice.hospitalmgt.entity.Patient;
import com.rolly.practice.hospitalmgt.repository.AppointmentRepository;
import com.rolly.practice.hospitalmgt.repository.DoctorRepository;
import com.rolly.practice.hospitalmgt.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final ModelMapper modelMapper;
    private final RedisService redisService;
    //private final EmailService emailService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    @Secured("ROLE_PATIENT")
    public AppointmentResponseDto createNewAppointment(CreateAppointmentRequestDto createAppointmentRequestDto) {
        Long doctorId = createAppointmentRequestDto.getDoctorId();
        Long patientId = createAppointmentRequestDto.getPatientId();

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with ID: " + patientId));
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found with ID: " + doctorId));
        Appointment appointment = Appointment.builder()
                .reason(createAppointmentRequestDto.getReason())
                .appointmentTime(createAppointmentRequestDto.getAppointmentTime())
                .build();

        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        patient.getAppointments().add(appointment); // to maintain consistency
        doctor.getAppointments().add(appointment); // to maintain consistency
        appointment = appointmentRepository.save(appointment);
        redisService.set("doctorId_"+doctor.getId(), doctor.getAppointments(), 300l);

        try{
            kafkaTemplate.send("appointment-mails", "camroonrolly@gmail.com", "test email body");
        }catch (Exception e){
            //emailService.sendEmail("camroonrolly@gmail.com", "Hospital Management test email", "test body");


            System.out.println("email service not working, email sent");
        }


        //"\"Appointment(id=4, appointmentTime=2025-08-29T15:45:41.424703, reason=fever)\



        return modelMapper.map(appointment, AppointmentResponseDto.class);
    }

    @Transactional
    @PreAuthorize("hasAuthority('appointment:write') or #doctorId == authentication.principal.id")
    public Appointment reAssignAppointmentToAnotherDoctor(Long appointmentId, Long doctorId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow();
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow();

        appointment.setDoctor(doctor); // this will automatically call the update, because it is dirty

        doctor.getAppointments().add(appointment); // just for bidirectional consistency

        return appointment;
    }

    @PreAuthorize("hasRole('ADMIN') OR (hasRole('DOCTOR') AND #doctorId == authentication.principal.id)")
    public List<AppointmentResponseDto> getAllAppointmentsOfDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow();
        Doctor docApp = redisService.get("doctorId_"+doctor.getId(), Doctor.class);
        if(docApp != null){
            return docApp.getAppointments()
                .stream()
                .map(appointment -> modelMapper.map(appointment, AppointmentResponseDto.class))
                .collect(Collectors.toList());
        }
        return doctor.getAppointments()
                .stream()
                .map(appointment -> modelMapper.map(appointment, AppointmentResponseDto.class))
                .collect(Collectors.toList());
    }
}
