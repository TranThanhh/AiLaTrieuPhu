package com.moon.ailatrieuphu;

import android.util.Log;

import com.moon.ailatrieuphu.email.GMailSender;
import com.moon.ailatrieuphu.model.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class Program {
    public static User user=null;

    public static int positionCauHoi=0;
    public static int positionPlayer=0;
    public static int positionModerator=0;

    public static String keyWordCauHoi="";
    public static String keyWordPlayer="";

    public static final int TIME_FUTURE = 60000;
    public static final int TIME_INTERVAL = 1000;

    public static String getDateTimeNow() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    public static String getRandom8NumberString() {
        Random rnd = new Random();
        int number = rnd.nextInt(99999999);
        // this will convert any number sequence into 6 character.
        return String.format("%08d", number);
    }

    public static void sendMail(final String title, final String message, final String code, final String email){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GMailSender sender = new GMailSender("ailatrieuphu.tt123@gmail.com",
                            "#1ailatrieuphu");
                    sender.sendMail(title, message + " " + code,
                            "ailatrieuphu.tt123@gmail.com", email);
                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);

                }
            }

        }).start();
    }

    public static void clearData(){
        user=null;
        positionCauHoi=0;
        positionModerator=0;
        positionPlayer=0;
        keyWordCauHoi="";
        keyWordPlayer="";
    }
}
