package com.bluesky.habit.data;

import com.google.common.base.Strings;

import java.util.Objects;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * @author BlueSky
 * @date 2019/3/4
 * Description: 一个任务类的描述
 */
@Entity
public final class Habit {

    //todo 主键必须有NonNull注解

    @PrimaryKey
    @NonNull
    @ColumnInfo(name="id")
    private final String mId;
    @ColumnInfo(name = "title")
    private final String mTitle;
    @ColumnInfo(name = "description")
    private final String mDescription;
    @ColumnInfo(name = "completed")
    private final boolean mCompleted;

    @Ignore
    public Habit(String title, String description) {
        this(UUID.randomUUID().toString(), title, description, false);
    }
    @Ignore
    public Habit(String id, String title, String description) {
        this(id, title, description, false);
    }
    @Ignore
    public Habit(String title, String description, boolean completed) {
        this(UUID.randomUUID().toString(), title, description, completed);
    }

    public Habit(String id, String title, String description, boolean completed) {
        mId = id;
        mTitle = title;
        mDescription = description;
        mCompleted = completed;
    }

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public boolean isCompleted() {
        return mCompleted;
    }

    /**
     * 如何title为空,那么返回desc
     *
     * @return
     */
    public String getTitleForList() {
        if (Strings.isNullOrEmpty(mTitle)) {
            return mTitle;
        } else {
            return mDescription;
        }
    }

    /**
     * 没有完成,就是活动状态
     *
     * @return
     */
    public boolean isActive() {
        return !mCompleted;
    }

    public boolean isEmpty() {
        return Strings.isNullOrEmpty(mTitle) && Strings.isNullOrEmpty(mDescription);
    }

    @Override
    public String toString() {
        return "Habit with title " + mTitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Habit habit = (Habit) o;
        return Objects.equals(mId, habit.mId) &&
                Objects.equals(mTitle, habit.mTitle) &&
                Objects.equals(mDescription, habit.mDescription);
    }

    /**
     * 源码中,使用的objects类是guava里的com.google.com.base
     *
     * @return
     */
    @Override
    public int hashCode() {
        return Objects.hash(mId, mTitle, mDescription);
    }
}
