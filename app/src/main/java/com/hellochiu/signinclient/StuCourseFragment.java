package com.hellochiu.signinclient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hellochiu.model.Course;
import com.hellochiu.model.Student;
import com.hellochiu.soap.CourseService;
import com.hellochiu.soap.XmlParser;

import java.util.List;

public class StuCourseFragment extends Fragment {

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private StuCourseAdapter adapter;
    private TextView none;
    private static boolean created = false;
    private static final String TAG = "StuCourseFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: onCreate");
        View view = inflater.inflate(R.layout.fragment_course, container, false);
        progressBar = view.findViewById(R.id.loadingBar);
        recyclerView = view.findViewById(R.id.courseList);
        none = view.findViewById(R.id.none);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        Context context = getActivity();
        if (context != null) {
            recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        }
        recyclerView.setLayoutManager(manager);
        adapter = new StuCourseAdapter(Course.SELECTED_COURSE);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(() -> new LoadCourseTask().execute());

        if (!created) {
            new LoadCourseTask().execute();
            created = true;
        } else {
            progressBar.setVisibility(View.GONE);
            none.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @SuppressLint("StaticFieldLeak")
    class LoadCourseTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPostExecute(String xml) {

            if (xml == null) {
                none.setText("加载超时");
                swipeRefreshLayout.setRefreshing(false);
                return;
            }

            XmlParser.parseStuSelectedCourse(xml);
            progressBar.setVisibility(View.GONE);
            if (!Course.isCourseInfoLoaded()) {
                none.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                return;
            }
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
            none.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            CourseService service = new CourseService();
            return service.getSelectedCourses(Student.CURRENT_USER.getStuId());
        }
    }

    class StuCourseAdapter extends RecyclerView.Adapter<StuCourseAdapter.ViewHolder> {

        private List<Course> courseList;

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stu_course_item, parent, false);
            ViewHolder holder = new ViewHolder(view);
            holder.view.setOnClickListener(v -> {
                int position = holder.getAdapterPosition();
                Log.d(TAG, "onCreateViewHolder: " + courseList);
                Course course = courseList.get(position);
                CourseDetailActivity.actionStart(StuCourseFragment.this.getActivity(), course.getCourseId());
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

        StuCourseAdapter(List<Course> courseList) {
            this.courseList = courseList;
        }

    }

}
