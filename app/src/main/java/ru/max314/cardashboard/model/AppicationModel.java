package ru.max314.cardashboard.model;

import android.content.Context;
import android.util.Log;
import android.util.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import ru.max314.cardashboard.App;
import ru.max314.cardashboard.LocationService;
import ru.max314.util.LogHelper;
import ru.max314.util.SpeechUtils;
import ru.max314.util.threads.LoopingThread;
import ru.max314.util.threads.TimerHelper;

/**
 * Класс модеь приложения тут будем держать все данные
 * Created by max on 15.12.2014.
 */
public class AppicationModel {
    protected static LogHelper Log = new LogHelper(AppicationModel.class);
    LocationService locationService = null;
    LoopingThread loopingThread = new LoopingThread();
    TimerHelper logSaveWatcher = null;
    TimerHelper dateChangerWatcher = null;
    TimerHelper locationVerifyWatcher = null;

    ModelData modelData = new ModelData();

    public void startLocationService() {
        if (locationService == null) {
            locationService = new LocationService((android.location.LocationManager) App.getInstance().getSystemService(Context.LOCATION_SERVICE), modelData);
            locationService.start();
        }
    }

    public void stoptLocationService() {
        if (locationService != null) {
            locationService.tryStop();
            locationService = null;
        }
    }

    public boolean isLocationServiceStarted() {
        return locationService == null;
    }

    public ModelData getModelData() {
        return modelData;
    }

    public void setModelData(ModelData modelData) {
        this.modelData = modelData;
    }

    public void initAftreCreate() {
        modelData.tripStartReset();
        Log.d("Reset trip from engine start.");
        // Если нет текущей даты установим ее
        if (modelData.getCurrentDate() == null) {
            modelData.setCurrentDate(new Date());
        }

        //region переодические задачи в отдельном потоке

        configBackgoundTask();
    }

    private void configBackgoundTask() {
        dateChangerWatcher = new TimerHelper("каждые пол минуты проверяем смену даты",
                TimeUnit.MILLISECONDS.convert(1,TimeUnit.MINUTES), // Начинаем через миуту
                TimeUnit.MILLISECONDS.convert(30,TimeUnit.SECONDS), // каждые полминуты
                new Runnable() {
            @Override
            public void run() {
                dateChanger();
            }
        });
        dateChangerWatcher.start();


        logSaveWatcher = new TimerHelper("Раз в три минуты сбрасываем лог местоположения",
                TimeUnit.MILLISECONDS.convert(3,TimeUnit.MINUTES), // Начинаем через миуту
                TimeUnit.MILLISECONDS.convert(3,TimeUnit.MINUTES), // каждые полминуты
                new Runnable() {
            @Override
            public void run() {
                saveAll();
            }
        });
        logSaveWatcher.start();

        locationVerifyWatcher = new TimerHelper("проверка GPS",
                TimeUnit.MILLISECONDS.convert(5,TimeUnit.MINUTES), // Начинаем через 3 миуту
                TimeUnit.MILLISECONDS.convert(5,TimeUnit.MINUTES), // каждые 5 полминуты
                new Runnable() {
            @Override
            public void run() {
                GPSVerify();
            }
        });
        locationVerifyWatcher.start();

    }

    private void GPSVerify() {
        try {
            String res = modelData.getLocationVerifyListiner().Verify();
            if (res.length()>0){
                SpeechUtils.speech(res,true);
            }
        } catch (Exception e) {
            Log.e("error",e);
        }
    }

    public void saveAll() {
        modelData.flushLocationLogger();
        // Заодно сохраним модель
        ApplicationModelFactory.saveModel();

    }

    public void dateChanger() {
        Log.d("каждые пол минуты проверяем смену даты выполнение");
        // каждые пол минуты
        // проверяем смену даты
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dateNow = sdf.format(new Date());
        String dateModel = sdf.format(modelData.getCurrentDate());
        if (!dateModel.equals(dateNow)) {
            dayChanged();
        }
    }

    /**
     * Дата изменилась
     */
    public void dayChanged() {
        Log.d("dayChanged()");
        modelData.getTripTodaySumator().reset();
        // Установить новую дату
        modelData.setCurrentDate(new Date());
    }

}
