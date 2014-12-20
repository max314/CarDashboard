package ru.max314.util;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by max on 16.12.2014.
 */
public class TimerUI {
    LogHelper Log = new LogHelper(TimerUI.class);
    Handler handlerForMainThread;
    Runnable task;
    long delayMillisec;
    Timer timer = null;
    String name;

    public TimerUI(String name, long delayMillisec, Runnable task) {
        this.name = name;
        this.task = task;
        this.delayMillisec = delayMillisec;
        handlerForMainThread = new Handler();
    }

    public void start(){
        if (timer!=null){
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {

                    // Если текущий тред не иметт потока сообщений создадим его
//                    boolean owner_Of_Looper  = false;
//                    if (Looper.myLooper()==null){
//                        owner_Of_Looper = true;
//                        Looper.prepare();
//                    }
                    Log.d(name + ": execute");
                    handlerForMainThread.post(task);
//                    if (owner_Of_Looper)
//                        Looper.loop();
                } catch (Throwable e) {
                    Log.e(name + ": error execute",e);
                }
            }
        },0,delayMillisec);
    }

    public void stop(){
        if (timer!=null){
            timer.cancel();
            timer = null;
        }
    }

}
