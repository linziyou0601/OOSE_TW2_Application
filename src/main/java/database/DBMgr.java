package database;

import model.Classroom;
import model.User;

import java.util.ArrayList;
import java.util.List;

public class DBMgr {
    private List<User> userStorage = new ArrayList<>();
    private List<Classroom> classroomStorage = new ArrayList<>();

    public DBMgr() {

    }

    // For User
    public void insertUser(User user) {
        userStorage.add(user);
    }

    public List<User> getUsers() {
        return userStorage;
    }

    public User getUserByAccount(String account) {
        for(User user: userStorage){
            if(user.getAccount().equals(account))
                return user;
        }
        return null;
    }

    // For Classroom
    public void insertClassroom(Classroom classroom) {
        classroomStorage.add(classroom);
    }

    public List<Classroom> getClassrooms() {
        return classroomStorage;
    }

    public Classroom getClassroomById(String account) {
        for(Classroom classroom: classroomStorage){
            if(classroom.getId().equals(account))
                return classroom;
        }
        return null;
    }
}
