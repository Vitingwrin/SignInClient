package com.hellochiu.signinclient;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.hellochiu.soap.UserService;
import com.hellochiu.task.LoadingDialogTask;
import com.hellochiu.task.LoadingDialogTaskListener;

public class RegisterActivity extends BaseActivity {

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button register = findViewById(R.id.register_button);
        EditText userIdView = findViewById(R.id.userId);
        EditText userPwdView = findViewById(R.id.userPwd);
        EditText userNameView = findViewById(R.id.userName);
        RadioGroup radioGroup = findViewById(R.id.radioGroup);

        register.setOnClickListener(v -> {
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
            String userName = userNameView.getText().toString();

            // 姓名为空
            if ("".equals(userName)) {
                Toast.makeText(this, "请输入姓名", Toast.LENGTH_SHORT).show();
                return;
            }

            int radioId = radioGroup.getCheckedRadioButtonId();
            String identity;
            switch (radioId) {
                case R.id.student:
                    identity = UserService.STUDENT;
                    break;
                case R.id.teacher:
                    identity = UserService.TEACHER;
                    break;
                default:
                    identity = "0";
            }

            // 初始化对话框的Builder
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.progress, null);
            builder.setCancelable(true)
                .setView(dialogView)
                .setTitle("正在注册");

            // 启动注册任务并传入监听器
            new LoadingDialogTask(builder, new LoadingDialogTaskListener() {
                @Override
                public void onFinish(String result) {
                    if (result == null) {
                        Toast.makeText(RegisterActivity.this, "请求超时", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (result.equals(UserService.ERROR)) {
                        Toast.makeText(RegisterActivity.this, "该账号已被注册", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (result.equals(UserService.SUCCESS)) {
                        Toast.makeText(RegisterActivity.this, "注册成功！请登录", Toast.LENGTH_SHORT).show();
                        ActivityManager.removeActivity(RegisterActivity.this);
                        ActivityManager.finishAll();
                        LoginActivity.actionStart(RegisterActivity.this, userId);
                        RegisterActivity.this.finish();
                    }
                }

                @Override
                public String onBackground() {
                    UserService service = new UserService();
                    return service.register(userId, userPwd, userName, identity);
                }
            }).execute();
        });
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }

}
