package edu.sinhgad.submitassignmentsit;

public class UploadAssignment {

    public String assignmentName;
    public String assignmentUrl;
    String date, time;

    public UploadAssignment() {}

    public UploadAssignment(String assignmentName, String assignmentUrl, String date, String time) {
        this.assignmentName = assignmentName;
        this.assignmentUrl = assignmentUrl;
        this.date = date;
        this.time = time;
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

}
