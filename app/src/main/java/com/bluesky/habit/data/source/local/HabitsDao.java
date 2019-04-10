package com.bluesky.habit.data.source.local;

import com.bluesky.habit.data.Habit;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

/**
 * @author BlueSky
 * @date 2019/3/20
 * Description:
 */
@Dao
public interface HabitsDao {

    /**
     * 查询所有habits
     *
     * @return
     */
    @Query("SELECT * FROM Habit")
    List<Habit> getHabits();

    @Query("SELECT * FROM Habit WHERE id=:habitId")
    Habit getHabitById(String habitId);

    /**
     * 插入一个habit,如果已经存在,则覆盖(这也是REPLACE的作用)
     *
     * @param habit
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertHabit(Habit habit);

    /**
     * 更新一个habit
     *
     * @param habit
     * @return 被更新的habit数量, 总为1.
     */
    @Update
    int updateHabit(Habit... habit);

    /**
     * 更新一个habit的completed状态
     *
     * @param habitId
     * @param completed
     */
    @Query("UPDATE Habit SET completed=:completed WHERE id=:habitId")
    void updateCompleted(String habitId, boolean completed);

    /**
     * 删除所有habit
     */
    @Delete
    void deleteHabits(List<Habit> habits);

    /**
     * 删除指定id的habit
     *
     * @param habitId
     * @return
     */
    @Query("DELETE FROM Habit WHERE id=:habitId")
    int deleteTaskById(String habitId);

    /**
     * 删除所有completed为真的habits
     *
     * @return
     */
    @Query("DELETE FROM Habit WHERE completed=1")
    int deleteCompletedHabits();
    //TODO 源码这里completed=1,1是sqlite中boolean的true的替代值
    //TODO 这里的sqlite命令,等号两边都没有加空格

}
