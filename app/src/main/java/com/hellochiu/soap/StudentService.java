package com.hellochiu.soap;

import org.ksoap2.serialization.SoapObject;

public class StudentService {

    private ServiceUtil util = new ServiceUtil();

    public String getStudentInfo(String stuId) {
        SoapObject result = util.callService("GetStudentInfo", "stuId", stuId);
        return result == null ? null : result.getPropertyAsString(0);
    }

    public String getSigningCourseId(String stuId) {
        SoapObject result = util.callService("GetSigningCourseId", "stuId", stuId);
        return result == null ? null : result.getPropertyAsString(0);
    }

    public String signIn(String stuId, String courseId) {
        SoapObject result = util.callService("SignIn", "stuId", stuId, "courseId", courseId);
        return result == null ? null : result.getPropertyAsString(0);
    }
}
