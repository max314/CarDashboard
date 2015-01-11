package ru.max314.cardashboard.model;

import android.location.Location;

import java.util.Date;

import ru.max314.util.LocationUtils;
import ru.max314.util.LogHelper;

/**
 * Класс который будет считать пробег
 * Created by max on 18.12.2014.
 */
public class LocationVerifyListiner {
    static LogHelper Log = new LogHelper(LocationVerifyListiner.class);
    private transient Location lastKnowLocation;
    private transient Location currentLocation;
    private Date startDate = new Date();


    public synchronized void addFromLocation(Location location){
        // первый запуск
        if (lastKnowLocation==null){
            lastKnowLocation=location;
            return;
        }

        // если есь скорость но не меняться местоположение - то жопа
        if (!LocationUtils.isSpeedZerro(location)){
            if (!(location.getLatitude() == lastKnowLocation.getLatitude() && location.getLongitude()==lastKnowLocation.getLongitude())){
                lastKnowLocation=currentLocation;
            }
        }else {
            lastKnowLocation=currentLocation;
        }
        currentLocation = location;
    }

    public synchronized String Verify(){
        String result = "";
        // были данные ?
        if (lastKnowLocation==null){
            result = "Данных от GPS не поступало";
        }else{
            // проверрим время последнего фикса

            Date nowDateLastKnow = new Date(lastKnowLocation.getTime());
            long nowLastKnow = nowDateLastKnow.getMinutes()*60+nowDateLastKnow.getSeconds();
            Date nowDate = new Date();
            long now = nowDate.getMinutes()*60+nowDate.getSeconds();


            if (Math.abs(nowLastKnow - now)>20 ){ // 3 секунды
                result = "Проблемы GPS. местоположение не обновлялось в течении 20 секунд.";
            }
            if (!LocationUtils.isSpeedZerro(currentLocation)){
                if (!(currentLocation.getLatitude() == lastKnowLocation.getLatitude() && currentLocation.getLongitude()==lastKnowLocation.getLongitude())){
                    lastKnowLocation=currentLocation;
                }
                result = "Проблемы GPS. местоположение не меняеться при наличии скорости";
            }
        }
        if (result.length()!=0){
            Log.e("проблемы проверки GPS");
            Log.e("last"+lastKnowLocation.toString());
            Log.e("current"+currentLocation.toString());
            Log.e("now"+new Date().toString());
        }
        return result;
    }
}
