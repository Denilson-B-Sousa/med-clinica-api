package br.edu.ifg.med_clinica_api.domain.dto.appointment;

import br.edu.ifg.med_clinica_api.domain.entity.Address;
import br.edu.ifg.med_clinica_api.domain.entity.Appointment;
import br.edu.ifg.med_clinica_api.domain.enums.AppointmentStatus;
import br.edu.ifg.med_clinica_api.domain.enums.MedicalSpeciality;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public record AdminAppointmentDTO(
        UUID id,
        LocalDate date,
        LocalTime time,
        LocalDateTime scheduleAt,
        UUID clinicUnitId,
        String clinicUnitName,
        String clinicUnitAddress,
        UUID doctorId,
        String doctorName,
        UUID patientId,
        String patientName,
        MedicalSpeciality speciality,
        AppointmentStatus status,
        Integer durationInMinutes,
        boolean canCancel
) {
    public AdminAppointmentDTO(Appointment appointment) {
        this(
                appointment.getId(),
                appointment.getScheduleAt().toLocalDate(),
                appointment.getScheduleAt().toLocalTime(),
                appointment.getScheduleAt(),
                appointment.getClinicUnit() != null ? appointment.getClinicUnit().getId() : null,
                appointment.getClinicUnit() != null ? appointment.getClinicUnit().getName() : null,
                appointment.getClinicUnit() != null ? formatAddress(appointment.getClinicUnit().getAddress()) : null,
                appointment.getDoctor().getId(),
                appointment.getDoctor().getName(),
                appointment.getPatient().getId(),
                appointment.getPatient().getName(),
                appointment.getDoctor().getSpeciality(),
                appointment.getStatus(),
                appointment.getDurationInMinutes(),
                appointment.getStatus() != AppointmentStatus.COMPLETED
                        && appointment.getStatus() != AppointmentStatus.CANCELED
        );
    }

    private static String formatAddress(Address address) {
        if (address == null) {
            return null;
        }

        var mainAddress = joinNonBlank(", ", address.getStreet(), address.getNumber());
        var district = address.getDistrict();

        if (isBlank(mainAddress)) {
            return district;
        }

        if (isBlank(district)) {
            return mainAddress;
        }

        return mainAddress + " - " + district;
    }

    private static String joinNonBlank(String delimiter, String... values) {
        return java.util.Arrays.stream(values)
                .filter(value -> !isBlank(value))
                .reduce((left, right) -> left + delimiter + right)
                .orElse(null);
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
