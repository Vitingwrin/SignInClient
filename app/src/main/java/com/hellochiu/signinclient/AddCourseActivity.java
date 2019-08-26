package com.hellochiu.signinclient;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hellochiu.model.Course;
import com.hellochiu.model.Student;
import com.hellochiu.soap.CourseService;
import com.hellochiu.soap.XmlParser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AddCourseActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CoursesAdapter adapter;
    private List<Course> result = new ArrayList<>(Course.ALL_COURSE);
    private AlertDialog dialog;
    private String searchText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_course);
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // progressBar = findViewById(R.id.loadingBar);
        recyclerView = findViewById(R.id.courseResultRecycler);
        // none = findViewById(R.id.none);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(manager);
        adapter = new CoursesAdapter(result);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        Listener listener = xml -> {
            if (xml == null) {
                Toast.makeText(this, "加载超时", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
                return;
            }
            XmlParser.parseAllCourse(xml);
            refreshResult();
            // progressBar.setVisibility(View.GONE);
            if (result.isEmpty()) {
                // none.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                return;
            }
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
            // none.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        };
        swipeRefreshLayout.setOnRefreshListener(() -> new LoadCoursesTask(listener).execute());
    }

    private void refreshResult() {
        result.clear();
        searchText = searchText.toLowerCase().trim();
        for (Course course : Course.ALL_COURSE) {
            if (course.getCourseName().toLowerCase().contains(searchText)) {
                result.add(course);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private static class LoadCoursesTask extends AsyncTask<Void, Void, String> {

        private Listener listener;

        LoadCoursesTask(Listener listener) {
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Void... voids) {
            CourseService service = new CourseService();
            return service.getAllCourses();
        }

        @Override
        protected void onPostExecute(String xml) {
            listener.onFinish(xml);
        }
    }

    private interface Listener {
        void onFinish(String xml);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        // searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("输入内容查找课程");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchText = newText;
                refreshResult();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, AddCourseActivity.class);
        context.startActivity(intent);
    }

    class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.ViewHolder> {

        private List<Course> courseList;

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stu_course_item, parent, false);
            ViewHolder holder = new ViewHolder(view);
            holder.view.setOnClickListener(v -> {
                int position = holder.getAdapterPosition();
                Course course = courseList.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(AddCourseActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_add_course, null);
                Thread addCourse = new Thread(() -> {
                   CourseService service = new CourseService();
                   String result = service.addCourse(Student.CURRENT_USER.getStuId(), course.getCourseId());
                   runOnUiThread(() -> {
                       if (result == null) {
                           Toast.makeText(AddCourseActivity.this, "请求超时", Toast.LENGTH_SHORT).show();
                           return;
                       }
                       Toast.makeText(AddCourseActivity.this, result, Toast.LENGTH_SHORT).show();
                       dialog.cancel();
                       if (result.equals(CourseService.ADD_COURSE_SUCCESS)) {
                           finish();
                       }
                   });
                });
                builder.setCancelable(true)
                        .setView(dialogView)
                        .setTitle(course.getCourseName())
                        .setPositiveButton("加入", (dialog, which) ->{
                            builder.setCancelable(false);
                            EditText coursePwd = dialogView.findViewById(R.id.coursePwd);
                            if (coursePwd.getText().toString().equals(course.getCoursePwd())) {
                                addCourse.start();
                            } else {
                                Toast.makeText(AddCourseActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                            }

                        })
                        .setOnCancelListener(DialogInterface::cancel);
                dialog = builder.show();

            });
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Course course = courseList.get(position);
            holder.courseName.setText(course.getCourseName());
            holder.tchName.setText(course.getTchName());
            String time = course.getBeginTime() + " - " + course.getEndTime();
            holder.courseTime.setText(time);
        }

        @Override
        public int getItemCount() {
            return courseList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            View view;
            TextView courseName;
            TextView courseTime;
            TextView tchName;

            ViewHolder(View view) {
                super(view);
                this.view = view;
                courseName = view.findViewById(R.id.courseName);
                courseTime = view.findViewById(R.id.courseTime);
                tchName = view.findViewById(R.id.tchName);
            }
        }

        CoursesAdapter(List<Course> courseList) {
            this.courseList = courseList;
        }

    }

}
