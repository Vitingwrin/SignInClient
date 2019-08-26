package com.hellochiu.signinclient;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.hellochiu.model.Course;
import com.hellochiu.model.Teacher;
import com.hellochiu.soap.CourseService;
import com.hellochiu.task.LoadingDialogTask;
import com.hellochiu.task.LoadingDialogTaskListener;

public class CreateCourseActivity extends AppCompatActivity {

    private int hour;
    private int minute;
    private StringBuilder time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);
        time = new StringBuilder();

        EditText courseName = findViewById(R.id.courseName);
        EditText coursePwd = findViewById(R.id.coursePwd);

        EditText beginTime = findViewById(R.id.beginTime);
        beginTime.setFocusable(false);
        beginTime.setOnClickListener(v -> initTimePicker(beginTime));

        EditText endTime = findViewById(R.id.endTime);
        endTime.setFocusable(false);
        endTime.setOnClickListener(v -> initTimePicker(endTime));

        // 初始化等待对话框的Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.progress, null);
        builder.setCancelable(true)
                .setView(dialogView)
                .setTitle("正在创建");
        Button button = findViewById(R.id.createCourse);

        button.setOnClickListener(v -> {

            String name = courseName.getText().toString();
            String pwd = coursePwd.getText().toString();
            String begin = beginTime.getText().toString();
            String end = endTime.getText().toString();

            if ("".equals(name)) {
                Toast.makeText(this, "请输入课程名", Toast.LENGTH_SHORT).show();
                return;
            }
            if ("".equals(pwd)) {
                Toast.makeText(this, "请输入加入密码", Toast.LENGTH_SHORT).show();
                return;
            }
            if ("".equals(begin)) {
                Toast.makeText(this, "请输入上课时间", Toast.LENGTH_SHORT).show();
                return;
            }
            if ("".equals(end)) {
                Toast.makeText(this, "请输入下课时间", Toast.LENGTH_SHORT).show();
                return;
            }

            // 创建并执行登录任务并传入监听器
            new LoadingDialogTask(builder, new LoadingDialogTaskListener() {
                @Override
                public void onFinish(String result) {
                    if (result == null) {
                        Toast.makeText(CreateCourseActivity.this, "请求超时", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Course course = new Course();
                    course.setCourseId(result);
                    course.setCourseName(name);
                    course.setCoursePwd(pwd);
                    course.setBeginTime(begin);
                    course.setEndTime(end);
                    course.setTchId(Teacher.CURRENT_USER.getTchId());
                    course.setTchName(Teacher.CURRENT_USER.getTchName());
                    Course.CREATED_COURSE.add(course);
                    Toast.makeText(CreateCourseActivity.this, "创建成功！", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public String onBackground() {
                    CourseService service = new CourseService();
                    return service.createCourse(Teacher.CURRENT_USER.getTchId(), name, pwd, begin, end);
                }
            }).execute();
        });
    }

    private void initTimePicker(EditText editText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("选择", (dialog, which) -> {
            if (time.length() > 0) {
                time.delete(0, time.length());
            }
            editText.setText(time.append(hour < 10 ? ("0" + hour) : ("" + hour)).append(":").append(minute < 10 ? ("0" + minute) : ("" + minute)));
            dialog.dismiss();
        });
        builder.setNegativeButton("返回", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        View dialogView = View.inflate(this, R.layout.dialog_time_picker, null);
        TimePicker timePicker = dialogView.findViewById(R.id.timePicker);
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(minute);
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener((view, hourOfDay, min) -> {
            hour = hourOfDay;
            minute = min;
        });
        dialog.setView(dialogView);
        dialog.show();
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, CreateCourseActivity.class);
        context.startActivity(intent);
    }
}
