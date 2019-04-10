package com.bluesky.habit.habitslist;

import com.bluesky.habit.BasePresenter;
import com.bluesky.habit.BaseView;
import com.bluesky.habit.data.Habit;

import java.util.List;

/**
 * @author BlueSky
 * @date 2019/3/5
 * Description: Taskist功能的契约类,包含V和P的接口
 */
public interface HabitsListContract {

    /**
     * P的接口,先定义,因为V要用到
     */
    interface Presenter extends BasePresenter {
        /**
         * onActivityResult传递用
         *
         * @param requestCode
         * @param resultCode
         */
        void result(int requestCode, int resultCode);

        /**
         * 从网络加载task列表
         *
         * @param forceUpdate 是否强制更新
         */
        void loadHabits(boolean forceUpdate);

        /**
         * 添加新task
         */
        void addNewHabit();

        /**
         * 打开某个task详情页
         *
         * @param requestedHabit
         */
        void openTaskDetails(Habit requestedHabit);

        /**
         * 结束某个Task
         *
         * @param completedHabit
         */
        void completeHabit(Habit completedHabit);

        /**
         * 激活某个task
         *
         * @param activeHabit
         */
        void activateHabit(Habit activeHabit);

        /**
         * 清除所有已经完成的Task
         */
        void clearCompletedHabits();

        /**
         * 设置过滤类型
         *
         * @param requestType
         */
        void setFiltering(HabitsFilterType requestType);

        /**
         * 得到当前过滤类型
         *
         * @return
         */
        HabitsFilterType getFiltering();
    }

    /**
     * V的接口
     */
    interface View extends BaseView<Presenter> {

        /**
         * 设置loading指示器.
         *
         * @param active 是否显示刷新进度
         */
        void setLoadingIndicator(boolean active);

        /**
         * 显示所有tasks列表
         *
         * @param habits
         */
        void showHabits(List<Habit> habits);

        /**
         * 显示添加任务界面
         */
        void showAddHabit();

        /**
         * 显示任务详情界面
         *
         * @param habitId 任务的id
         */
        void showHabitDetailsUi(String habitId);

        /**
         * 显示"任务标注完成"
         */
        void showHabitMarkedComplete();

        /**
         * 显示"任务标注激活"
         */
        void showHabitMarkedActive();

        /**
         * 显示"已完成任务清除"
         */
        void showCompletedHabitsCleared();

        /**
         * 显示"获取任务失败"
         */
        void showLoadingHabitsError();

        /**
         * 显示"没有任务"
         */
        void showNoHabits();

        /**
         * 显示"激活"过滤标识
         */
        void showActiveFilterLabel();

        /**
         * 显示"已完成"过滤标识
         */
        void showCompletedFilterLabel();

        /**
         * 显示所有过滤标识
         */
        void showAllFilterLabel();

        /**
         * 显示"没有已激活任务"
         */
        void showNoActiveHabits();

        /**
         * 显示"没有已完成任务"
         */
        void showNoCompletedHabits();

        /**
         * 显示成功保存信息
         */
        void showSuccessfullySavedMessage();

        /**
         * 显示过滤气泡
         */
        void showFilteringPopUpMenu();

        /**
         * 是否激活
         *
         * @return
         */
        //Todo 具体用途不明
        boolean isActive();
    }
}
