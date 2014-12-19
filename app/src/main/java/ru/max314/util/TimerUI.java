package ru.max314.util;


import android.os.Handler;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by max on 16.12.2014.
 */
public class TimerUI {

    Handler handlerForMainThread;
    Runnable task;
    long delayMillisec;
    Timer timer = null;

    public TimerUI( long delayMillisec, Runnable task) {
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
                handlerForMainThread.post(task);
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
