package com.hellochiu.task;

public interface LoadingTaskListener {

    void onFinish(String result);
    String onBackground();

}
