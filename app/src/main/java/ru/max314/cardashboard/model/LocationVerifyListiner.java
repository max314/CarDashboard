package ru.max314.cardashboard.model;

import android.location.Location;

import org.xml.sax.Locator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ru.max314.util.LocationUtils;
import ru.max314.util.LogHelper;

/**
 * Класс который будет считать пробег
 * Created by max on 18.12.2014.
 */
public class LocationVerifyListiner {
    static LogHelper Log = new LogHelper(LocationVerifyListiner.class);
    private static final int MAX_DEEP = 200;
    private String locationDescription = "";
    private int locationDublicates = 0;

    private transient List<Location> locationList = new ArrayList<Location>();
    private Date startDate = new Date();
    private Date lastAdd = null;


    public synchronized void addFromLocation(Location location){

        lastAdd = new Date();
        locationList.add(location);
        while (locationList.size()>MAX_DEEP){ // если перебрали глубину подчищаем первые элементы
            locationList.remove(0);
        }

        // сравниваем паттерны
        String buff = createLocationDescription(location);
        if (buff.equals(locationDescription)){
            locationDublicates++;
        }else {
            locationDublicates=0;
            locationDescription = buff;
        }
    }

    private String createLocationDescription(Location location){
        String str = String.format("%20.17f;%20.17f;%10.5f",location.getLatitude(),location.getLongitude(),location.getSpeed() );
        return str;
    }

    /**
     * Если GPS залип количество повторяющихся местоположений
     * @return
     */
    public int getLocationDublicates() {
        return locationDublicates;
    }

    public synchronized String Verify(){
        String result = "";
        // были данные ?
        Date now = new Date();
        if (Math.abs(now.getTime() - lastAdd.getTime())> TimeUnit.MILLISECONDS.convert(5,TimeUnit.MINUTES) ){
            result = "Данных от GPS не поступало 5 минут";
        }else{

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
