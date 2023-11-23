package com.Arjunagi.DoctorApp.models.doctorRecord;

public enum DoctorQualification {
    MBBS("Bachelor of Medicine, Bachelor of Surgery"),
    MD("Doctor of Medicine"),
    MS("Master of Surgery"),
    DM("Doctor of Medicine (Super Specialization)"),
    MCh("Master of Chirurgiae (Super Specialization)"),
    BDS("Bachelor of Dental Surgery"),
    MDS("Master of Dental Surgery"),
    BAMS("Bachelor of Ayurvedic Medicine and Surgery"),
    BHMS("Bachelor of Homeopathic Medicine and Surgery"),
    BUMS("Bachelor of Unani Medicine and Surgery"),
    BNYS("Bachelor of Naturopathy and Yogic Sciences"),
    DHMS("Diploma in Homeopathic Medicine and Surgery"),
    DPT("Diploma in Physiotherapy"),
    BPT("Bachelor of Physiotherapy"),
    BSc_Nursing("Bachelor of Science in Nursing"),
    MSc_Nursing("Master of Science in Nursing"),
    Other("Other Qualification");

    private String description;

    DoctorQualification(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

