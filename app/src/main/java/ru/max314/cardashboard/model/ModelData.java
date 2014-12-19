package ru.max314.cardashboard.model;

import android.location.Location;
import android.location.LocationProvider;
import android.util.Log;

import com.google.gson.annotations.Expose;

import java.util.Date;

/*
275 байт приблизительно размер записи о локатион
1 раз в сеунду частота сьема
275*60 = 16500 минуту
990000 в час

 */
/**
 * Данные приложения
 * Created by max on 16.12.2014.
 */
public class ModelData {

    /**
     * Текущи зум на карте
     */
    private float currentZoom = -1;

    /**
     * Текущая позиция на карте
     */
    private Location currentLocation = null;

    /**
     * Статус провайдера местоположения
     */
    private int currentLocationStatus = LocationProvider.OUT_OF_SERVICE;

    /**
     * логировать местположение
     */
    private boolean logLocation = true;

    /**
     * логгер местоположения
     */
    @Expose
    private transient LocationLogger locationLogger = new LocationLogger();

    /**
     * Весь пробег
     */
    TripSumator allTripSumator = new TripSumator();
    /**
     * за сегодня пробег
     */
    TripSumator todayTripSumator = new TripSumator();
    /**
     * пробег со старта
     */
    TripSumator startTripSumator = new TripSumator();
    /**
     * пробег 1
     */
    TripSumator ownTripSumator = new TripSumator();

    Date currentDate = null;


    /**
     * Зумм карты
     * @return
     */
    public float getCurrentZoom() {
        return currentZoom;
    }

    /**
     * Зумм карты
     * @param currentZoom
     */
    public void setCurrentZoom(float currentZoom) {
        this.currentZoom = currentZoom;
    }

    /***
     * Текущее местоположение
     * @return
     */
    public Location getCurrentLocation() {
        return currentLocation;
    }

    /**
     * Установить Текущее местоположение
     * @param currentLocation
     */
    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
        if (logLocation)
            locationLogger.addEntity(currentLocation);
    }

    /***
     * Получить текущий статус GPS
     * @return
     */
    public int getCurrentLocationStatus() {
        return currentLocationStatus;
    }

    /**
     * Установить статус GPS
     * @param currentLocationStatus
     */
    public void setCurrentLocationStatus(int currentLocationStatus) {
        this.currentLocationStatus = currentLocationStatus;
    }

    /**
     * Логировать местоположение в файл
      * @return
     */
    public boolean isLogLocation() {
        return logLocation;
    }

    /**
     * Логировать местоположение в файл
     */
    public void setLogLocation(boolean logLocation) {
        this.logLocation = logLocation;
    }

    /**
     * Сохранить лог изменения местоположения
     */
    public void flushLocationLogger(){
        if (isLogLocation())
            locationLogger.performLog();
    }

    //region Trip manipulation
    /**
     * Сюросить пробег со старта двигателя
     */
    public void tripStartReset(){
        startTripSumator.reset();
    }
    /**
     * Сбросить пробег 1
     */
    public void tripOwnReset(){
        startTripSumator.reset();
    }

    public TripSumator getAllTripSumator() {
        return allTripSumator;
    }

    public TripSumator getTodayTripSumator() {
        return todayTripSumator;
    }

    public TripSumator getStartTripSumator() {
        return startTripSumator;
    }

    public TripSumator getOwnTripSumator() {
        return ownTripSumator;
    }

    //endregion


    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

}
