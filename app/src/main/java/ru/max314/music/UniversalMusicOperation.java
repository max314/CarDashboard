package ru.max314.music;

import android.content.Intent;
import android.os.Handler;
import android.view.KeyEvent;
import ru.max314.cardashboard.App;
import ru.max314.util.DisplayToast;

/**
 * Created by max on 11.11.2014.
 */
public class UniversalMusicOperation implements IMusicOpertion {

    Handler handler;
    private boolean showToast = false;


    public UniversalMusicOperation(boolean showToast) {
        this.showToast = showToast;
        handler = new Handler();
    }

    @Override
    public void play() {
        runOperaton(KeyEvent.KEYCODE_MEDIA_PLAY);
    }

    @Override
    public void pause() {
        runOperaton(KeyEvent.KEYCODE_MEDIA_PAUSE);
    }

    @Override
    public void playPause() {
        runOperaton(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
    }

    @Override
    public void nextTrack() {
        runOperaton(KeyEvent.KEYCODE_MEDIA_NEXT);
    }

    @Override
    public void prevTrack() {
        runOperaton(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
    }

    private void runOperaton(int oper) {
        sendToast(oper);
        Intent mbIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mbIntent.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, oper));
        App.getInstance().sendOrderedBroadcast(mbIntent, null);
        mbIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mbIntent.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, oper));
        App.getInstance().sendOrderedBroadcast(mbIntent, null);
    }

    private void sendToast(int oper) {
        if (!showToast)
            return;
        String operStr = "operation not detected";
        switch (oper) {
            case KeyEvent.KEYCODE_MEDIA_PLAY:
                operStr = "KEYCODE_MEDIA_PLAY";
                break;
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                operStr = "KEYCODE_MEDIA_PAUSE";
                break;
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                operStr = "KEYCODE_MEDIA_PLAY_PAUSE";
                break;
            case KeyEvent.KEYCODE_MEDIA_NEXT:
                operStr = "KEYCODE_MEDIA_NEXT";
                break;
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                operStr = "KEYCODE_MEDIA_PREVIOUS";
                break;
        }
        operStr = String.format("Сформирован бродкаст с прарметром (%s)", operStr);
        handler.post(new DisplayToast(App.getInstance(), operStr,false));
    }

}
