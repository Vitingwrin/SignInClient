package com.hellochiu.soap;

import org.ksoap2.serialization.SoapObject;

public class UserService {

    private ServiceUtil util = new ServiceUtil();
    public static final String ERROR = "error";
    public static final String IS_TEACHER = "teacher";
    public static final String IS_STUDENT = "student";
    public static final String TEACHER = "1";
    public static final String STUDENT = "0";
    public static final String SUCCESS = "success";

    public String login(String userId, String userPwd) {
        SoapObject result = util.callService("Login", "userId", userId, "userPwd", userPwd);
        return result == null ? null : result.getPropertyAsString(0);
    }

    public String register(String userId, String userPwd, String userName, String identity) {
        SoapObject result = util.callService("Register", "userId", userId, "userPwd", userPwd, "userName", userName, "identity", identity);
        return result == null ? null : result.getPropertyAsString(0);
    }

    public String signIn() {
        SoapObject result = util.callService("SignIn");
        return result == null ? null : result.getPropertyAsString(0);
    }

    public String startSignIn(String tchId, String classId) {
        SoapObject result = util.callService("StartSignIn", "tchId", tchId, "classId", classId);
        return result == null ? null : result.getPropertyAsString(0);
    }

    public String stopSignIn(String tchId, String classId) {
        SoapObject result = util.callService("StopSignIn", "tchId", tchId, "classId", classId);
        return result == null ? null : result.getPropertyAsString(0);
    }
}
