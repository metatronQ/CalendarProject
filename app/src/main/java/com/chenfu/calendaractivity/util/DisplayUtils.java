package com.chenfu.calendaractivity.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by hlj on 2014/9/12.
 * 屏幕相关工具类
 */
public class DisplayUtils {
    public static final Double ASPECT_RATIO_16_9 = 16.0 / 9;
    public static final Double ASPECT_RATIO_16_10 = 16.0 / 10;

    /**
     * 获取屏幕宽的像素值
     *
     * @param context 上下文
     * @return 像素值
     */
    public static int getScreenWidthPixels(@NotNull Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 收起键盘
     */
    public static void hideKeyboard(@NotNull View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive()) {
            inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }
    }

    /**
     * 显示键盘
     * @param context 上下文
     */
    public static void showInputMethod(@NotNull Context context) {
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 获取屏幕高的像素值
     *
     * @param context 上下文
     * @return 像素值
     */
    public static int getScreenHeightPixels(@NotNull Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(@NotNull Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(@NotNull Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取 density
     *
     * @param context 上下文
     * @return density
     */
    public static float getScreenDensity(@NotNull Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.density;
    }

    /**
     * 获取通知栏高度
     *
     * @param context activity context
     * @return 高度
     */
    public static int getStatusBarHeight(@NotNull Activity context) {
        Rect frame = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            return !activity.isDestroyed() && !activity.isFinishing();
        }
        return true;
    }

    public static boolean isNavigationBarShow(@NotNull Activity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        Point realSize = new Point();
        display.getSize(size);
        display.getRealSize(realSize);
        return realSize.y != size.y;
    }

    public static int getNavigationBarHeight(Activity activity) {
        if (!isNavigationBarShow(activity)) {
            return 0;
        }
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height",
                "dimen", "android");
        //获取NavigationBar的高度
        return resources.getDimensionPixelSize(resourceId);
    }

    /**
     * 最大16:9的分辨率宽高比
     */
    public static boolean isAspectRatioNormal(Context context) {
        return ASPECT_RATIO_16_9.equals(getScreenAspectRatio(context));
    }

    /**
     * 小于16:9
     * 典型值16:10
     * @param context
     * @return
     */
    public static boolean isAspectRatioSmall(Context context) {
        return getScreenAspectRatio(context) < ASPECT_RATIO_16_9;
    }
    /**
     * 大于16:9
     * 典型值18:9
     * @param context
     * @return
     */
    public static boolean isAspectRatioLarge(Context context) {
        return getScreenAspectRatio(context) > ASPECT_RATIO_16_9;
    }

    /**
     * 获取屏幕宽高比
     */
    public static double getScreenAspectRatio(Context context) {
        return 1.0 * getScreenWidth(context) / getScreenHeight(context);
    }

    /**
     * 获取屏幕高度(px)
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取屏幕宽度(px)
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static void hideBottomUIMenu(Window window) {
        //隐藏虚拟按键，并且全屏
        View decorView = window.getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Nullable
    public static Drawable getDrawableById(Context context, int resId) {
        try {
            return ContextCompat.getDrawable(context, resId);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断色值是否为浅色系
     * @param color 需要判断的色值
     * @return 是否为浅色系
     */
    public static boolean isLightColor(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness < 0.5;
    }

    /**
     * 未加载视频计算宽高
     */
    public static int[] getUnDisplayViewSize(View view) {
        int[] size = new int[2];
        if(view == null){
            return size;
        }
        int width = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        view.measure(width, height);
        size[0] = view.getMeasuredWidth();
        size[1] = view.getMeasuredHeight();
        return size;
    }

    public static boolean isPortrait(Context context) {
        Configuration configuration = context.getResources().getConfiguration();
        int orientation = configuration.orientation;
        return orientation == Configuration.ORIENTATION_PORTRAIT;
    }
}
