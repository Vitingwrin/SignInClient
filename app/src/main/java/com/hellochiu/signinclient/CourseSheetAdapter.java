package com.hellochiu.signinclient;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hellochiu.model.Course;
import com.hellochiu.model.Teacher;

import java.util.List;

public class CourseSheetAdapter extends RecyclerView.Adapter<CourseSheetAdapter.ViewHolder> {

    private List<Course> courseList;
    private Listener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_sheet_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.view.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            Course course = courseList.get(position);
            if (!"".equals(Teacher.CURRENT_USER.getTchId())) {
                Teacher.course.setCourseName(course.getCourseName());
                Teacher.course.setCourseId(course.getCourseId());
            }
            listener.onClick();
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.courseName.setText(course.getCourseName());
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView courseName;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            courseName = view.findViewById(R.id.courseName);
        }
    }

    CourseSheetAdapter(List<Course> courseList, Listener listener) {
        this.courseList = courseList;
        this.listener = listener;
    }

    public interface Listener {
        void onClick();
    }

}