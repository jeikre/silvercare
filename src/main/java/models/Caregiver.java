package models;

public class Caregiver {
    private int caregiverId;
    private String name;
    private String bio;
    private String qualifications;
    private String languages;
    private int experienceYears;
    private double hourlyRate;
    private String photoPath;
    private String status;

    public int getCaregiverId() { return caregiverId; }
    public void setCaregiverId(int caregiverId) { this.caregiverId = caregiverId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getQualifications() { return qualifications; }
    public void setQualifications(String qualifications) { this.qualifications = qualifications; }

    public String getLanguages() { return languages; }
    public void setLanguages(String languages) { this.languages = languages; }

    public int getExperienceYears() { return experienceYears; }
    public void setExperienceYears(int experienceYears) { this.experienceYears = experienceYears; }

    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
