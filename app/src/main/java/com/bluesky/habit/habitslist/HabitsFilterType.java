package com.bluesky.habit.habitslist;

/**
 * @author BlueSky
 * @date 2019/3/5
 * Description:过滤类型
 */
public enum HabitsFilterType {
    /**
     * 不过滤,显示所有任务
     */
    ALL_HABITS,

    /**
     * 只显示活动任务(过滤掉完成任务)
     */
    ACTIVE_HABITS,

    /**
     * 显示完成任务(过滤掉活动任务)
     */
    COMPLETED_HABITS
}
