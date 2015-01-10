package ru.max314.cardashboard.view;


import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import org.mapsforge.core.model.LatLong;
import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
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

    public class MyTileSource extends XYTileSource {

        public MyTileSource(String aName, ResourceProxy.string aResourceId, int aZoomMinLevel,
                            int aZoomMaxLevel, int aTileSizePixels,
                            String aImageFilenameEnding, String... aBaseUrl) {
            super(aName, aResourceId, aZoomMinLevel, aZoomMaxLevel,
                    aTileSizePixels, aImageFilenameEnding, aBaseUrl);

        }
        //переопределим метод getTileURLString, он будет возвращать ссылку на тайл карты
        @Override
        public String getTileURLString(MapTile aTile) {

            return String.format(getBaseUrl(), aTile.getX(), aTile.getY(),
                    aTile.getZoomLevel());
        }

    }

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
        map.setTileSource(TileSourceFactory.MAPNIK);
        //map.setTileSource(TileSourceFactory.CYCLEMAP);
//        map.setTileSource(TileSourceFactory.MAPQUESTOSM);
//            map.setTileSource(new OnlineTileSourceBase("Google Maps", ResourceProxy.string.unknown, 1, 20, 256, ".png",
//                    new String[]{"http://mt3.google.com/vt/v=w2.97"}) {
//            @Override
//            public String getTileURLString(final MapTile aTile) {
/*
* GOOGLE MAPS URL looks like
* base url const x y zoom
* http://mt3.google.com/vt/v=w2.97&x=74327&y=50500&z=17
*/
//                return getBaseUrl() + "&x=" + aTile.getX() + "&y=" + aTile.getY() + "&z=" + aTile.getZoomLevel();
//            }
//        });
//            map.setTileSource(new MyTileSource("Google Maps", ResourceProxy.string.unknown, 1, 20, 256, ".png",
//                    "http://vec04.maps.yandex.net/tiles?l=map&v=4.28.0&x=%s&y=%s&z=%s&lang=ru-RU",
//                    "http://vec03.maps.yandex.net/tiles?l=map&v=4.28.0&x=%s&y=%s&z=%s&lang=ru-RU",
//                    "http://vec02.maps.yandex.net/tiles?l=map&v=4.28.0&x=%s&y=%s&z=%s&lang=ru-RU",
//                    "http://vec01.maps.yandex.net/tiles?l=map&v=4.28.0&x=%s&y=%s&z=%s&lang=ru-RU" ));
//            map.setTileSource(new MyTileSource("Google Maps", ResourceProxy.string.unknown, 1, 20, 256, ".png",
//                    "http://mt0.google.com/vt/lyrs=m&hl=ru&gl=RU&x=%s&y=%s&z=%s&s=Galileo",
//                    "http://mt1.google.com/vt/lyrs=m&hl=ru&gl=RU&x=%s&y=%s&z=%s&s=Galileo",
//                    "http://mt2.google.com/vt/lyrs=m&hl=ru&gl=RU&x=%s&y=%s&z=%s&s=Galileo",
//                    "http://mt3.google.com/vt/lyrs=m&hl=ru&gl=RU&x=%s&y=%s&z=%s&s=Galileo" ));
//        map.setTileSource(new MyTileSource("ya", ResourceProxy.string.unknown, 1, 20, 256, ".png",
//                "http://jgo.maps.yandex.net/1.1/tiles?l=trf%2Ctrfe&x=%s&y=%s&z=%s&tm=1420457338"
//                 ));
//        http://jgo.maps.yandex.net/1.1/tiles?l=trf%2Ctrfe&x=2493&y=1439&z=12&tm=1420457338

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
