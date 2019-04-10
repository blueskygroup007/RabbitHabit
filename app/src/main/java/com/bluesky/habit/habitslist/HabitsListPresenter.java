package com.bluesky.habit.habitslist;

import android.app.Activity;

import com.bluesky.habit.data.Habit;
import com.bluesky.habit.data.source.HabitsDataSource;
import com.bluesky.habit.data.source.HabitsRepository;
import com.bluesky.habit.util.EspressoIdlingResource;
import com.bluesky.habit.addedithabit.AddEditTaskActivity;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author BlueSky
 * @date 2019/3/12
 * Description:
 */
public class HabitsListPresenter implements HabitsListContract.Presenter {

    private final HabitsRepository mTasksRepository;

    private final HabitsListContract.View mTasksView;

    private HabitsFilterType mCurrentFiltering = HabitsFilterType.ALL_HABITS;

    private boolean mFirstLoad = true;

    public HabitsListPresenter(HabitsRepository tasksRepository, HabitsListContract.View tasksView) {
        mTasksRepository = tasksRepository;
        mTasksView = tasksView;

        mTasksView.setPresenter(this);
    }

    @Override
    public void start() {
        loadHabits(false);
    }


    @Override
    public void result(int requestCode, int resultCode) {
        //如果一个任务被成功添加,显示一个snackbar弹窗
        if (AddEditTaskActivity.REQUEST_ADD_TASK == requestCode && Activity.RESULT_OK == resultCode) {
            mTasksView.showSuccessfullySavedMessage();
        }
    }

    @Override
    public void loadHabits(boolean forceUpdate) {
        //首次加载时,强制网络加载
        loadTasks(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    private void loadTasks(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            mTasksView.setLoadingIndicator(true);
        }
        if (forceUpdate) {
            mTasksRepository.refreshHabits();
        }
        //App进入阻塞
        EspressoIdlingResource.increment();//App is busy
        //接下来执行耗时操作
        mTasksRepository.getHabits(new HabitsDataSource.LoadHabitsCallback() {
            @Override
            public void onHabitsLoaded(List<Habit> habits) {
                List<Habit> tasksToShow = new ArrayList<>();

                //这个回调可能被调用两次,一次是cache,一次是从服务器中取出数据.
                //所以,在decrement执行前检查,否则会抛出异常
                //如果计数器不为0
                if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                    EspressoIdlingResource.decrement();//Set app as idle
                }

                //用requestType过滤tasks
                for (Habit habit :
                        habits) {
                    switch (mCurrentFiltering) {
                        case ALL_HABITS:
                            tasksToShow.add(habit);
                            break;
                        case ACTIVE_HABITS:
                            if (habit.isActive()) {
                                tasksToShow.add(habit);
                            }
                            break;
                        case COMPLETED_HABITS:
                            if (habit.isCompleted()) {
                                tasksToShow.add(habit);
                            }
                            break;
                        default:
                            tasksToShow.add(habit);
                            break;
                    }
                }
                //当前view可能无法处理UI更新
                if (!mTasksView.isActive()) {
                    return;
                }
                if (showLoadingUI) {
                    mTasksView.setLoadingIndicator(false);
                }
                processTasks(tasksToShow);
            }

            @Override
            public void onDataNotAvailable() {
                //当前view可能无法处理UI更新
                if (!mTasksView.isActive()) {
                    return;
                }
                mTasksView.showLoadingHabitsError();
            }
        });
    }

    private void processTasks(List<Habit> tasksToShow) {
        if (tasksToShow.isEmpty()) {
            //所选task类型下,为空
            //显示一个"为空"的提示信息
            processEmptyTasks();
        } else {
            mTasksView.showHabits(tasksToShow);
            //显示过滤标签
            showFilterLabel();
        }
    }

    /**
     * 显示过滤标签
     */
    private void showFilterLabel() {
        switch (mCurrentFiltering) {
            case ACTIVE_HABITS:
                mTasksView.showActiveFilterLabel();
                break;
            case COMPLETED_HABITS:
                mTasksView.showCompletedFilterLabel();
                break;
            default:
                mTasksView.showAllFilterLabel();
                break;
        }
    }

    /**
     * 显示无相关tasks提示信息
     */
    private void processEmptyTasks() {
        switch (mCurrentFiltering) {
            case ACTIVE_HABITS:
                mTasksView.showNoActiveHabits();
                break;
            case COMPLETED_HABITS:
                mTasksView.showNoCompletedHabits();
                break;
            default:
                mTasksView.showNoHabits();
                break;
        }
    }


    @Override
    public void addNewHabit() {
        mTasksView.showAddHabit();
    }

    @Override
    public void openTaskDetails(Habit requestedHabit) {
        checkNotNull(requestedHabit, "requestedHabit cannot be null!");
        mTasksView.showHabitDetailsUi(requestedHabit.getId());
    }

    @Override
    public void completeHabit(Habit completedHabit) {
        checkNotNull(completedHabit, "completedHabit cannot be null!");
        //先将服务器修改task为complete
        mTasksRepository.completeHabit(completedHabit);
        //显示提示信息
        mTasksView.showHabitMarkedComplete();
        //重新取出tasks
        loadTasks(false, false);
    }

    @Override
    public void activateHabit(Habit activeHabit) {
        checkNotNull(activeHabit, "activeHabit cannot be null!");
        mTasksRepository.activateHabit(activeHabit);
        mTasksView.showHabitMarkedActive();
        loadTasks(false, false);
    }

    @Override
    public void clearCompletedHabits() {
        mTasksRepository.clearCompletedHabits();
        mTasksView.showCompletedHabitsCleared();
        loadTasks(false, false);
    }

    @Override
    public void setFiltering(HabitsFilterType requestType) {
        mCurrentFiltering = requestType;
    }

    @Override
    public HabitsFilterType getFiltering() {
        return mCurrentFiltering;
    }


}
