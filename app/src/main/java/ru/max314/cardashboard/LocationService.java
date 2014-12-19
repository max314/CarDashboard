package ru.max314.cardashboard;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import ru.max314.cardashboard.model.ModelData;
import ru.max314.util.LogHelper;

/**
 * Created by max on 15.12.2014.
 */

public class LocationService extends Thread {
    LogHelper Log = new LogHelper(LocationService.class);
    LocationProcessing locationProcessing = null;
    private Handler handler;


    public LocationService(LocationManager locationManager ,ModelData modelData) {
        super("LocationThread");
        locationProcessing = new LocationProcessing(locationManager, modelData);
    }


    @Override
    public void run() {
        try {
            Log.d("run");
            Looper.prepare();
            handler = new Handler();
            locationProcessing.up();
            Looper.loop();

        } catch (Exception e) {
            Log.e("LoopingThread", e);
        }
    }

    /**
     * Остановить
     */
    public void tryStop() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                locationProcessing.down();
                Looper.myLooper().quit();
            }
        });
    }


    private class LocationProcessing implements LocationListener {

        private LocationManager locationManager = null;
        private ModelData modelData = null;

        public LocationProcessing(LocationManager locationManager, ModelData modelData) {
            this.locationManager = locationManager;
            this.modelData = modelData;
        }

        public void up() {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }

        public void down() {
            locationManager.removeUpdates(this);
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
            Log.d(location.toString());
            Bundle b= location.getExtras ();
            modelData.setCurrentLocation(location);
        }

        /**
         * Called when the provider status changes. This method is called when
         * a provider is unable to fetch a location or if the provider has recently
         * become available after a period of unavailability.
         *
         * @param provider the name of the location provider associated with this
         *                 update.
         * @param status   {@link LocationProvider#OUT_OF_SERVICE} if the
         *                 provider is out of service, and this is not expected to change in the
         *                 near future; {@link LocationProvider#TEMPORARILY_UNAVAILABLE} if
         *                 the provider is temporarily unavailable but is expected to be available
         *                 shortly; and {@link LocationProvider#AVAILABLE} if the
         *                 provider is currently available.
         * @param extras   an optional Bundle which will contain provider specific
         *                 status variables.
         *                 <p/>
         *                 <p> A number of common key/value pairs for the extras Bundle are listed
         *                 below. Providers that use any of the keys on this list must
         *                 provide the corresponding value as described below.
         *                 <p/>
         *                 <ul>
         *                 <li> satellites - the number of satellites used to derive the fix
         */
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(String.format("onStatusChanged: provider %s", provider));
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
            Log.d("onProviderEnabled: provider "+provider);
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
            Log.d("onProviderDisabled: provider "+provider);

        }
    }


}
