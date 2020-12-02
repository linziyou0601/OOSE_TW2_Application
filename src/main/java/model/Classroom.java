package model;

public class Classroom {
    private String id; // doesn't change

    public Classroom(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
