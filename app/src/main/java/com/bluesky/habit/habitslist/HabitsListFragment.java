package com.bluesky.habit.habitslist;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bluesky.habit.data.Habit;
import com.bluesky.habit.R;
import com.bluesky.habit.addedithabit.AddEditTaskActivity;
import com.bluesky.habit.habitdetail.HabitDetailActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author BlueSky
 * @date 2019/3/5
 * Description:
 */
public class HabitsListFragment extends Fragment implements HabitsListContract.View {

    private HabitsListContract.Presenter mPresenter;

    private TaskListAdapter mListAdapter;


    // 没有任务的提示框---------

    private View mNoHabitsView;


    private ImageView mNoHabitIcon;

    private TextView mNoHabitMainView;

    private TextView mNoHabitAddView;

    // ------------------


    private LinearLayout mHabitsView;

    private TextView mFilteringLabelView;


    /**
     * 无参构造函数,必须有
     */
    public HabitsListFragment() {
    }

    /**
     * fragment的创建方法,加了两个无用参数来说明用法
     *
     * @return
     */
    public static HabitsListFragment newInstance() {
        HabitsListFragment fragment = new HabitsListFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new TaskListAdapter(new ArrayList<>(0), mItemListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        //真正的启动入口
        mPresenter.start();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        //-----------------------------------------------
        View root = inflater.inflate(R.layout.frag_habits_list, container, false);
        //初始化任务列表视图
        ListView listView = root.findViewById(R.id.lv_task_list);
        listView.setAdapter(mListAdapter);
        mFilteringLabelView = root.findViewById(R.id.tv_filtering_lable);
        mHabitsView = root.findViewById(R.id.ll_tasklist);
        //初始化no tasks窗体视图
        mNoHabitsView = root.findViewById(R.id.ll_no_task);
        mNoHabitIcon = root.findViewById(R.id.iv_no_task_icon);
        mNoHabitMainView = root.findViewById(R.id.tv_no_tasks_main);
        mNoHabitAddView = root.findViewById(R.id.tv_no_tasks_add);
        //这个控件是NoTask视图中的add控件,visible状态在别处设置
        mNoHabitAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddHabit();
            }
        });

        //设置浮动按钮(加号)
        //在fragment中设置activity的浮动按钮,它不属于当前fragment的layout中
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addNewHabit();
            }
        });

        //设置进度指示器
        final ScrollChildSwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.refresh_layout);
        //swipeRefreshLayout.setColorSchemeColors(getActivity().getColor(R.color.colorPrimary));
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));

        //设置滚动视图给自定义的SwipRefreshLayout
        swipeRefreshLayout.setScrollUpChild(listView);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadHabits(false);
            }
        });
        //不调用这个方法,onCreateOptionsMenu就不会被执行(Fragment中)
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.result(requestCode, resultCode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                showFilteringPopUpMenu();
                break;
            case R.id.menu_clear:
                mPresenter.clearCompletedHabits();
                break;
            case R.id.menu_refresh:
                mPresenter.loadHabits(true);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_task_list_frag, menu);
    }

    @Override
    public void setLoadingIndicator(final boolean active) {
        if (getView() == null) {
            return;
        }
        final SwipeRefreshLayout srl = getView().findViewById(R.id.refresh_layout);
        //确保setRefreshing()在布局完成后再被调用
        //todo View.post()
        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(active);
            }
        });

    }

    @Override
    public void showHabits(List<Habit> habits) {
        mListAdapter.replaceData(habits);
        mHabitsView.setVisibility(View.VISIBLE);
        mNoHabitsView.setVisibility(View.GONE);
    }

    @Override
    public void showAddHabit() {
        Intent intent = new Intent(getContext(), AddEditTaskActivity.class);
        startActivityForResult(intent, AddEditTaskActivity.REQUEST_ADD_TASK);
    }

    @Override
    public void showHabitDetailsUi(String habitId) {
        //源码注释说,在它自己的activity中,用以显示intent的灵活性
        Intent intent = new Intent(getContext(), HabitDetailActivity.class);
        intent.putExtra(HabitDetailActivity.EXTRA_HABIT_ID, habitId);
        startActivity(intent);
    }

    @Override
    public void showHabitMarkedComplete() {
        showMessage(getString(R.string.habit_marked_complete));
    }

    @Override
    public void showHabitMarkedActive() {
        showMessage(getString(R.string.habit_marked_active));
    }

    @Override
    public void showCompletedHabitsCleared() {
        showMessage(getString(R.string.completed_habits_cleared));
    }

    @Override
    public void showLoadingHabitsError() {
        showMessage(getString(R.string.loading_habits_error));
    }

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showNoHabits() {
        showNoTasksViews(getString(R.string.no_habits_all), R.drawable.ic_check_circle_24dp, false);
    }

    @Override
    public void showActiveFilterLabel() {
        mFilteringLabelView.setText(getString(R.string.label_active));
    }

    @Override
    public void showCompletedFilterLabel() {
        mFilteringLabelView.setText(getString(R.string.label_completed));

    }

    @Override
    public void showAllFilterLabel() {
        mFilteringLabelView.setText(getString(R.string.label_all));

    }

    @Override
    public void showNoActiveHabits() {
        showNoTasksViews(getString(R.string.no_habits_active), R.drawable.ic_check_circle_24dp, false);
    }

    @Override
    public void showNoCompletedHabits() {
        showNoTasksViews(getString(R.string.no_habits_completed), R.drawable.ic_verified_user_24dp, false);

    }

    @Override
    public void showSuccessfullySavedMessage() {
        showMessage(getString(R.string.successfully_saved_habit_message));
    }

    private void showNoTasksViews(String mainText, int iconRes, boolean showAddView) {
        mHabitsView.setVisibility(View.GONE);
        mNoHabitsView.setVisibility(View.VISIBLE);
        mNoHabitMainView.setText(mainText);
        //todo 使用了contextcompat,源码使用getResources().getDrawable
        mNoHabitIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), iconRes));
        mNoHabitAddView.setVisibility(showAddView ? View.VISIBLE : View.GONE);

    }

    @Override
    public void showFilteringPopUpMenu() {
        PopupMenu popup = new PopupMenu(getContext(), getActivity().findViewById(R.id.menu_filter));
        popup.getMenuInflater().inflate(R.menu.filter_task_list, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.active:
                        mPresenter.setFiltering(HabitsFilterType.ACTIVE_HABITS);
                        break;
                    case R.id.completed:
                        mPresenter.setFiltering(HabitsFilterType.COMPLETED_HABITS);
                        break;
                    default:
                        mPresenter.setFiltering(HabitsFilterType.ALL_HABITS);
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void setPresenter(HabitsListContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    private class TaskListAdapter extends BaseAdapter {

        private List<Habit> mHabits;
        private TaskItemListener mItemListener;

        public TaskListAdapter(List<Habit> habits, TaskItemListener itemListener) {
            mHabits = habits;
            mItemListener = itemListener;
        }

        public void replaceData(List<Habit> habits) {
            setList(habits);
            notifyDataSetChanged();
        }

        private void setList(List<Habit> habits) {
            mHabits = checkNotNull(habits);
        }


        @Override
        public int getCount() {
            return mHabits.size();
        }

        @Override
        public Habit getItem(int position) {
            return mHabits.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                rowView = inflater.inflate(R.layout.item_habit, parent, false);
            }

            final Habit habit = getItem(position);

            TextView tvTitle = rowView.findViewById(R.id.tv_title);
            tvTitle.setText(habit.getTitleForList());
            CheckBox cbComplete = rowView.findViewById(R.id.completed);
            cbComplete.setChecked(habit.isCompleted());

            if (habit.isCompleted()) {
                rowView.setSelected(true);
            } else {
                rowView.setSelected(false);
            }

            cbComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!habit.isCompleted()) {
                        mItemListener.onCompleteTaskClick(habit);
                    } else {
                        mItemListener.onActivateTaskClick(habit);
                    }
                }
            });

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemListener.onTaskClick(habit);
                }
            });
            return rowView;
        }
    }

    public interface TaskItemListener {
        void onTaskClick(Habit clickedHabit);

        void onCompleteTaskClick(Habit completedHabit);

        void onActivateTaskClick(Habit activatedHabit);
    }

    TaskItemListener mItemListener = new TaskItemListener() {
        @Override
        public void onTaskClick(Habit clickedHabit) {
            mPresenter.openTaskDetails(clickedHabit);
        }

        @Override
        public void onCompleteTaskClick(Habit completedHabit) {
            mPresenter.completeHabit(completedHabit);
        }

        @Override
        public void onActivateTaskClick(Habit activatedHabit) {
            mPresenter.activateHabit(activatedHabit);
        }
    };
}
