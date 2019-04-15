package com.tony.clicksample;

import android.app.Instrumentation;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by lance on 16/6/12.
 * 保活服务，用于检测程序是否在前台运行
 */
public class DomainService extends Service {
    public static final String TAG = "DomainService";

    // 保活进程，保证切换到后台的最大时间间隔
    private final static int DOMAIN_INTERVAL = 1_000;

    // 手指点击状态
    private int pointerState = 0;

    private int centerX = 300;
    private int centerY = 700;
    private int pointerX = 300;
    private int pointerY = 700;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while (true) {

                if (pointerState % 2 == 0) {
                    pointerX = 50;
                    pointerY = 1100;
                } else {
                    pointerX = 600;
                    pointerY = 750;
                }
                pointerState++;

                try {
                    mInst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, pointerX, pointerY, 0));
                    mInst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, pointerX, pointerY, 0));
                } catch (Exception e) {
                    Log.w(TAG, "点击异常：" + e.getMessage());
                }

                Log.i(TAG, "执行点击：" + " - " + pointerX + " - " + pointerY);
                SystemClock.sleep(DOMAIN_INTERVAL);
            }
        }
    };

    private Instrumentation mInst = new Instrumentation();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean isActive = false;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (!isActive) {
            isActive = true;
            DisplayMetrics dm = getResources().getDisplayMetrics();
            centerX = dm.widthPixels / 2;
            centerY = dm.heightPixels / 2;

            stopTimerTask();
            startTimerTask();
        }

        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        // 这里必须手动停止线程, 否则service销毁依然会继续运行
        stopTimerTask();
        super.onDestroy();
        // 不允许被杀死 -- 最新SDK版本可能会有限制
        startService(new Intent(this, DomainService.class));
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    private void startTimerTask() {

        SingleThreadPool.execute(runnable);
    }

    private void stopTimerTask() {

    }

}
