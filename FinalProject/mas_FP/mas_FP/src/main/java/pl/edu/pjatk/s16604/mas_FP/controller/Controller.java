package pl.edu.pjatk.s16604.mas_FP.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pjatk.s16604.mas_FP.DTO.DoctorDTO;
import pl.edu.pjatk.s16604.mas_FP.DTO.MeetingDTO;
import pl.edu.pjatk.s16604.mas_FP.DTO.PatientDTO;
import pl.edu.pjatk.s16604.mas_FP.DTO.ReferralDTO;
import pl.edu.pjatk.s16604.mas_FP.DTO.historyVisitDTO;
import pl.edu.pjatk.s16604.mas_FP.entity.Doctor;
import pl.edu.pjatk.s16604.mas_FP.entity.Patient;
import pl.edu.pjatk.s16604.mas_FP.service.DivisionService;
import pl.edu.pjatk.s16604.mas_FP.service.DoctorService;
import pl.edu.pjatk.s16604.mas_FP.service.PatientService;
import pl.edu.pjatk.s16604.mas_FP.service.ReferralService;
import pl.edu.pjatk.s16604.mas_FP.service.VisitService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class Controller {

    @Autowired
    PatientService patientService;

    @Autowired
    DoctorService doctorService;

    @Autowired
    DivisionService divisionService;

    @Autowired
    VisitService visitService;

    @Autowired
    ReferralService referralService;


    // to search all patients
    @GetMapping({"/patient"})
    public List<Patient> getAllPatients() {
        return patientService.findAllPatient();
    }

    @GetMapping({"/patient/{string}"})
    public List<Patient> searchPatientByString(@PathVariable String string) {
        return patientService.searchPatientByString(string);
    }

    // patient details
    @GetMapping({"/patient/history/{patientId}"})
    public List<historyVisitDTO> searchPatientAppHistory(@PathVariable long patientId) {
        return patientService.searchPatientAppHistory(patientId);
    }

    @GetMapping({"/patient/referrals/{patientId}"})
    public List<ReferralDTO> searchPatientRefHistory(@PathVariable long patientId) {
        return patientService.searchPatientRefHistory(patientId);
    }

    //patient edit and create
    @PutMapping({"/patient/{patientId}/edit"})
    public ResponseEntity<Void> updatePatient(@PathVariable long patientId, @RequestBody PatientDTO patientDTO) {
        Patient patientFound = patientService.searchPatientById(patientId);
        if (patientFound != null) {
            patientService.updatePatient(patientId, patientDTO);
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/patient")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void createPatient(@RequestBody PatientDTO patientDTO) {
        patientService.addPatient(patientDTO);
    }


    // to search all doctors
    @GetMapping({"/doctor"})
    public List<DoctorDTO> getAllDoctors() {
        return doctorService.getAllDoctors();
    }


    @GetMapping({"/appointment/{doctorId}/{patientId}/{dateFromSt}/{dateToSt}/{hasReferral}"})
    public List<LocalDateTime> getSpots(@PathVariable long doctorId,
                                        @PathVariable long patientId,
                                        @PathVariable String dateFromSt,
                                        @PathVariable String dateToSt,
                                        @PathVariable boolean hasReferral) {
        Doctor doctor = doctorService.searchDoctorById(doctorId);
        Patient patient = patientService.searchPatientById(patientId);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime dateFrom = LocalDate.parse(dateFromSt, dtf).atStartOfDay();
        LocalDateTime dateTo = LocalDate.parse(dateToSt, dtf).atStartOfDay();

        if (patient != null && doctor != null) {
            return doctorService.searchByCriteria(doctor, patient, dateFrom, dateTo, hasReferral);
        }
        return new ArrayList<>();
    }

    // booking of the meeting
    @PostMapping(value = "/appointment")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void createMeeting(@RequestBody MeetingDTO meetingDTO) {
        visitService.addMeeting(meetingDTO);
    }

}
