package com.bluesky.habit.addedithabit;

import com.bluesky.habit.data.Habit;
import com.bluesky.habit.data.source.HabitsDataSource;

import static androidx.core.util.Preconditions.checkNotNull;

/**
 * @author BlueSky
 * @date 2019/4/3
 * Description: 监听用户UI操作,检索数据并更新UI
 */
public class AddEditHabitPresenter implements AddEditTaskContract.Presenter, HabitsDataSource.GetHabitCallback {

    private final HabitsDataSource mTasksRepository;
    private final AddEditTaskContract.View mAddTaskView;
    private String mTaskId;
    private boolean mIsDataMissing;


    /**
     * 创建一个适用于add和edit的P
     *
     * @param taskId                 task的id,null为新建
     * @param tasksRepository        以接口引用的tasks仓库(仓库实现了该接口,并用到了local和remote)
     * @param addTaskView            V
     * @param shouldLoadDataFromRepo 是否需要取仓库取数据
     */
    public AddEditHabitPresenter(String taskId, HabitsDataSource tasksRepository,
                                 AddEditTaskContract.View addTaskView, boolean shouldLoadDataFromRepo) {
        mTasksRepository = checkNotNull(tasksRepository);
        mAddTaskView = checkNotNull(addTaskView);
        mTaskId = taskId;
        //todo 看看到底是谁传进来的这个参数(是否需要取数据),因为onResume时还要用这个参数
        mIsDataMissing = shouldLoadDataFromRepo;

        mAddTaskView.setPresenter(this);
    }

    @Override
    public void saveTask(String title, String description) {
        if (isNewTask()) {
            createTask(title, description);
        } else {
            updateTask(title, description);
        }
    }

    private void updateTask(String title, String description) {
        if (isNewTask()) {
            throw new RuntimeException("updateHabit() was called but task is new.");
        }
        mTasksRepository.saveHabit(new Habit(title, description));
        mAddTaskView.showTasksList();
    }

    private void createTask(String title, String description) {
        Habit newHabit = new Habit(title, description);
        if (newHabit.isEmpty()) {
            mAddTaskView.showEmptyTaskError();
        } else {
            mTasksRepository.saveHabit(newHabit);
            mAddTaskView.showTasksList();
        }
    }

    /**
     * populate--填充
     */
    @Override
    public void populateTask() {
        if (isNewTask()) {
            throw new RuntimeException("populateTask() was called but task is new.");
        }
        mTasksRepository.getHabit(mTaskId, this);
    }

    @Override
    public boolean isDataMissing() {
        return mIsDataMissing;
    }

    @Override
    public void start() {
        if (!isNewTask() && mIsDataMissing) {
            populateTask();
        }
    }


    private boolean isNewTask() {
        return mTaskId == null;
    }

    @Override
    public void onHabitLoaded(Habit habit) {
        if (mAddTaskView.isActive()) {
            mAddTaskView.setTitle(habit.getTitle());
            mAddTaskView.setDescription(habit.getDescription());
        }
        mIsDataMissing = false;
    }

    @Override
    public void onDataNotAvailable() {
        if (mAddTaskView.isActive()) {
            mAddTaskView.showEmptyTaskError();
        }
    }
}
