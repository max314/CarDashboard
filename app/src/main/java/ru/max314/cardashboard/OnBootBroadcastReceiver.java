package ru.max314.cardashboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnBootBroadcastReceiver extends BroadcastReceiver {
    public OnBootBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        BootMaxService.start(context);
    }
}
