package com.hellochiu.signinclient;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.hellochiu.model.Student;
import com.hellochiu.model.Teacher;
import com.hellochiu.soap.CourseService;
import com.hellochiu.soap.UserService;
import com.hellochiu.soap.XmlParser;

import java.util.concurrent.TimeUnit;

public class StartActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start);
        SharedPreferences preferences = getSharedPreferences("userInfo", MODE_PRIVATE);
        String userId = preferences.getString("userId", "");
        String identity = preferences.getString("identity", "");
        new LoadResourceTask(xml -> {
            if (xml == null) {
                Toast.makeText(this, "网络超时", Toast.LENGTH_SHORT).show();
            } else {
                XmlParser.parseAllCourse(xml);
            }

            if ("".equals(userId) || "".equals(identity)) {
                LoginActivity.actionStart(this, userId);
            } else {
                if (UserService.IS_STUDENT.equals(identity)) {
                    Student.CURRENT_USER.setStuId(userId);
                    Student.CURRENT_USER.setStuName(preferences.getString("userName", ""));
                    Student.CURRENT_USER.setStuEmail(preferences.getString("userEmail", ""));
                    Student.CURRENT_USER.setStuPhone(preferences.getString("userPhone", ""));
                    StudentMainActivity.actionStart(this, userId);
                } else if (UserService.IS_TEACHER.equals(identity)) {
                    Teacher.CURRENT_USER.setTchId(userId);
                    Teacher.CURRENT_USER.setTchName(preferences.getString("userName", ""));
                    Teacher.CURRENT_USER.setTchEmail(preferences.getString("userEmail", ""));
                    Teacher.CURRENT_USER.setTchPhone(preferences.getString("userPhone", ""));
                    TeacherMainActivity.actionStart(this, userId);
                }
            }
            finish();
        }).execute();
    }

    private static class LoadResourceTask extends AsyncTask<Void, Void, String> {

        private Listener listener;

        LoadResourceTask(Listener listener) {
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Void... voids) {
            CourseService service = new CourseService();

            long time = System.currentTimeMillis();
            String result = service.getAllCourses();
            time = System.currentTimeMillis() - time;

            // 本地服务器加载太快，让启动页停留至少1秒
            try {
                TimeUnit.MILLISECONDS.sleep(1000 - time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String xml) {
            listener.onFinish(xml);
        }
    }

    private interface Listener {
        void onFinish(String xml);
    }

}