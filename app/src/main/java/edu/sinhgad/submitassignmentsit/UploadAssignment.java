package edu.sinhgad.submitassignmentsit;

public class UploadAssignment {

    public String assignmentName;
    public String assignmentUrl;
    String date, time, uploaderName;

    public UploadAssignment() {}

    public UploadAssignment(String assignmentName, String assignmentUrl, String date, String time, String uploaderName) {
        this.assignmentName = assignmentName;
        this.assignmentUrl = assignmentUrl;
        this.date = date;
        this.time = time;
        this.uploaderName = uploaderName;
    }

    public String getAssignmentName() {
        return assignmentName;
    }

    public String getAssignmentUrl() {
        return assignmentUrl;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return  time;
    }

    public String getUploaderName() {
        return uploaderName;
    }

}
