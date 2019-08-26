package com.hellochiu.soap;

import org.ksoap2.serialization.SoapObject;

public class CourseService {

    public static final String NONE = "none";
    public static final String ADD_COURSE_SUCCESS = "加入成功";
    private ServiceUtil util = new ServiceUtil();

    public String getSelectedCourses(String stuId) {
        SoapObject result = util.callService("GetSelectedCourses", "stuId", stuId);
        return result == null ? null : result.getPropertyAsString(0);
    }

    public String getCreatedCourses(String tchId) {
        SoapObject result = util.callService("GetCreatedCourses", "tchId", tchId);
        return result == null ? null : result.getPropertyAsString(0);
    }

    public String getCourseStuDetails(String courseId) {
        SoapObject result = util.callService("GetCourseStuDetails", "courseId", courseId);
        return result == null ? null : result.getPropertyAsString(0);
    }

    public String getAllCourses() {
        SoapObject result = util.callService("GetAllCourses");
        return result == null ? null : result.getPropertyAsString(0);
    }

    public String addCourse(String stuId, String courseId) {
        SoapObject result = util.callService("AddCourse", "stuId", stuId, "courseId", courseId);
        return result == null ? null : result.getPropertyAsString(0);
    }

    public String createCourse(String tchId, String courseName, String coursePwd, String beginTime, String endTime) {
        SoapObject result = util.callService("CreateCourse", "tchId", tchId, "courseName", courseName,
                "coursePwd", coursePwd, "beginTime", beginTime, "endTime", endTime);
        return result == null ? null : result.getPropertyAsString(0);
    }

}
