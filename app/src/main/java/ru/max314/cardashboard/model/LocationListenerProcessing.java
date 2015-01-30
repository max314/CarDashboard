package ru.max314.cardashboard.model;

import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.Iterator;

import ru.max314.util.LogHelper;
import ru.max314.util.SpeechUtils;

/**
* Created by max on 14.01.2015.
*/
public class LocationListenerProcessing implements LocationListener, GpsStatus.Listener, ILocationListenerProcessing {
    private static LogHelper Log = new LogHelper(LocationListenerProcessing.class);
    private LocationManager locationManager = null;
    private ModelData modelData = null;

    public LocationListenerProcessing(LocationManager locationManager, ModelData modelData) {
        this.locationManager = locationManager;
        this.modelData = modelData;
    }

    @Override
    public void up() {
        boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        locationManager.addGpsStatusListener(this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    public void down() {
        locationManager.removeUpdates(this);
        locationManager.removeGpsStatusListener(this);
    }

    @Override
    public void dump() {

    }

    public void onGpsStatusChanged(int event) {
        switch (event) {
            case GpsStatus.GPS_EVENT_STARTED:
                SpeechUtils.speech("GPS статус. запущенно", false);
                Log.d("GPS статус. запущенно");
                // do your tasks
                break;
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                Log.d("GPS статус. первая фиксация");
//                SpeechUtils.speech("GPS статус. первая фиксация",false);
                // do your tasks
                break;
            case GpsStatus.GPS_EVENT_STOPPED:
                Log.d("GPS статус. остановленно");
                SpeechUtils.speech("GPS статус. остановленно",false);
                // do your tasks
                break;
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                // do your tasks
                break;
        }
    }



/**
 * Called when the location has changed.
 * <p/>
 * <p> There are no restrictions on the use of the supplied Location object.
 *
 * @param location The new location, as a Location object.
 */
    @Override
    public void onLocationChanged(Location location) {
        LocationService.Log.d(location.toString());

        modelData.setCurrentLocation(location);
        //region Description
        //            if (lastLocation==null)
//                lastLocation = location;
//
//            if (lastLocation.getLatitude()!=location.getLatitude() || lastLocation.getLongitude()!=location.getLongitude()){
//
//                Bundle b= location.getExtras ();
//                Log.d("dddd");
//            }
//            lastLocation = location;
//            locationManager.removeUpdates(this);
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        //endregion
    }

    private Location lastLocation = null;

    /**
     * Called when the provider status changes. This method is called when
     * a provider is unable to fetch a location or if the provider has recently
     * become available after a period of unavailability.
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationService.Log.d(String.format("onStatusChanged: provider %s", provider));
        SpeechUtils.speech("Местоположение. статус изменен",true);
        modelData.setCurrentLocationStatus(status);
    }

    /**
     * Called when the provider is enabled by the user.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     */
    @Override
    public void onProviderEnabled(String provider) {
        SpeechUtils.speech("Местоположение. провайдер доступен "+provider,true);
        LocationService.Log.d("onProviderEnabled: provider "+provider);
    }

    /**
     * Called when the provider is disabled by the user. If requestLocationUpdates
     * is called on an already disabled provider, this method is called
     * immediately.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     */
    @Override
    public void onProviderDisabled(String provider) {
        SpeechUtils.speech("Местоположение . провайде отключен "+provider,true);
        LocationService.Log.d("onProviderDisabled: provider "+provider);
    }
}
