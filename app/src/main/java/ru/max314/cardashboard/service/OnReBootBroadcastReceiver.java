package ru.max314.cardashboard.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ru.max314.cardashboard.model.ApplicationModelFactory;
import ru.max314.util.LogHelper;

public class OnReBootBroadcastReceiver extends BroadcastReceiver {
    protected static LogHelper Log = new LogHelper(OnReBootBroadcastReceiver.class);
    public OnReBootBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("onReceive");
        ApplicationModelFactory.getModel().saveAll();
    }
}
