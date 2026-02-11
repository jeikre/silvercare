package models;

public class TimeSlot {
    private int slotId;
    private int serviceId;

    // DB columns
    private String displayLabel;  // e.g. "06:00 AM – 07:00 AM"
    private String timeValue;     // "HH:mm:ss" (from TIME)
    private String startTime;     // optional (varchar) can be null
    private String endTime;       // optional (varchar) can be null

    // computed availability (set by servlet/DAO)
    private int bookedCount;      // how many booked for selected date
    private int remaining;        // capacity - bookedCount

    public TimeSlot() {}

    public TimeSlot(int slotId, int serviceId, String displayLabel, String timeValue, String startTime, String endTime) {
        this.slotId = slotId;
        this.serviceId = serviceId;
        this.displayLabel = displayLabel;
        this.timeValue = timeValue;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getSlotId() { return slotId; }
    public void setSlotId(int slotId) { this.slotId = slotId; }

    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }

    public String getDisplayLabel() { return displayLabel; }
    public void setDisplayLabel(String displayLabel) { this.displayLabel = displayLabel; }

    public String getTimeValue() { return timeValue; }
    public void setTimeValue(String timeValue) { this.timeValue = timeValue; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public int getBookedCount() { return bookedCount; }
    public void setBookedCount(int bookedCount) { this.bookedCount = bookedCount; }

    public int getRemaining() { return remaining; }
    public void setRemaining(int remaining) { this.remaining = remaining; }
}
