package com.fullteaching.e2e.no_elastest.utils;

import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = 694253668952718366L;

    private String name;
    private String password;
    private String role;


    public User(String name, String password, String role) {
        this.name = name;
        this.password = password;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRoles(String role) {
        this.role = role;
    }

    public String getUserCsv() {
        return "" + name + "," + password + "," + getRolesCsv();
    }

    private String getRolesCsv() {
        return role;
    }


}
