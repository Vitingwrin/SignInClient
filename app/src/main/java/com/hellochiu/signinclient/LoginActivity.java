package com.hellochiu.signinclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hellochiu.model.Student;
import com.hellochiu.model.Teacher;
import com.hellochiu.soap.StudentService;
import com.hellochiu.soap.TeacherService;
import com.hellochiu.soap.UserService;
import com.hellochiu.soap.XmlParser;
import com.hellochiu.task.LoadingDialogTask;
import com.hellochiu.task.LoadingDialogTaskListener;

public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button login = findViewById(R.id.login_button);
        EditText userIdView = findViewById(R.id.userId);
        EditText userPwdView = findViewById(R.id.userPwd);
        TextView register = findViewById(R.id.register_textView);

        // 自动填充注册的账号
        Intent intent = getIntent();
        String userIdBuffer = intent.getStringExtra("userId");
        if (userIdBuffer != null && !userIdBuffer.equals("")) {
            userIdView.setText(userIdBuffer);
            userPwdView.requestFocus();
        }

        register.setOnClickListener(v -> RegisterActivity.actionStart(this));

        login.setOnClickListener(v -> {
            String userId = userIdView.getText().toString();

            // 账号为空
            if ("".equals(userId)) {
                Toast.makeText(this, "请输入账号", Toast.LENGTH_SHORT).show();
                return;
            }
            String userPwd = userPwdView.getText().toString();

            // 密码为空
            if ("".equals(userPwd)) {
                Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
                return;
            }

            // 初始化对话框的Builder
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.progress, null);
            builder.setCancelable(true)
                    .setView(dialogView)
                    .setTitle("正在登录");

            // 创建并执行登录任务并传入监听器
            new LoadingDialogTask(builder, new LoadingDialogTaskListener() {
                @Override
                public void onFinish(String result) {
                    if (result == null) {
                        Toast.makeText(LoginActivity.this, "请求超时", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (result.equals(UserService.ERROR)) {
                        Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (result.equals(UserService.IS_STUDENT)) {
                        StudentMainActivity.actionStart(LoginActivity.this, userId);
                        LoginActivity.this.finish();
                    } else if (result.equals(UserService.IS_TEACHER)) {
                        TeacherMainActivity.actionStart(LoginActivity.this, userId);
                        LoginActivity.this.finish();
                    }
                }

                @Override
                public String onBackground() {
                    UserService service = new UserService();
                    String result = service.login(userId, userPwd);
                    if (result == null) {
                        return null;
                    }
                    if (result.equals(UserService.ERROR)) {
                        return result;
                    }
                    if (result.equals(UserService.IS_STUDENT)) {
                        Student.CURRENT_USER.setStuId(userId);
                        StudentService studentService = new StudentService();
                        String xml = studentService.getStudentInfo(userId);
                        XmlParser.parseStuInfo(xml);
                        SharedPreferences.Editor editor = getSharedPreferences("userInfo", MODE_PRIVATE).edit();
                        editor.putString("userId", Student.CURRENT_USER.getStuId());
                        editor.putString("userName", Student.CURRENT_USER.getStuName());
                        editor.putString("userPhone", Student.CURRENT_USER.getStuPhone());
                        editor.putString("userEmail", Student.CURRENT_USER.getStuEmail());
                        editor.putString("identity", result);
                        editor.apply();
                    } else if (result.equals(UserService.IS_TEACHER)) {
                        Teacher.CURRENT_USER.setTchId(userId);
                        TeacherService teacherService = new TeacherService();
                        String xml = teacherService.getTeacherInfo(userId);
                        XmlParser.parseTchInfo(xml);
                        SharedPreferences.Editor editor = getSharedPreferences("userInfo", MODE_PRIVATE).edit();
                        editor.putString("userId", Teacher.CURRENT_USER.getTchId());
                        editor.putString("userName", Teacher.CURRENT_USER.getTchName());
                        editor.putString("userPhone", Teacher.CURRENT_USER.getTchPhone());
                        editor.putString("userEmail", Teacher.CURRENT_USER.getTchEmail());
                        editor.putString("identity", result);
                        editor.apply();
                    }
                    return result;
                }
            }).execute();

        });
    }

    public static void actionStart(Context context, String userId) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("userId", userId);
        context.startActivity(intent);
    }

}

