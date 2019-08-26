package com.hellochiu.model;

public class Teacher {

    private String tchId;
    private String tchName;
    private String tchEmail;
    private String tchPhone;
    public static Course course = new Course();
    public static boolean signing = false;
    public static final Teacher CURRENT_USER = new Teacher();
    public static boolean isTeacherInfoLoaded() {
        return !"".equals(CURRENT_USER.tchName);
    }

    public String getTchId() {
        return tchId;
    }

    public void setTchId(String tchId) {
        this.tchId = tchId;
    }

    public String getTchName() {
        return tchName;
    }

    public void setTchName(String tchName) {
        this.tchName = tchName;
    }

    public String getTchEmail() {
        return tchEmail;
    }

    public void setTchEmail(String tchEmail) {
        this.tchEmail = tchEmail;
    }

    public String getTchPhone() {
        return tchPhone;
    }

    public void setTchPhone(String tchPhone) {
        this.tchPhone = tchPhone;
    }
}
