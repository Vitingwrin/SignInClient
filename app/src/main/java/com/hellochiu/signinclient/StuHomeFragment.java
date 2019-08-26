package com.hellochiu.signinclient;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hellochiu.model.Course;
import com.hellochiu.model.Student;
import com.hellochiu.model.Teacher;
import com.hellochiu.soap.StudentService;
import com.hellochiu.soap.TeacherService;
import com.hellochiu.task.LoadingTask;
import com.hellochiu.task.LoadingTaskListener;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

public class StuHomeFragment extends Fragment {

    private Activity activity;
    private Button signInButton;
    private ConstraintLayout signingLayout;
    private LinearLayout linearLayout;
    private static final int COLOR_WARNING = Color.parseColor("#ffaa00");
    private static final int COLOR_SUCCESS = Color.parseColor("#00B768");
    private ProgressBar progressBar;
    private TextView ok;
    private static volatile boolean flag = false;
    private SignHandler handler;
    private static String courseId;
    private TextView state;
    private static final String TAG = "StuHomeFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stu_home, container, false);

        signingLayout = view.findViewById(R.id.signing_layout);
        state = view.findViewById(R.id.state);
        ok = view.findViewById(R.id.ok);
        progressBar = view.findViewById(R.id.progressBar);
        linearLayout = activity.findViewById(R.id.linearLayout);

        signInButton = view.findViewById(R.id.signIn_button);
        
        signInButton.setOnClickListener(v -> {
            signInButton.setClickable(false);
            progressBar.setVisibility(View.VISIBLE);
            new LoadingTask(new LoadingTaskListener() {
                @Override
                public void onFinish(String result) {
                    if (result == null) {
                        Toast.makeText(activity, "请求超时", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        signInButton.setClickable(true);
                        return;
                    }
                    progressBar.setVisibility(View.GONE);

                    state.setVisibility(View.GONE);
                    ok.setVisibility(View.VISIBLE);
                    signInButton.setBackgroundResource(R.drawable.button_success);
                    linearLayout.setBackgroundColor(COLOR_SUCCESS);
                    StudentMainActivity.color = COLOR_SUCCESS;
                    signingLayout.setBackgroundColor(COLOR_SUCCESS);
                    signInButton.setText("已签到");
                }

                @Override
                public String onBackground() {
                    StudentService service = new StudentService();
                    return service.signIn(Student.CURRENT_USER.getStuId(), courseId);
                }
            }).execute();
        });
        signInButton.setClickable(false);

        handler = new SignHandler(activity, view);
        flag = true;
        new Thread(new SignListener()).start();
        return view;
    }

    private class SignListener implements Runnable {

        @Override
        public void run() {
            StudentService service = new StudentService();
            while (flag) {
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String result = service.getSigningCourseId(Student.CURRENT_USER.getStuId());
                if (result == null || result.equals("none")) {
                    continue;
                }
                courseId = result;
                Message message = new Message();
                message.obj = result;
                handler.sendMessage(message);
            }
        }
    }

    private static class SignHandler extends Handler {

        private WeakReference<Activity> activityWeakReference;
        private WeakReference<View> viewWeakReference;
        private Activity activity;
        //private View view;
        private ConstraintLayout beforeLayout;
        private ConstraintLayout signingLayout;
        private Button signInButton;
        private TextView courseName;
        private LinearLayout linearLayout;
        private TextView state;
        private TextView ok;

        SignHandler(Activity activity, View view) {
            activityWeakReference = new WeakReference<>(activity);
            viewWeakReference = new WeakReference<>(view);
            activity = activityWeakReference.get();
            view = viewWeakReference.get();
            beforeLayout = view.findViewById(R.id.before_layout);
            signingLayout = view.findViewById(R.id.signing_layout);
            signInButton = view.findViewById(R.id.signIn_button);
            courseName = view.findViewById(R.id.courseName);
            linearLayout = activity.findViewById(R.id.linearLayout);
            state = view.findViewById(R.id.state);
            ok = view.findViewById(R.id.ok);
        }

        @Override
        public void handleMessage(Message msg) {
            String courseId = (String) msg.obj;
            signInButton.setClickable(true);
            signInButton.setBackgroundResource(R.drawable.button_warning);
            signInButton.setText("签到");
            state.setVisibility(View.VISIBLE);
            ok.setVisibility(View.GONE);
            for (Course course : Course.SELECTED_COURSE) {
                if (course.getCourseId().equals(courseId)) {
                    courseName.setText(course.getCourseName());
                }
            }
            beforeLayout.setVisibility(View.GONE);
            signingLayout.setBackgroundColor(COLOR_WARNING);
            signingLayout.setVisibility(View.VISIBLE);
            linearLayout.setBackgroundColor(COLOR_WARNING);
            StudentMainActivity.color = COLOR_WARNING;
        }
    }
}
