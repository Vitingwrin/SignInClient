package com.hellochiu.soap;

import org.ksoap2.serialization.SoapObject;

public class TeacherService {

    private ServiceUtil util = new ServiceUtil();

    public String getTeacherInfo(String tchId) {
        SoapObject result = util.callService("GetTeacherInfo", "tchId", tchId);
        return result == null ? null : result.getPropertyAsString(0);
    }

    public String startSignIn(String courseId) {
        SoapObject result = util.callService("StartSignIn", "courseId", courseId);
        return result == null ? null : result.getPropertyAsString(0);
    }

    public String stopSignIn(String courseId) {
        SoapObject result = util.callService("StopSignIn", "courseId", courseId);
        return result == null ? null : result.getPropertyAsString(0);
    }
}
