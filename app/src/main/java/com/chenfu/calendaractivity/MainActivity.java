package com.chenfu.calendaractivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.chenfu.calendaractivity.adapter.HorizontalCalendarAdapter;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    int curPosition = 1;
    boolean isNext = false;
    boolean isPageChange = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvYear = findViewById(R.id.tv_year);
        TextView tvMonth = findViewById(R.id.tv_month);
        ViewPager2 viewPager2 = findViewById(R.id.viewpager2);
        HorizontalCalendarAdapter adapter = new HorizontalCalendarAdapter(this, new Callback() {
            @Override
            public void setTvYearAndMonth(int year, int month) {
                tvYear.setText("" + year);
                tvMonth.setText("" + month);
            }
        });
        viewPager2.setAdapter(adapter);
        viewPager2.setCurrentItem(1);
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

    public interface Callback {
        void setTvYearAndMonth(int year, int month);
    }
}