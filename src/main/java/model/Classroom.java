package model;

public class Classroom {
    private String classroomId ; // doesn't change

    public Classroom(String classroomId) {
        this.classroomId = classroomId;
    }

    public String getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(String classroomId) {
        this.classroomId = classroomId;
    }
}
