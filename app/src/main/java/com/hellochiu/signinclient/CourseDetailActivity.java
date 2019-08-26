package com.hellochiu.signinclient;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hellochiu.model.Course;
import com.hellochiu.model.CourseStuDetail;
import com.hellochiu.soap.CourseService;
import com.hellochiu.soap.XmlParser;
import com.hellochiu.task.LoadingDialogTask;
import com.hellochiu.task.LoadingDialogTaskListener;

import java.util.ArrayList;
import java.util.List;

public class CourseDetailActivity extends BaseActivity {

    private List<CourseStuDetail> details = new ArrayList<>();
    private CourseStuDetailAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Course course = new Course();
    private CourseService service = new CourseService();
    private static final String TAG = "CourseDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        Intent intent = getIntent();
        String courseId = intent.getStringExtra("courseId");
        List<Course> courses = Course.SELECTED_COURSE.isEmpty() ? Course.CREATED_COURSE : Course.SELECTED_COURSE;
        for (Course c : courses) {
            if (c.getCourseId().equals(courseId)) {
                course = c;
            }
        }
        Toolbar toolbar = findViewById(R.id.toolBar);
        RecyclerView recyclerView = findViewById(R.id.courseDetailRecycler);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(manager);
        adapter = new CourseStuDetailAdapter(details);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        swipeRefreshLayout.setOnRefreshListener(() -> new Thread(() -> {
            String result = service.getCourseStuDetails(course.getCourseId());
            runOnUiThread(() -> {
                XmlParser.parseCourseStuDetail(result, details);
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            });
        }).start());

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbarLayout.setTitle(course.getCourseName());
        initRecycler();
    }

    private void initRecycler() {

        // 初始化对话框的Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.progress, null);
        builder.setCancelable(true)
                .setView(dialogView)
                .setTitle("正在加载");

        // 创建并执行加载任务
        new LoadingDialogTask(builder, new LoadingDialogTaskListener() {
            @Override
            public void onFinish(String result) {
                if (result == null) {
                    Toast.makeText(CourseDetailActivity.this, "请求超时", Toast.LENGTH_SHORT).show();
                    return;
                }
                XmlParser.parseCourseStuDetail(result, details);
                Log.d(TAG, "onFinish: " + details);
                adapter.notifyDataSetChanged();
            }

            @Override
            public String onBackground() {
                return service.getCourseStuDetails(course.getCourseId());
            }
        }).execute();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void actionStart(Context context, String courseId) {
        Intent intent = new Intent(context, CourseDetailActivity.class);
        intent.putExtra("courseId", courseId);
        context.startActivity(intent);
    }

    static class CourseStuDetailAdapter extends RecyclerView.Adapter<CourseStuDetailAdapter.ViewHolder> {

        private List<CourseStuDetail> details;

        @NonNull
        @Override
        public CourseStuDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_stu_detail_item, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull CourseStuDetailAdapter.ViewHolder holder, int position) {
            CourseStuDetail detail = details.get(position);
            holder.stuName.setText(detail.getStuName());
            holder.stuId.setText(detail.getStuId());
            String rate = detail.getRate() + "%";
            holder.rate.setText(rate);
        }

        @Override
        public int getItemCount() {
            return details.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            // View view;
            TextView stuName;
            TextView stuId;
            TextView rate;

            ViewHolder(View view) {
                super(view);
                // this.view = view;
                stuId = view.findViewById(R.id.stuId);
                stuName = view.findViewById(R.id.stuName);
                rate = view.findViewById(R.id.rate);
            }
        }

        CourseStuDetailAdapter(List<CourseStuDetail> details) {
            this.details = details;
        }

    }

}
