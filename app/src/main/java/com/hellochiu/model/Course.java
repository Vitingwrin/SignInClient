package com.hellochiu.model;

import java.util.ArrayList;
import java.util.List;

public class Course {

    private String courseId;
    private String courseName;
    private String coursePwd;
    private String beginTime;
    private String endTime;
    private String tchName;
    private String tchId;
    public static final List<Course> SELECTED_COURSE = new ArrayList<>();
    public static final List<Course> CREATED_COURSE = new ArrayList<>();
    public static final List<Course> ALL_COURSE = new ArrayList<>();
    public static boolean isCourseInfoLoaded() {
        return !SELECTED_COURSE.isEmpty();
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCoursePwd() {
        return coursePwd;
    }

    public void setCoursePwd(String coursePwd) {
        this.coursePwd = coursePwd;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTchName() {
        return tchName;
    }

    public void setTchName(String tchName) {
        this.tchName = tchName;
    }

    public String getTchId() {
        return tchId;
    }

    public void setTchId(String tchId) {
        this.tchId = tchId;
    }
}
