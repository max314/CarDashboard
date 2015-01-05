package ru.max314.cardashboard.view;


import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import org.mapsforge.core.model.LatLong;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.DirectedLocationOverlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.IOrientationConsumer;
import org.osmdroid.views.overlay.compass.IOrientationProvider;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;

import ru.max314.cardashboard.R;
import ru.max314.cardashboard.model.ApplicationModelFactory;
import ru.max314.cardashboard.model.ModelData;
import ru.max314.util.LogHelper;
import ru.max314.util.threads.TimerUIHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class OSMapFragment extends Fragment implements IBackgroudMapFrame {
    protected static LogHelper Log = new LogHelper(OSMapFragment.class);


    public OSMapFragment() {
        // Required empty public constructor
    }

    MapView map;
    private ModelData modelData; // model
    private boolean mapBussy = true; // map ready ?
    TimerUIHelper timerUIHelper; // auto update data from model
    private  DirectedLocationOverlay myLocationOverlay;
    private  myOrient myOrient = new myOrient();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_osmap, container, false);

        map = (MapView) view.findViewById(R.id.frOSMapView);
        //map.setTileSource(TileSourceFactory.MAPNIK);
        //map.setTileSource(TileSourceFactory.CYCLEMAP);
        map.setTileSource(TileSourceFactory.MAPQUESTOSM);

        // setup mylocation overlay
        myLocationOverlay = new DirectedLocationOverlay(this.getActivity());
        map.getOverlays().add(myLocationOverlay);
        // Scale bar
        ScaleBarOverlay myScaleBarOverlay = new ScaleBarOverlay(this.getActivity());
        myScaleBarOverlay.setScaleBarOffset(myScaleBarOverlay.screenWidth-130,myScaleBarOverlay.screenHeight-30);
        map.getOverlays().add(myScaleBarOverlay);
        // compas

        CompassOverlay compassOverlay = new CompassOverlay(this.getActivity(),myOrient , map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);


        modelData = ApplicationModelFactory.getModel().getModelData();
//        map.setBuiltInZoomControls(true);
//        map.setMultiTouchControls(true);
        return view;
    }

    private final String PREF_ZOOM = "OSM_ZOOM";
    private final String PREF_LAN = "OSM_LAN";
    private final String PREF_LON = "OSM_LON";

    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally
     * Activity's lifecycle.
     */
    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences pref = getActivity().getPreferences(getActivity().MODE_PRIVATE);
        this.map.getController().setZoom(pref.getInt(PREF_ZOOM, 12));
        GeoPoint center = new GeoPoint(
                pref.getFloat(PREF_LAN, (float) modelData.getDefaultLocation().getLatitude()),
                pref.getFloat(PREF_LON, (float) modelData.getDefaultLocation().getLongitude())
        );
        this.map.getController().setCenter(center);

        if (timerUIHelper != null) {
            timerUIHelper.cancel();
        }
        timerUIHelper = new TimerUIHelper(500, new Runnable() {
            @Override
            public void run() {
                updateData();
            }
        });
    }

    private void updateData() {

        IMapController mapController = map.getController();
        Location location = modelData.getCurrentLocation();
        if (location == null)
            return;
        myLocationOverlay.setEnabled(true);

        GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        myLocationOverlay.setEnabled(true);
        mapController.animateTo(startPoint);
        myLocationOverlay.setLocation(startPoint);
        myLocationOverlay.setAccuracy((int) location.getAccuracy());
        myLocationOverlay.setBearing(location.getBearing());
        map.setMapOrientation(-location.getBearing());
        myOrient.setOrientation(location.getBearing());
        map.invalidate();
    }

    @Override
    public void onPause() {

        super.onPause();

        SharedPreferences pref = getActivity().getPreferences(getActivity().MODE_PRIVATE);
        SharedPreferences.Editor ed = pref.edit();
        ed.putInt(PREF_ZOOM,map.getZoomLevel());
        IGeoPoint center  = this.map.getMapCenter();
        ed.putFloat(PREF_LAN, (float) center.getLatitude());
        ed.putFloat(PREF_LON, (float) center.getLongitude());
        ed.commit();

        if (timerUIHelper != null) {
            timerUIHelper.cancel();
            timerUIHelper = null;
        }
    }


    /**
     * Приблизить карту
     */
    @Override
    public void ZoomIn() {
        IMapController mapController = map.getController();
        mapController.zoomIn();
    }

    /**
     * Отдалить карту
     */
    @Override
    public void ZoomOut() {
        IMapController mapController = map.getController();
        mapController.zoomOut();
    }

private class myOrient implements IOrientationProvider{

    private float orientation;
    private IOrientationConsumer mOrientationConsumer;

    @Override
    public boolean startOrientationProvider(IOrientationConsumer orientationConsumer) {
        mOrientationConsumer = orientationConsumer;
        return true;
    }

    @Override
    public void stopOrientationProvider() {
        mOrientationConsumer = null;
    }

    @Override
    public float getLastKnownOrientation() {
        return orientation;
    }

    public void setOrientation(float orientation) {
        this.orientation = orientation;
        mOrientationConsumer.onOrientationChanged(orientation,this);
    }
}

}
