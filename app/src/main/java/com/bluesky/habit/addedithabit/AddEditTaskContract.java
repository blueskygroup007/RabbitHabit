package com.bluesky.habit.addedithabit;

import com.bluesky.habit.BasePresenter;
import com.bluesky.habit.BaseView;

/**
 * @author BlueSky
 * @date 2019/4/3
 * Description:
 */
public interface AddEditTaskContract {

    interface View extends BaseView<Presenter> {
        void showEmptyTaskError();

        void showTasksList();

        void setTitle(String title);

        void setDescription(String description);

        boolean isActive();
    }

    interface Presenter extends BasePresenter {
        void saveTask(String title, String description);

        /**
         * 填充task
         */
        void populateTask();

        boolean isDataMissing();
    }
}
