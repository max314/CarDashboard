package ru.max314.music;

import android.content.Intent;
import android.os.Handler;
import android.view.KeyEvent;

import ru.max314.cardashboard.App;
import ru.max314.util.DisplayToast;

/**
 * Created by max on 11.11.2014.
 * имплементация для aim через сервис
 */
public class AimpMusicOperation implements IMusicOpertion {
    Handler handler;
    private boolean showToast = false;

    public AimpMusicOperation(boolean showToast)
    {
        this.showToast=showToast;
        handler = new Handler();
    }

    @Override
    public void play() {
        runOperaton("com.aimp.service.action.PLAY");
    }

    @Override
    public void pause() {
        runOperaton("com.aimp.service.action.PAUSE");
    }

    @Override
    public void playPause() {
        runOperaton("com.aimp.service.action.PLAY_PAUSE");
    }

    @Override
    public void nextTrack() {
        runOperaton("com.aimp.service.action.NEXT_TRACK");

    }

    @Override
    public void prevTrack() {
        runOperaton("com.aimp.service.action.PREV_TRACK");
    }

    private void runOperaton(String oper) {
        sendToast(oper);
        Intent intent = new Intent();
        String packageName = "com.aimp.player";
        String className = "com.aimp.player.service.AIMPService";
        intent.setClassName(packageName, className);
        intent.setAction(oper);
//        App.instance.startService(intent);
    }

    private void sendToast(String oper) {
        if (!showToast)
            return;
        oper = String.format("Вызван сервис aimp с прарметром (%s)",oper);
        handler.post(new DisplayToast(App.getInstance(), oper, false));
    }

}
