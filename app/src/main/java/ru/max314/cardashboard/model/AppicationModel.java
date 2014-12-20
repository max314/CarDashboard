package ru.max314.cardashboard.model;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;

import ru.max314.cardashboard.App;
import ru.max314.cardashboard.LocationService;
import ru.max314.util.LogHelper;
import ru.max314.util.TimerUI;

/**
 * Класс модеь приложения тут будем держать все данные
 * Created by max on 15.12.2014.
 */
public class AppicationModel implements Runnable{
    LogHelper Log = new LogHelper(AppicationModel.class);
    LocationService locationService = null;
    TimerUI timerUI = null;

    ModelData modelData = new ModelData();

    public void startLocationService(){
        if (locationService==null){
            locationService = new LocationService((android.location.LocationManager) App.getInstance().getSystemService(Context.LOCATION_SERVICE),modelData);
            locationService.start();
        }
    }
    public void stoptLocationService(){
        if (locationService!=null){
            locationService.tryStop();
            locationService = null;
        }
    }

    public boolean isLocationServiceStarted(){
        return locationService == null;
    }

    public ModelData getModelData() {
        return modelData;
    }

    public void setModelData(ModelData modelData) {
        this.modelData = modelData;
    }

    public void initAftreCreate(){
        modelData.tripStartReset();
        Log.d("Reset trip from engine start.");
        // Если нет текущей даты установим ее
        if (modelData.getCurrentDate()==null){
            modelData.setCurrentDate(new Date());
        }
        // Раз в три минуты сбрасываем лог местоположения
        new TimerUI("Раз в три минуты сбрасываем лог местоположения",1000*60*3,new Runnable() {
            @Override
            public void run() {
                    saveAll();
                }
        })
        .start();
        timerUI = new TimerUI("каждые пол минуты проверяем смену даты",1000*30,this);
        timerUI.start();

    }

    public void saveAll(){
        modelData.flushLocationLogger();
        // Заодно сохраним модель
        ApplicationModelFactory.saveModel();

    }

    @Override
    public void run() {
        Log.d("каждые пол минуты проверяем смену даты выполнение");
        // каждые пол минуты
        // проверяем смену даты
        SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
        String dateNow = sdf.format(new Date());
        String dateModel = sdf.format(modelData.getCurrentDate());
        if (!dateModel.equals(dateNow)){
            dayChanged();
        }
    }

    /**
     * Дата изменилась
     */
    public void dayChanged(){
        Log.d("dayChanged()");
        modelData.getTripTodaySumator().reset();
        // Установить новую дату
        modelData.setCurrentDate(new Date());
    }

}
