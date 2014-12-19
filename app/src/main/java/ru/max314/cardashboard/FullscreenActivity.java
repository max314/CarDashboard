package ru.max314.cardashboard;

import ru.max314.cardashboard.model.AppicationModel;
import ru.max314.cardashboard.model.ApplicationModelFactory;
import ru.max314.cardashboard.model.ModelData;
import ru.max314.cardashboard.util.SystemUiHider;
import ru.max314.util.LogHelper;
import ru.max314.util.TimerUI;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.AvoidXfermode;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class FullscreenActivity extends Activity {
    LogHelper Log = new LogHelper(FullscreenActivity.class);
    private GoogleMap googleMap; // Might be null if Google Play services APK is not available.
    private MapView mapView;
    private ModelData modelData;
    private TimerUI timerUI;
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        modelData = ApplicationModelFactory.getModel().getModelData();

        setContentView(R.layout.activity_fullscreen);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);
        final View speedView = findViewById(R.id.speed);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);

                        }
                        speedView.setVisibility(visible ? View.GONE: View.VISIBLE );
                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
//        mapView.onResume();//needed to get the map to display immediately

        try {
            MapsInitializer.initialize(this);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        timerUI = new TimerUI(500,new Runnable() {
            @Override
            public void run() {
                updateMapPosition();
            }
        });
        googleMap = mapView.getMap();
        setUpMapIfNeeded();

    }

    private void updateMapPosition() {
        Location location = modelData.getCurrentLocation();
        if (location ==null)
            return;
        LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(loc)
                .zoom(modelData.getCurrentZoom())
                .bearing(location.getBearing())
                .tilt(30)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        // Check if we were successful in obtaining the map.
        if (googleMap != null) {
            setUpMap();
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #googleMap} is not null.
     */
    private void setUpMap() {

        googleMap.setTrafficEnabled(true);
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setMyLocationButtonEnabled(false);
        googleMap.setMyLocationEnabled(true);

        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                Log.d("map loaded...");
                timerUI.start();
            }
        });
        GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                Log.d("map lonMyLocationChange...");
                LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                //Marker mMarker = googleMap.addMarker(new MarkerOptions().position(loc));
//                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15.0f));
                if (modelData.getCurrentZoom()>0){
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc,modelData.getCurrentZoom()));
                } else{
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc,modelData.getCurrentZoom()));
                    }
                float min = googleMap.getMinZoomLevel();
                float max = googleMap.getMaxZoomLevel();
            }
        };
//        googleMap.setOnMyLocationChangeListener(myLocationChangeListener);

//        if (modelData.getCurrentZoom()>0){
//            googleMap.animateCamera( CameraUpdateFactory.zoomTo(modelData.getCurrentZoom()));
//
//        }

//        // Отслеживаем зумм карты
//        googleMap.setOnCameraChangeListener( new GoogleMap.OnCameraChangeListener() {
//            @Override
//            public void onCameraChange(CameraPosition cameraPosition) {
//                Log.d("map onCameraChange...");
//                modelData.setCurrentZoom(cameraPosition.zoom);
//            }
//        });

        //   googleMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        modelData.setCurrentZoom(googleMap.getCameraPosition().zoom);
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        timerUI.stop();
        ApplicationModelFactory.saveModel();
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public void Plus(View view) {
        float zoom = googleMap.getCameraPosition().zoom+1;
        googleMap.animateCamera( CameraUpdateFactory.zoomTo(zoom));
        modelData.setCurrentZoom(zoom);
    }

    public void Minus(View view) {
        float zoom = googleMap.getCameraPosition().zoom-1;
        googleMap.animateCamera( CameraUpdateFactory.zoomTo(zoom));
        modelData.setCurrentZoom(zoom);

    }
}
