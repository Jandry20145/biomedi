package ec.edu.ucacue.odontocare.patient.domain;

import java.time.Instant;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import ec.edu.ucacue.odontocare.shared.util.AesEncryptor;

@Entity
@Table(name = "patients")
public class Patient {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "clinical_record_number", nullable = false, unique = true, length = 40)
    private String clinicalRecordNumber;
    @Column(unique = true, length = 255)
    @Convert(converter = AesEncryptor.class)
    private String identification;
    @Column(name = "responsible_name", nullable = false, length = 120)
    private String responsibleName;
    @Column(name = "first_names", nullable = false, length = 120)
    private String firstNames;
    @Column(name = "last_names", nullable = false, length = 120)
    private String lastNames;
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;
    @Column(nullable = false, length = 30)
    private String sex;
    @Column(nullable = false, length = 500)
    @Convert(converter = AesEncryptor.class)
    private String address;
    @Column(length = 255)
    @Convert(converter = AesEncryptor.class)
    private String phone;
    @Column(length = 255)
    @Convert(converter = AesEncryptor.class)
    private String email;
    @Column(nullable = false)
    private boolean active = true;
    @Column(name = "deleted_at")
    private Instant deletedAt;
    @Column(name = "created_by")
    private Long createdBy;
    @Column(name = "updated_by")
    private Long updatedBy;

    protected Patient() {}

    public Patient(String clinicalRecordNumber, String identification, String responsibleName, String firstNames, String lastNames,
            LocalDate birthDate, String sex, String address, String phone, String email, Long createdBy) {
        update(clinicalRecordNumber, identification, responsibleName, firstNames, lastNames, birthDate, sex, address, phone, email, createdBy);
        this.createdBy = createdBy;
    }

    public void update(String clinicalRecordNumber, String identification, String responsibleName, String firstNames, String lastNames,
            LocalDate birthDate, String sex, String address, String phone, String email, Long updatedBy) {
        this.clinicalRecordNumber = clinicalRecordNumber != null ? clinicalRecordNumber.trim() : null;
        this.identification = (identification != null && !identification.trim().isEmpty()) ? identification.trim() : null;
        this.responsibleName = (responsibleName != null && !responsibleName.trim().isEmpty()) ? responsibleName.trim() : "";
        this.firstNames = firstNames != null ? firstNames.trim() : null;
        this.lastNames = lastNames != null ? lastNames.trim() : null;
        this.birthDate = birthDate;
        this.sex = sex != null ? sex.trim() : null;
        this.address = (address != null && !address.trim().isEmpty()) ? address.trim() : "";
        this.phone = (phone != null && !phone.trim().isEmpty()) ? phone.trim() : null;
        this.email = (email != null && !email.trim().isEmpty()) ? email.trim().toLowerCase() : null;
        this.updatedBy = updatedBy;
    }

    public void setClinicalRecordNumber(String clinicalRecordNumber) {
        this.clinicalRecordNumber = clinicalRecordNumber != null ? clinicalRecordNumber.trim() : null;
    }

    public void deactivate(Long userId) { this.active = false; this.deletedAt = Instant.now(); this.updatedBy = userId; }
    public Long getId() { return id; } public String getClinicalRecordNumber() { return clinicalRecordNumber; }
    public String getIdentification() { return identification; } public String getResponsibleName() { return responsibleName; } public String getFirstNames() { return firstNames; }
    public String getLastNames() { return lastNames; } public LocalDate getBirthDate() { return birthDate; }
    public String getSex() { return sex; } public String getAddress() { return address; }
    public String getPhone() { return phone; } public String getEmail() { return email; }
}

