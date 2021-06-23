package com.azetech.insentiva;

public class Users {

    String username, name, mobile, password, salary, attendance, isPresent;

    public Users(String username, String name, String mobile, String password, String salary, String attendance, String isPresent) {
        this.username = username;
        this.name = name;
        this.mobile = mobile;
        this.password = password;
        this.salary = salary;
        this.attendance = attendance;
        this.isPresent = isPresent;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSalary() {
        return salary;
    }

    public String getIsPresent() {
        return isPresent;
    }

    public void setIsPresent(String isPresent) {
        this.isPresent = isPresent;
    }

    public String getAttendance() {
        return attendance;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Users() {
    }
}
