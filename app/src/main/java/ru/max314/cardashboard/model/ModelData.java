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
     * Текущи зум на карте Я
     */
    private float currentYaZoom = -1;



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

    @Expose
    private transient LocationVerifyListiner locationVerifyListiner = new LocationVerifyListiner();

    private String BTAddress ="";

    Date currentDate = null;

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
        this.locationVerifyListiner.addFromLocation(currentLocation);
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

    public LocationVerifyListiner getLocationVerifyListiner() {
        return locationVerifyListiner;
    }

    //endregion


    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    /**
     * Вернуть левый локайшен для старта
     * @return
     */
    public Location getDefaultLocation(){
        Location loc =  new Location("faeke");
        loc.setLatitude(47.20140598);
        loc.setLongitude(38.92323017);
        loc.setAccuracy(30);
        loc.setSpeed(0);
        return loc;
    }

    public String getBTAddress() {
        return BTAddress;
    }

    public void setBTAddress(String BTAddress) {
        this.BTAddress = BTAddress;
    }
}
