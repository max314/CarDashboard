package ru.max314.cardashboard.model;

import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;

import ru.max314.util.LogHelper;
import ru.max314.util.SpeechUtils;

/**
 * Created by max on 15.12.2014.
 */

public class LocationService extends Thread {
    static LogHelper Log = new LogHelper(LocationService.class);
    ILocationListenerProcessing locationListenerProcessing = null;
    private Handler handler;


    public LocationService(LocationManager locationManager ,ModelData modelData) {
        super("LocationThread");
        //locationListenerProcessing = new LocationListenerParanoicProcessing(locationManager, modelData);
        locationListenerProcessing = new LocationListenerProcessing(locationManager, modelData);
    }


    @Override
    public void run() {
        try {
            Log.d("run");
            SpeechUtils.speech("Запуск потока отслеживание местоположения",false);
            Looper.prepare();
            handler = new Handler();
            locationListenerProcessing.up();
            Looper.loop();

        } catch (Exception e) {
            Log.e("LoopingThread", e);
        }
    }

    /**
     * Остановить
     */
    public void tryStop() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                locationListenerProcessing.down();
                Looper.myLooper().quit();
            }
        });
    }

    public void dump(){
        locationListenerProcessing.dump();
    }


}
