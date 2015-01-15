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
    private static final int MAX_DEEP = 1200;
    private static final int MAX_DUP_COUNT = 60*5;
    private String locationDescription = "";
    private int locationDublicates = 0;

    private transient List<Location> locationList = new ArrayList<Location>();
    private Date startDate = new Date();
    private Date lastAdd = null;


    public synchronized void addFromLocation(Location location) {

        lastAdd = new Date();
        locationList.add(location);
        while (locationList.size() > MAX_DEEP) { // если перебрали глубину подчищаем первые элементы
            locationList.remove(0);
        }

        // сравниваем паттерны
        String buff = createLocationDescription(location);
        if (buff.equals(locationDescription)) {
            locationDublicates++;
        } else {
            locationDublicates = 0;
            locationDescription = buff;
        }
        if (getLocationDublicates()>0 && (getLocationDublicates() % MAX_DUP_COUNT==0)){
            LocationLogger loger = new LocationLogger(locationList){
                @Override
                protected String getFileName() {
                    return String.format("%s.%d.ver.log", super.getFileName(),getLocationDublicates());
                }
            };
            loger.performLog();
        }
    }

    private String createLocationDescription(Location location) {
        String str = String.format("%20.17f;%20.17f;%10.5f;%10.5f;%10.5f;%10.5f",
                location.getLatitude(), location.getLongitude(), location.getSpeed(), location.getAltitude(),location.getAccuracy(),location.getBearing());
        return str;
    }

    /**
     * Если GPS залип количество повторяющихся местоположений
     *
     * @return
     */
    public int getLocationDublicates() {
        return locationDublicates;
    }

    public synchronized String Verify() {
        String result = "";
        // были данные ?
        Date now = new Date();
        if (lastAdd==null){
            result = "Проблемы GPS. Данных не поступало вообще";
        }
        else{
            if (Math.abs(now.getTime() - lastAdd.getTime()) > TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES)) {
                result = "Проблемы GPS. Данных не поступало 5 минут";
            } else {
                if (getLocationDublicates() > MAX_DUP_COUNT) { // 3 секунды
                    result = String.format("Проблемы GPS. местоположение не меняется в течении %d циклов.", getLocationDublicates());

                }
            }

        }

        return result;
    }
}
