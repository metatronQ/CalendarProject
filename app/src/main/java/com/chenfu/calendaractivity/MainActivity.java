package com.chenfu.calendaractivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.chenfu.calendaractivity.adapter.BaseAdapter;
import com.chenfu.calendaractivity.adapter.ClipAdapter;
import com.chenfu.calendaractivity.adapter.MonthCalendarAdapter;
import com.chenfu.calendaractivity.adapter.WeekCalendarAdapter;
import com.chenfu.calendaractivity.util.CalendarUtil;
import com.chenfu.calendaractivity.util.DisplayUtils;
import com.chenfu.calendaractivity.view.MyScrollView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    boolean isNext = false;
    boolean isPageChange = false;

    boolean isWeekChanged = false;
    boolean isPlaceHolderChange = false;

    int rawCount = 6;
    int raw = 0;
    int itemHeight = 0;

    float monthHeight = 0;
    float displayTiming = 0;

    public void initToday(TextView tvYear, TextView tvMonth) {
        GlobalInstance.INSTANCE.setSelectedYear(Calendar.getInstance().get(Calendar.YEAR));
        GlobalInstance.INSTANCE.setSelectedMonth(Calendar.getInstance().get(Calendar.MONTH) + 1);
        GlobalInstance.INSTANCE.setSelectedDay(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        tvYear.setText("" + GlobalInstance.INSTANCE.getSelectedYear());
        tvMonth.setText("" + GlobalInstance.INSTANCE.getSelectedMonth());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout placeHolderLl = findViewById(R.id.placeholder_container);
        TextView clickPlaceHolderTv = findViewById(R.id.click_placeholder_tv);
        clickPlaceHolderTv.setOnClickListener(v -> Toast.makeText(this, "content的点击事件", Toast.LENGTH_SHORT).show());
        MyScrollView scrollView = findViewById(R.id.my_scroll_view);
        TextView tvYear = findViewById(R.id.tv_year);
        TextView tvMonth = findViewById(R.id.tv_month);
        // 由于initToday只在初始化今天时使用，因此移至此处init
        initToday(tvYear, tvMonth);
        ViewPager2 monthViewPager2 = findViewById(R.id.month_view_pager);
        ViewPager2 weekViewPager2 = findViewById(R.id.week_view_pager);
        Callback callback = (year, month) -> {
            tvYear.setText("" + year);
            tvMonth.setText("" + month);
        };
        MonthCalendarAdapter adapterMonth = new MonthCalendarAdapter(this, callback);
        bindAdapter(monthViewPager2, adapterMonth);
        WeekCalendarAdapter adapterWeek = new WeekCalendarAdapter(this, callback);
        bindAdapter(weekViewPager2, adapterWeek);
        weekViewPager2.setVisibility(View.GONE);
        GlobalInstance.INSTANCE.setListener(new GlobalInstance.UpdateListener() {

            @Override
            public void updateMonth2Week(float translationY) {
                monthViewPager2.setTranslationY(translationY);
                if (!isPlaceHolderChange) {
                    placeHolderLl.setTranslationY(translationY);
                }
                if (GlobalInstance.INSTANCE.isAbsoluteLess(translationY, displayTiming, 10) && !isWeekChanged) {
                    weekViewPager2.setVisibility(View.VISIBLE);
                    isWeekChanged = true;
                }
                if (GlobalInstance.INSTANCE.isAbsoluteLess(monthHeight + translationY, itemHeight * 1f, 10) && !isPlaceHolderChange) {
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) placeHolderLl.getLayoutParams();
                    lp.removeRule(RelativeLayout.BELOW);
                    lp.addRule(RelativeLayout.BELOW, weekViewPager2.getId());
                    placeHolderLl.setLayoutParams(lp);
                    isPlaceHolderChange = true;
                    placeHolderLl.setTranslationY(0);
                }
            }

            @Override
            public void updateWeek2Month(float translationY) {
                monthViewPager2.setTranslationY(translationY);
                if (isPlaceHolderChange) {
                    placeHolderLl.setTranslationY(translationY);
                }
                if (GlobalInstance.INSTANCE.isAbsoluteLess(translationY, displayTiming, 10) && !isWeekChanged) {
                    weekViewPager2.setVisibility(View.GONE);
                    isWeekChanged = true;
                }
                if (GlobalInstance.INSTANCE.isAbsoluteLess(monthHeight + translationY, itemHeight * 1f, 10) && !isPlaceHolderChange) {
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) placeHolderLl.getLayoutParams();
                    lp.removeRule(RelativeLayout.BELOW);
                    lp.addRule(RelativeLayout.BELOW, monthViewPager2.getId());
                    placeHolderLl.setLayoutParams(lp);
                    isPlaceHolderChange = true;
                    placeHolderLl.setTranslationY(translationY);
                }
            }
        });
        scrollView.setCallback(new Callback2Update() {
            @Override
            public void start2Week() {
                if (GlobalInstance.INSTANCE.isMonth() && !GlobalInstance.INSTANCE.isAnimatorStarted()) {
                    raw = new CalendarUtil().getRaw(GlobalInstance.INSTANCE.getSelectedYear(), GlobalInstance.INSTANCE.getSelectedMonth() - 1, GlobalInstance.INSTANCE.getSelectedDay());
                    rawCount = new CalendarUtil().getAllRaws(GlobalInstance.INSTANCE.getSelectedYear(), GlobalInstance.INSTANCE.getSelectedMonth() - 1, GlobalInstance.INSTANCE.getSelectedDay());
                    itemHeight = DisplayUtils.dip2px(monthViewPager2.getContext(), 300f) / rawCount;

                    monthHeight = itemHeight * rawCount * 1f;
                    displayTiming = -(raw - 1) * itemHeight;

                    // 月切周，月数据已经确定，需要在开始动画前更新周的数据和视图
                    adapterWeek.updateSelect();
                    weekViewPager2.setCurrentItem(1, false);

                    monthViewPager2.setVisibility(View.VISIBLE);
                    isWeekChanged = false;
                    isPlaceHolderChange = false;
                    GlobalInstance.INSTANCE.startAnimationForWeek(0f, monthHeight, 500);
                }
            }

            @Override
            public void start2Month() {
                if (!GlobalInstance.INSTANCE.isMonth() && !GlobalInstance.INSTANCE.isAnimatorStarted()) {
                    raw = new CalendarUtil().getRaw(GlobalInstance.INSTANCE.getSelectedYear(), GlobalInstance.INSTANCE.getSelectedMonth() - 1, GlobalInstance.INSTANCE.getSelectedDay());
                    rawCount = new CalendarUtil().getAllRaws(GlobalInstance.INSTANCE.getSelectedYear(), GlobalInstance.INSTANCE.getSelectedMonth() - 1, GlobalInstance.INSTANCE.getSelectedDay());
                    itemHeight = DisplayUtils.dip2px(monthViewPager2.getContext(), 300f) / rawCount;

                    monthHeight = itemHeight * rawCount;
                    displayTiming = -(raw - 1) * itemHeight;

                    // 周切月，周数据已经确定，需要在开始动画前更新月的数据和视图
                    adapterMonth.updateSelect();
                    monthViewPager2.setCurrentItem(1, false);
                    monthViewPager2.setVisibility(View.VISIBLE);
                    isWeekChanged = false;
                    isPlaceHolderChange = false;
                    GlobalInstance.INSTANCE.startAnimationForMonth(-DisplayUtils.dip2px(monthViewPager2.getContext(), 300f), monthHeight, 500);
                }
            }

            @Override
            public void update() {

            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        String data[] = {"1","2","3","4","5","6","7","8","9","10","11","12","13"};
        ClipAdapter adapter = new ClipAdapter(this, data);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new MyItemDecoration(this, data));
    }

    public void bindAdapter(ViewPager2 viewPager2, BaseAdapter adapter) {
        viewPager2.setAdapter(adapter);
        viewPager2.setCurrentItem(1, false);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            /**
             * 这里可以判断左移或右移的趋势
             * 滑动或代码设置平滑切换会回调
             * @param position 向右滑即next则当前位置，向左滑即pre则为上一个位置
             * @param positionOffset position相对偏移的百分比
             * @param positionOffsetPixels position相对偏移的像素
             */
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                Log.d(TAG, "onPageScrolled: " + position + " " + positionOffset + " " + positionOffsetPixels);
                // 滑动到最后稳定时会position回到指定页，则通过对比中间页判断切换方向，之后会回调onPageScrollStateChanged
                // 其实应该在onPageSelected中判断
//                if (position > 1) {
//                    // 右滑
//                    isNext = true;
//                } else if (position < 1){
//                    // 左滑
//                    isNext = false;
//                }
            }

            /**
             * @param position 到达选中的位置，滑动而未换位置则不会触发此方法
             *                 这里可以确定是否切页，切页则会回调该方法，代码设置某页也会回调
             */
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Log.d(TAG, "onPageSelected: " + position);
                if (position == 1) {
                    // 初始设置中间和切换上下一页完毕后重回中间
                    isPageChange = false;
                } else {
                    // true右滑
                    isNext = position > 1;
                    isPageChange = true;
                }
            }

            /**
             * @param state 0代表空闲或滑动完全结束，1代表正在滑，而且同一次滑动动作只会回调一次
             *              2代表当前滑动动作结束，正在稳定至0状态，代码设置某页也会回调
             */
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                Log.d(TAG, "onPageScrollStateChanged: " + state);
                if (state == ViewPager2.SCROLL_STATE_IDLE && isPageChange) {
                    if (isNext) {
                        adapter.updateNext();
                    } else {
                        adapter.updatePre();
                    }
                    viewPager2.setCurrentItem(1, false);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalInstance.INSTANCE.release();
    }

    public interface Callback {
        void setTvYearAndMonth(int year, int month);
    }
}