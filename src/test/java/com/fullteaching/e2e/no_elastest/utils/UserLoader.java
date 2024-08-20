package com.fullteaching.e2e.no_elastest.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class UserLoader {

    public final static int USERNAME = 0;
    public final static int PASSWORD = 1;
    public final static int ROLES = 2;
    private static final String cvsMainFieldsSplitBy = ",";
    private static final String usersDefaultFile = "src/test/resources/inputs/default_user_file.csv";
    private static final String oneTeacherMultipleStudentsFile = "src/test/resources/inputs/session_test_file.csv";
    private static Map<String, User> users;

    public static void loadUsers(List<User> userList, boolean override) {
        if (override || users == null)
            users = new HashMap<>();

        for (User i : userList) users.put(i.getName(), i);

    }

    public static void loadUsers(String usersFile, boolean override) throws IOException {
        //read file and create users
        List<User> userList = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(usersDefaultFile));
        String line;
        while ((line = br.readLine()) != null) {
            userList.add(parseUser(line));
        }
        loadUsers(userList, override);
    }

    public static void loadUsers(String usersFile) throws IOException {
        loadUsers(usersFile, false);
    }

    public static void loadUsers() throws IOException {
        loadUsers(usersDefaultFile);
    }

    public static User parseUser(String cvsline) {
        String[] field = cvsline.split(cvsMainFieldsSplitBy);
        return new User(field[USERNAME],
                field[PASSWORD],
                field[ROLES]);
    }

    public static Collection<String[]> getSessionParameters() throws IOException {
        String line;

        List<String[]> paramList = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(oneTeacherMultipleStudentsFile));
        while ((line = br.readLine()) != null) {
            paramList.add(line.split(cvsMainFieldsSplitBy));
        }
        return paramList;
    }

    public static User retrieveUser(String name) {
        return users.get(name);
    }

    public static Collection<User> getAllUsers() throws IOException {
        if (users == null) {
            loadUsers();
        }
        return users.values();
    }

}