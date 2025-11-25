package com.lms.model;

public class Course {
    private int id;
    private String courseName;
    private String description;
    private int instructorId;

    public Course() {
    }

    public Course(int id, String courseName, String description, int instructorId) {
        this.id = id;
        this.courseName = courseName;
        this.description = description;
        this.instructorId = instructorId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(int instructorId) {
        this.instructorId = instructorId;
    }
}
