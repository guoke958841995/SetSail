package com.sxhalo.PullCoal.utils;

import android.os.Handler;
import android.os.Message;

/**
 *  计时器工具类
 * Created by amoldZhang on 2018/2/28.
 */
public abstract class AdvancedCountdownTimer {

    private final long mCountdownInterval;  // 定时间隔，以毫秒计

    private long mTotalTime;  // 定时时间

    private long mRemainTime;  // 剩余时间


    /**
     *  构造函数
     * @param millisInFuture 定时时间
     * @param countDownInterval  定时间隔
     */
    public AdvancedCountdownTimer(long millisInFuture, long countDownInterval) {
        mTotalTime = millisInFuture;
        mCountdownInterval = countDownInterval;

        mRemainTime = millisInFuture;
    }

    public final void seek(int value) {
        synchronized (AdvancedCountdownTimer.this) {
            mRemainTime = ((100 - value) * mTotalTime) / 100;
        }
    }


    /**
     * 取消计时
     */
    public final void cancel() {
        mHandler.removeMessages(MSG_RUN);
        mHandler.removeMessages(MSG_PAUSE);
    }

    /**
     * 重新开始计时
     */
    public final void resume() {
        mHandler.removeMessages(MSG_PAUSE);
        mHandler.sendMessageAtFrontOfQueue(mHandler.obtainMessage(MSG_RUN));
    }

    /**
     * 暂停计时
     */
    public final void pause() {
        mHandler.removeMessages(MSG_RUN);
        mHandler.sendMessageAtFrontOfQueue(mHandler.obtainMessage(MSG_PAUSE));
    }


    /**
     * 开始计时
     * @return
     */
    public synchronized final AdvancedCountdownTimer start() {
        // 计时结束后返回
        if (mRemainTime <= 0) {
            onFinish();
            return this;
        }
        // 设置计时间隔
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_RUN),
                mCountdownInterval);
        return this;
    }

    /**
     *  计时中
     * @param millisUntilFinished
     * @param percent
     */
    public abstract void onTick(long millisUntilFinished, int percent);

    /**
     * 计时结束
     */
    public abstract void onFinish();

    private static final int MSG_RUN = 1;
    private static final int MSG_PAUSE = 2;

    /**
     * 通过handler更新android UI，显示定时时间
     */
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            synchronized (AdvancedCountdownTimer.this) {
                if (msg.what == MSG_RUN) {
                    mRemainTime = mRemainTime - mCountdownInterval;

                    if (mRemainTime <= 0) {
                        onFinish();
                    } else if (mRemainTime < mCountdownInterval) {
                        sendMessageDelayed(obtainMessage(MSG_RUN), mRemainTime);
                    } else {

                        onTick(mRemainTime, new Long(100
                                * (mTotalTime - mRemainTime) / mTotalTime)
                                .intValue());


                        sendMessageDelayed(obtainMessage(MSG_RUN),
                                mCountdownInterval);
                    }
                } else if (msg.what == MSG_PAUSE) {

                }
            }
        }
    };
}
