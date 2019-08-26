package com.hellochiu.signinclient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hellochiu.model.Teacher;
import com.hellochiu.soap.StudentService;
import com.hellochiu.soap.XmlParser;
import com.hellochiu.model.Student;

import java.lang.ref.WeakReference;

public class StudentMainActivity extends BaseActivity {

    // private InfoHandler handler;

    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    private ViewPager viewPager;
    private MenuItem menuItem;
    private LinearLayout linearLayout;
    public static int color = Color.parseColor("#00ACF2");
    private static final String TAG = "StudentMainActivity";

    /*private static class InfoHandler extends Handler {

        WeakReference<Activity> activityWeakReference;
        WeakReference<View> viewWeakReference;

        InfoHandler(Activity activity, View headerView) {
            activityWeakReference = new WeakReference<>(activity);
            viewWeakReference = new WeakReference<>(headerView);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.obj == null) {
                return;
            }
            Activity activity = activityWeakReference.get();
            View headerView = viewWeakReference.get();
            if (activity == null || headerView == null) {
                return;
            }
            XmlParser.parseStuInfo((String) msg.obj);
            TextView stuNameText = headerView.findViewById(R.id.stuName);
            stuNameText.setText(Student.CURRENT_USER.getStuName());
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        initView();
        bottomNavigationView.getMenu().getItem(1).setChecked(true);
        viewPager.setCurrentItem(1);

        /*// 获取用户账号
        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");
        Student.CURRENT_USER.setStuId(userId);
        stuIdText.setText(Student.CURRENT_USER.getStuId());

        // 开启线程加载用户数据
        handler = new InfoHandler(this, headerView);
        new Thread(() -> {
            StudentService service = new StudentService();
            Message message = new Message();
            message.obj = service.getStudentInfo(userId);
            handler.sendMessage(message);
        }).start();*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        menuItem = menu.findItem(R.id.addCourse);
        menuItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.addCourse:
                AddCourseActivity.actionStart(this);
                break;
        }
        return true;
    }

    public static void actionStart(Context context, String userId) {
        Intent intent = new Intent(context, StudentMainActivity.class);
        intent.putExtra("userId", userId);
        context.startActivity(intent);
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        linearLayout = findViewById(R.id.linearLayout);
        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.studentNav);
        View headerView = navigationView.getHeaderView(0);
        TextView stuIdText = headerView.findViewById(R.id.stuId);
        stuIdText.setText(Student.CURRENT_USER.getStuId());
        TextView stuNameText = headerView.findViewById(R.id.stuName);
        stuNameText.setText(Student.CURRENT_USER.getStuName());

        // 加载底部导航栏
        bottomNavigationView = findViewById(R.id.stuBottomNavigation);
        viewPager = findViewById(R.id.stuViewPager);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.classItem:
                    viewPager.setCurrentItem(0);
                    // initStuCourse();
                    return true;
                case R.id.homeItem:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.mineItem:
                    viewPager.setCurrentItem(2);
                    return true;
                default:
                    return false;
            }
        });

        setupViewPager(viewPager);

        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_add_course:
                    AddCourseActivity.actionStart(this);
                    break;
                case R.id.nav_setting:
                    viewPager.setCurrentItem(2);
                    break;
                case R.id.nav_exit:
                    SharedPreferences.Editor editor = getSharedPreferences("userInfo", MODE_PRIVATE).edit();
                    editor.clear();
                    editor.apply();
                    LoginActivity.actionStart(this, Student.CURRENT_USER.getStuId());
                    StudentMainActivity.color = Color.parseColor("#00ACF2");
                    finish();
                    break;
            }
            drawerLayout.closeDrawers();
            return true;
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            TextView title = findViewById(R.id.stuTitle);

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                switch (position) {
                    case 0:
                        title.setText("课程");
                        toolbar.setVisibility(View.VISIBLE);
                        linearLayout.setBackgroundColor(Color.parseColor("#3F51B5"));
                        if (menuItem != null) {
                            menuItem.setVisible(true);
                        }
                        break;
                    case 1:
                        title.setText("签到");
                        linearLayout.setBackgroundColor(color);
                        if (menuItem != null) {
                            menuItem.setVisible(false);
                        }
                        toolbar.setVisibility(View.GONE);
                        break;
                    case 2:
                        title.setText("我的");
                        toolbar.setVisibility(View.VISIBLE);
                        linearLayout.setBackgroundColor(Color.parseColor("#3F51B5"));
                        if (menuItem != null) {
                            menuItem.setVisible(false);
                        }
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // 加载菜单按钮
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        BottomAdapter adapter = new BottomAdapter(getSupportFragmentManager());
        adapter.addFragment(new StuCourseFragment());
        adapter.addFragment(new StuHomeFragment());
        adapter.addFragment(new StuMineFragment());
        viewPager.setAdapter(adapter);
    }
}
