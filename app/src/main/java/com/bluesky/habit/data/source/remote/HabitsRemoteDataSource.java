package com.bluesky.habit.data.source.remote;

import android.os.Handler;

import com.bluesky.habit.data.Habit;
import com.bluesky.habit.data.source.HabitsDataSource;
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author BlueSky
 * @date 2019/3/12
 * Description: 实现一个模拟网络的数据源
 */
public class HabitsRemoteDataSource implements HabitsDataSource {

    private static HabitsRemoteDataSource INSTANCE;

    private static final int SERVICE_LATENCY_IN_MILLIS = 5000;

    private final static Map<String, Habit> HABITS_SERVICE_DATA;

    static {
        HABITS_SERVICE_DATA = new LinkedHashMap<>(2);
        addHabit("Build tower in Pisa", "Ground looks good, no foundation work required.");
        addHabit("Finish bridge in Tacoma", "Found awesome girders at half the cost!");
        addHabit("Reading the EnglishBook---Desert Mountain Sea.", "Himalaya Kathmandu");

    }

    public static HabitsRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HabitsRemoteDataSource();
        }
        return INSTANCE;
    }

    private HabitsRemoteDataSource() {
    }

    private static void addHabit(String title, String description) {
        Habit newHabit = new Habit(title, description);
        HABITS_SERVICE_DATA.put(newHabit.getId(), newHabit);
    }


    /**
     * 模拟网络取数据，先延时5秒，再返回自行创建的两个habit
     *
     * @param callback
     */
    @Override
    public void getHabits(LoadHabitsCallback callback) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onHabitsLoaded(Lists.newArrayList(HABITS_SERVICE_DATA.values()));
            }
        }, SERVICE_LATENCY_IN_MILLIS);
    }

    @Override
    public void getHabit(String habitId, GetHabitCallback callback) {
        final Habit habit = HABITS_SERVICE_DATA.get(habitId);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onHabitLoaded(habit);
            }
        }, SERVICE_LATENCY_IN_MILLIS);
    }

    @Override
    public void saveHabit(Habit habit) {
        HABITS_SERVICE_DATA.put(habit.getId(), habit);
    }

    @Override
    public void completeHabit(Habit habit) {
        Habit completedHabit = new Habit(habit.getId(), habit.getTitle(), habit.getDescription(), true);
        HABITS_SERVICE_DATA.put(habit.getId(), habit);
    }

    @Override
    public void completeHabit(String habitId) {
        //源码注明不需要实现
    }

    @Override
    public void activateHabit(Habit habit) {
        Habit activeHabit = new Habit(habit.getId(), habit.getTitle(), habit.getDescription());
        HABITS_SERVICE_DATA.put(habit.getId(), habit);
    }

    @Override
    public void activateHabit(String habitId) {
        //同上，不需要实现
    }

    @Override
    public void clearCompletedHabits() {
        Iterator<Map.Entry<String, Habit>> it = HABITS_SERVICE_DATA.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Habit> entry = it.next();
            if (entry.getValue().isCompleted()) {
                it.remove();
            }
        }
    }

    @Override
    public void refreshHabits() {
        //同上
    }

    @Override
    public void deleteAllHabits() {
        HABITS_SERVICE_DATA.clear();
    }

    @Override
    public void deleteHabit(String habitId) {
        HABITS_SERVICE_DATA.remove(habitId);
    }
}
