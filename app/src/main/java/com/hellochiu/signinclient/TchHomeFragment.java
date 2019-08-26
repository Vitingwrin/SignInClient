package com.hellochiu.signinclient;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hellochiu.model.Course;
import com.hellochiu.model.Teacher;
import com.hellochiu.soap.TeacherService;
import com.hellochiu.task.LoadingTask;
import com.hellochiu.task.LoadingTaskListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TchHomeFragment extends Fragment {

    private Button select;
    private Button start;
    private Button stop;
    private Activity activity;
    private BottomSheetDialog bottomSheetDialog;
    private CourseSheetAdapter adapter;
    private View dialogView;
    private TextView state;
    private LinearLayout linearLayout;
    private TextView timer;
    private static final int COLOR_WARNING = Color.parseColor("#ffaa00");
    private static final int COLOR_NORMAL = Color.parseColor("#00ACF2");
    private static boolean signFlag = false;
    private Handler handler = new Handler();
    private long time = 0;
    private long pass;
    private Runnable timerTask;
    private ConstraintLayout beforeLayout;
    private ConstraintLayout signingLayout;
    private ProgressBar progressBar;

    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tch_home, container, false);
        if (bottomSheetDialog == null) {
            bottomSheetDialog = new BottomSheetDialog(activity);
            dialogView = inflater.inflate(R.layout.course_sheet, container, false);
            bottomSheetDialog.setContentView(dialogView);
        }

        initView();

        return view;
    }

    private void initView() {

        linearLayout = activity.findViewById(R.id.linearLayout);
        state = view.findViewById(R.id.state);
        timer = view.findViewById(R.id.timer);

        RecyclerView recyclerView = dialogView.findViewById(R.id.tch_course_sheet);
        LinearLayoutManager manager = new LinearLayoutManager(activity);
        recyclerView.addItemDecoration(new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(manager);

        beforeLayout = view.findViewById(R.id.before_layout);
        signingLayout = view.findViewById(R.id.signing_layout);

        adapter = new CourseSheetAdapter(Course.CREATED_COURSE, () -> {
            beforeLayout.setVisibility(View.GONE);
            TextView courseName = signingLayout.findViewById(R.id.courseName);
            courseName.setText(Teacher.course.getCourseName());
            state.setText("点击开始签到");
            bottomSheetDialog.hide();
            signingLayout.setVisibility(View.VISIBLE);
            select.setVisibility(View.GONE);
            start.setVisibility(View.VISIBLE);
        });
        recyclerView.setAdapter(adapter);

        select = view.findViewById(R.id.select_button);
        select.setOnClickListener(v -> {
            adapter.notifyDataSetChanged();
            bottomSheetDialog.show();
        });


        SimpleDateFormat sd = new SimpleDateFormat("mm:ss:SS", Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        timerTask = new Runnable() {
            @Override
            public void run() {
                if (signFlag) {
                    pass = System.currentTimeMillis() - time;
                    calendar.setTimeInMillis(pass);
                    timer.setText(sd.format(calendar.getTime()));
                    handler.postDelayed(this, 10);
                }
            }
        };

        start = view.findViewById(R.id.start_button);
        start.setOnClickListener(v -> {
            start.setClickable(false);
            progressBar.setVisibility(View.VISIBLE);
            new LoadingTask(new LoadingTaskListener() {
                @Override
                public void onFinish(String result) {
                    start.setClickable(true);
                    if (result == null) {
                        Toast.makeText(activity, "请求超时", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        state.setVisibility(View.VISIBLE);
                        return;
                    }

                    // 不可能的情况，以防万一
                    if ("请先结束未完成的签到".equals(result)) {
                        Toast.makeText(activity, "系统错误，请联系作者", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        state.setVisibility(View.VISIBLE);
                        return;
                    }
                    startSignIn();
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public String onBackground() {
                    TeacherService service = new TeacherService();
                    return service.startSignIn(Teacher.course.getCourseId());
                }
            }).execute();
        });

        stop = view.findViewById(R.id.stop_button);
        stop.setOnClickListener(v -> {
            stop.setClickable(false);
            progressBar.setVisibility(View.VISIBLE);
            new LoadingTask(new LoadingTaskListener() {
                @Override
                public void onFinish(String result) {
                    stop.setClickable(true);
                    if (result == null) {
                        Toast.makeText(activity, "请求超时", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        state.setVisibility(View.VISIBLE);
                        return;
                    }
                    stopSignIn();
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public String onBackground() {
                    TeacherService service = new TeacherService();
                    return service.stopSignIn(Teacher.course.getCourseId());
                }
            }).execute();
        });

        progressBar = view.findViewById(R.id.progressBar);
    }

    private void startSignIn() {
        time = System.currentTimeMillis();
        signFlag = true;
        handler.postDelayed(timerTask, 10);
        start.setVisibility(View.GONE);
        stop.setVisibility(View.VISIBLE);
        signingLayout.setBackgroundColor(COLOR_WARNING);
        TeacherMainActivity.color = COLOR_WARNING;
        linearLayout.setBackgroundColor(COLOR_WARNING);
        state.setText("正在签到");
        new Thread(new GradientEffect()).start();
        Toast.makeText(activity, "已发起签到", Toast.LENGTH_SHORT).show();
    }

    private void stopSignIn() {
        signFlag = false;
        timer.setText("");
        stop.setVisibility(View.GONE);
        select.setVisibility(View.VISIBLE);
        signingLayout.setBackgroundColor(COLOR_NORMAL);
        TeacherMainActivity.color = COLOR_NORMAL;
        linearLayout.setBackgroundColor(COLOR_NORMAL);
        signingLayout.setVisibility(View.GONE);
        beforeLayout.setVisibility(View.VISIBLE);
        Toast.makeText(activity, "已停止签到", Toast.LENGTH_SHORT).show();
    }

    class GradientEffect implements Runnable {

        private int i = 0;

        @Override
        public void run() {
            while (signFlag) {
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                activity.runOnUiThread(() -> {
                    String text = state.getText().toString() + ".";
                    if (++i > 3) {
                        text = text.substring(0, text.length() - 4);
                        i = 0;
                    }
                    state.setText(text);
                });
            }
        }
    }
    /*private class GradientTextView extends AppCompatTextView {

        private int width;
        private TextPaint paint;
        private LinearGradient linearGradient;
        private Matrix matrix;
        private int translate = 0;

        public GradientTextView(Context context) {
            super(context);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            if (width == 0) {
                width = getMeasuredWidth();
                if (width > 0) {
                    paint = getPaint();
                    linearGradient = new LinearGradient(0, 0, width, 0, new int[]{
                            Color.BLUE, 0xffffffff,
                            Color.RED
                    }, null, Shader.TileMode.CLAMP);
                    paint.setShader(linearGradient);
                    matrix = new Matrix();
                }
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (matrix != null) {
                translate += width / 5;
                if (translate > 2 * width) {
                    translate = -width;
                }

                matrix.setTranslate(translate, 0);
                linearGradient.setLocalMatrix(matrix);
                postInvalidateDelayed(100);
            }
        }
    }*/
}