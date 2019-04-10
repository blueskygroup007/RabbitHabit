package com.bluesky.habit.data.source.local;

import android.content.Context;

import com.bluesky.habit.data.Habit;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * @author BlueSky
 * @date 2019/3/20
 * Description:
 */
@Database(entities = {Habit.class}, version = 1,exportSchema = false)
public abstract class ToDoDatabase extends RoomDatabase {
    //todo exportSchema = false 否则报错
    private static ToDoDatabase INSTANCE;

    public abstract HabitsDao habitDao();

    private static final Object sLock = new Object();

    public static ToDoDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(), ToDoDatabase.class, "Habits.db").build();
            }
            return INSTANCE;
        }
    }
}
