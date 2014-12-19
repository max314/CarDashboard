package ru.max314.cardashboard.model;

import android.location.Location;

import ru.max314.util.LocationUtils;

/**
 * Класс который будет считать пробег
 * Created by max on 18.12.2014.
 */
public class TripSumator {

    private double startOffset;
    private double summator = 0;
    private transient Location lastKnowLocation;

    /**
     * Сбросить начальное значеиеt
     */
    public void reset(){
        setStartOffset(0);
    }

    public synchronized void addFromLocation(Location location){
        // первый запуск
        if (lastKnowLocation==null){
            lastKnowLocation=location;
            return;
        }
        if (LocationUtils.isSpeedZerro(location) || (LocationUtils.isEq(location,lastKnowLocation))){
            return;
        }
        double delta = lastKnowLocation.distanceTo(location);
        summator += delta;
        lastKnowLocation=location;
    }

    public double getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(double startOffset) {
        this.startOffset = startOffset;
        summator=0;
    }

    public double getTrip(){
        return startOffset+summator;
    }

    public String getTripKM(){
        return String.format("%.0f",(startOffset+summator)/1000);
    }
}
