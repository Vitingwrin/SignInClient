package com.hellochiu.model;

public class Student {

    private String stuId;
    private String stuName;
    private String stuEmail;
    private String stuPhone;
    public static final Student CURRENT_USER = new Student();
    public static boolean isStudentInfoLoaded() {
        return !"".equals(CURRENT_USER.getStuName());
    }

    public String getStuId() {
        return stuId;
    }

    public void setStuId(String stuId) {
        this.stuId = stuId;
    }

    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }

    public String getStuEmail() {
        return stuEmail;
    }

    public void setStuEmail(String stuEmail) {
        this.stuEmail = stuEmail;
    }

    public String getStuPhone() {
        return stuPhone;
    }

    public void setStuPhone(String stuPhone) {
        this.stuPhone = stuPhone;
    }

}
