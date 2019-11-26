package com.moon.ailatrieuphu.email;

import android.os.CountDownTimer;

import com.moon.ailatrieuphu.RegisterActivity;

public class TimerSingleton {
    //Time het han.
    private static final int minute = 900000;
    private static final int count = 1000;
    public static CountDownTimer timer = new CountDownTimer(minute, count) {
        @Override
        public void onTick(long millisUntilFinished) {
            RegisterActivity.countTestTime = millisUntilFinished/1000 + "";
        }

        @Override
        public void onFinish() {
            //RegisterActivity.countTestTime = "Finish ne!!!!!!";
        }
    };
}
