package ru.max314.cardashboard.model;

import android.location.Location;
import android.location.LocationProvider;

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
     * Текущи зум на карте open street
     */
    private int currentOpenStreetZoom = -1;


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
    TripSumator tripAllSumator = new TripSumator();
    /**
     * за сегодня пробег
     */
    TripSumator tripTodaySumator = new TripSumator();
    /**
     * пробег со старта
     */
    TripSumator tripStartSumator = new TripSumator();
    /**
     * пробег 1
     */
    TripSumator tripOneSumator = new TripSumator();

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
     * Текущей зум openStreet
     * @return
     */
    public int getCurrentOpenStreetZoom() {
        return currentOpenStreetZoom;
    }

    /***
     * Текущей зум openStreet
     * @return
     */
    public void setCurrentOpenStreetZoom(int currentOpenStreetZoom) {
        this.currentOpenStreetZoom = currentOpenStreetZoom;
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
        this.tripAllSumator.addFromLocation(currentLocation);
        this.tripOneSumator.addFromLocation(currentLocation);
        this.tripStartSumator.addFromLocation(currentLocation);
        this.tripTodaySumator.addFromLocation(currentLocation);
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
        tripStartSumator.reset();
    }
    /**
     * Сбросить пробег 1
     */
    public void tripOwnReset(){
        tripStartSumator.reset();
    }

    public TripSumator getTripAllSumator() {
        return tripAllSumator;
    }

    public TripSumator getTripTodaySumator() {
        return tripTodaySumator;
    }

    public TripSumator getTripStartSumator() {
        return tripStartSumator;
    }

    public TripSumator getTripOneSumator() {
        return tripOneSumator;
    }

    //endregion


    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

}
