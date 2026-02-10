package models;

public class TimeSlot {
    private String displayLabel; // e.g. "2:00 PM"
    private String timeValue;    // e.g. "14:00:00"

    public TimeSlot() {}

    public TimeSlot(String displayLabel, String timeValue) {
        this.displayLabel = displayLabel;
        this.timeValue = timeValue;
    }

    public String getDisplayLabel() { return displayLabel; }
    public void setDisplayLabel(String displayLabel) { this.displayLabel = displayLabel; }

    public String getTimeValue() { return timeValue; }
    public void setTimeValue(String timeValue) { this.timeValue = timeValue; }
}
