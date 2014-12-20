package ru.max314.cardashboard.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ru.max314.util.LogHelper;

public class OnBootBroadcastReceiver extends BroadcastReceiver {
    LogHelper Log = new LogHelper(OnBootBroadcastReceiver.class);
    public OnBootBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("onReceive");
        BootCarService.start(context);
    }
}
