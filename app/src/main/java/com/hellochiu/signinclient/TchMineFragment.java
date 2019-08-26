package com.hellochiu.signinclient;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hellochiu.model.Student;
import com.hellochiu.model.Teacher;

import static android.content.Context.MODE_PRIVATE;

public class TchMineFragment extends Fragment {

    private Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        Button logout = view.findViewById(R.id.logout);

        TextView userName = view.findViewById(R.id.userName);
        TextView userId = view.findViewById(R.id.userId);

        userName.setText(Teacher.CURRENT_USER.getTchName());
        String text = "工资号：" + Teacher.CURRENT_USER.getTchId();
        userId.setText(text);

        logout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = activity.getSharedPreferences("userInfo", MODE_PRIVATE).edit();
            editor.clear();
            editor.apply();
            LoginActivity.actionStart(activity, Teacher.CURRENT_USER.getTchId());
            TeacherMainActivity.color = Color.parseColor("#00ACF2");
            activity.finish();
        });
        return view;
    }

}
