package com.bluesky.habit.habitdetail;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.bluesky.habit.R;

/**
 * @author BlueSky
 * @date 2019/3/18
 * Description: Task详情页面
 */
public class HabitDetailActivity extends AppCompatActivity {

    public static final String EXTRA_HABIT_ID = "HABIT_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_habit_detail);
    }
}
