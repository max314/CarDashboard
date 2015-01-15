package ru.max314.cardashboard.model;

import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

import ru.max314.cardashboard.App;
import ru.max314.util.LogHelper;
import ru.max314.util.SpeechUtils;
import ru.max314.util.SysUtils;

/**
 * Created by max on 14.01.2015.
 * параноидный листинер))
 */
public class LocationListenerParanoicProcessing implements LocationListener, GpsStatus.Listener, GpsStatus.NmeaListener, ILocationListenerProcessing {
    private static LogHelper Log = new LogHelper(LocationListenerParanoicProcessing.class);

    private LocationManager locationManager = null;
    private ModelData modelData = null;
    StringBuilder satteliteLog = new StringBuilder();

    public LocationListenerParanoicProcessing(LocationManager locationManager, ModelData modelData) {
        this.locationManager = locationManager;
        this.modelData = modelData;
    }

    public void up() {
        boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        locationManager.addGpsStatusListener(this);
        locationManager.addNmeaListener(this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }


    public void down() {
        locationManager.removeUpdates(this);
        locationManager.removeGpsStatusListener(this);
    }

    @Override
    public synchronized void dump() {

        try {
            FileWriter out = new FileWriter(new File(getFileName()));
            out.write(satteliteLog.toString());
            out.close();
            satteliteLog = new StringBuilder();
        } catch (IOException e) {
            Log.e("Error write string as file", e);
        }

    }

    protected String getFileName() {
        Format formatter = new SimpleDateFormat("yyyy-MM-dd-HH-00");
        String fileName = String.format("%s-paranoic-%s.log", formatter.format(new Date()), UUID.randomUUID().toString());
        File path = new File(App.getInstance().getFilesDir().getPath() + "/log/");
        if (!path.exists()) {
            path.mkdir();
        }
        String fullFileName = new File(path, fileName).getPath();
        return fullFileName;
    }


    public void onGpsStatusChanged(int event) {
        switch (event) {
            case GpsStatus.GPS_EVENT_STARTED:
                SpeechUtils.speech("GPS статус. запущенно", false);
                // do your tasks
                break;
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                SpeechUtils.speech("GPS статус. первая фиксация", false);
                onGpsStatus();
                // do your tasks
                break;
            case GpsStatus.GPS_EVENT_STOPPED:
                SpeechUtils.speech("GPS статус. остановленно", false);
                // do your tasks
                break;
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                // do your tasks
                break;
        }
    }


    private void onGpsStatus() {
        GpsStatus gpsStatus1 = locationManager.getGpsStatus(null);
        if (gpsStatus1 != null) {
            Iterable<GpsSatellite> satellites = gpsStatus1.getSatellites();
            Iterator<GpsSatellite> sat = satellites.iterator();
            StringBuilder sb = new StringBuilder();
            Format format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            Date date = new Date();
            String formatted = format.format(date);
            sb.append(formatted + ";");
            int i = 0;
            while (sat.hasNext()) {
                GpsSatellite satellite = sat.next();
                String satInfo = String.format(";%3d;%3d;%s;%f;%f;%f",
                        i,
                        satellite.getPrn(),
                        new Boolean(satellite.usedInFix()).toString(),
                        satellite.getSnr(),
                        satellite.getAzimuth(),
                        satellite.getElevation()
                );
                i++;
                sb.append(satInfo);
            }
            satteliteLog.append(sb.toString() + "\n");
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
        Format format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date date = new Date(location.getTime());
        String formatted = format.format(date);
        String buff = String.format("%s;%s", formatted, location.toString());
        satteliteLog.append(buff + "\n");

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
        SpeechUtils.speech("Местоположение. статус изменен", true);
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
        SpeechUtils.speech("Местоположение. провайдер доступен " + provider, true);
        LocationService.Log.d("onProviderEnabled: provider " + provider);
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
        SpeechUtils.speech("Местоположение . провайде отключен " + provider, true);
        LocationService.Log.d("onProviderDisabled: provider " + provider);
    }

    @Override
    public void onNmeaReceived(long timestamp, String nmea) {
        Format format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date date = new Date(timestamp);
        String formatted = format.format(date);
        String buff = String.format("%s;%s", formatted, nmea);
        satteliteLog.append(buff + "\n");
    }
}
