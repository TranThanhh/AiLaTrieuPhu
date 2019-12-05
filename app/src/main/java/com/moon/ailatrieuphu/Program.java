package com.moon.ailatrieuphu;

import com.moon.ailatrieuphu.model.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Program {
    public static User user=new User(55,null,null,null,null,null,0,1);
    //public static User user;
    public static int diemCao;

    public static String getDateTimeNow() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(Calendar.getInstance().getTime());
    }
}
