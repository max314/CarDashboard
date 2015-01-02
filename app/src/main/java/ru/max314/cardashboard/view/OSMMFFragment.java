package ru.max314.cardashboard.view;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.rendertheme.AssetsRenderTheme;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.model.MapViewPosition;
import org.mapsforge.map.rendertheme.InternalRenderTheme;
import org.mapsforge.map.rendertheme.XmlRenderTheme;
import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.max314.cardashboard.R;
import ru.max314.cardashboard.model.ApplicationModelFactory;
import ru.max314.cardashboard.model.ModelData;
import ru.max314.util.LogHelper;
import ru.max314.util.threads.TimerUIHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class OSMMFFragment extends Fragment implements IBackgroudMapFrame {
    protected static LogHelper Log = new LogHelper(OSMMFFragment.class);


    private MapView mapView;
    private TileCache tileCache;
    private TileRendererLayer tileRendererLayer;
    private ModelData modelData; // model
    TimerUIHelper timerUIHelper;
    MyLocationOverlayOSMSF myLocationOverlay;
    Drawable drawableLocation;
    Bitmap bitmap;

    public OSMMFFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AndroidGraphicFactory.createInstance(this.getActivity().getApplication());
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_osmmf, container, false);
        mapView = (MapView) view.findViewById(R.id.frOSMMFMap);

        modelData = ApplicationModelFactory.getModel().getModelData();


        //this.mapView = new MapView(this);
        //setContentView(this.mapView);

        this.mapView.setClickable(true);
        this.mapView.getMapScaleBar().setVisible(true);
        this.mapView.setBuiltInZoomControls(true);
        this.mapView.getMapZoomControls().setZoomLevelMin((byte) 8);
        this.mapView.getMapZoomControls().setZoomLevelMax((byte) 18);

        // create a tile cache of suitable size
        this.tileCache = AndroidUtil.createTileCache(this.getActivity(), "mapcache",
                mapView.getModel().displayModel.getTileSize(), 1f,
                this.mapView.getModel().frameBufferModel.getOverdrawFactor());

        return view;
    }
    /**
     * Provides the directory of the map file, by default the Android external storage
     * directory (e.g. sdcard).
     * @return
     */
    protected File getMapFileDirectory() {
        return Environment.getExternalStorageDirectory();
    }

    /**
     * Combines map file directory and map file to a map file.
     * This method usually will not need to be changed.
     * @return a map file
     */
    protected File getMapFile() {
        File file = new File(getMapFileDirectory(), "rostov+.map");
        return file;
    }

    @Override
    public void onStart() {
        super.onStart();

//        this.mapView.getModel().mapViewPosition.setCenter(new LatLong(47.1673097, 38.10058594));
//        this.mapView.getModel().mapViewPosition.setZoomLevel((byte) 12);

        // tile renderer layer using internal render theme
        this.tileRendererLayer = new TileRendererLayer(tileCache,
                this.mapView.getModel().mapViewPosition, false, true, AndroidGraphicFactory.INSTANCE);
        tileRendererLayer.setMapFile(getMapFile());
        tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);

        // only once a layer is associated with a mapView the rendering starts
        this.mapView.getLayerManager().getLayers().add(tileRendererLayer);

        drawableLocation = getResources().getDrawable(R.drawable.ymk_user_location_gps);

        bitmap = AndroidGraphicFactory.convertToBitmap(drawableLocation);
        myLocationOverlay = new MyLocationOverlayOSMSF(this.getActivity(),this.mapView.getModel().mapViewPosition,bitmap);
        myLocationOverlay.setSnapToLocationEnabled(true);
        myLocationOverlay.setVisible(true);
        myLocationOverlay.enableMyLocation(true);

        this.mapView.getLayerManager().getLayers().add(myLocationOverlay);
    }

    private final String PREF_ZOOM = "OSMMF_ZOOM";
    private final String PREF_LAN = "OSMMF_LAN";
    private final String PREF_LON = "OSMMF_LON";

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences pref = getActivity().getPreferences(getActivity().MODE_PRIVATE);
        this.mapView.getModel().mapViewPosition.setZoomLevel((byte) pref.getInt(PREF_ZOOM, 12));
        LatLong center = new LatLong(
                pref.getFloat(PREF_LAN, (float) modelData.getDefaultLocation().getLatitude()),
                pref.getFloat(PREF_LON, (float) modelData.getDefaultLocation().getLongitude())
        );
        this.mapView.getModel().mapViewPosition.setCenter(center);
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


        Location location = modelData.getCurrentLocation();
        if (location == null)
            return;
        myLocationOverlay.onLocationChanged(location);

    }

    @Override
    public void onPause() {
//        modelData.setCurrentOpenStreetZoom(map.getZoomLevel());
        super.onPause();
        SharedPreferences pref = getActivity().getPreferences(getActivity().MODE_PRIVATE);
        SharedPreferences.Editor ed = pref.edit();
        ed.putInt(PREF_ZOOM,mapView.getModel().mapViewPosition.getZoomLevel());
        LatLong center  = this.mapView.getModel().mapViewPosition.getCenter();
        ed.putFloat(PREF_LAN, (float) center.latitude);
        ed.putFloat(PREF_LON, (float) center.longitude);
        ed.commit();

        if (timerUIHelper != null) {
            timerUIHelper.cancel();
            timerUIHelper = null;
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        this.mapView.getLayerManager().getLayers().remove(this.tileRendererLayer);
        this.tileRendererLayer.onDestroy();
    }

    /**
     * Android Activity life cycle method.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.tileCache.destroy();
        this.mapView.getModel().mapViewPosition.destroy();
        this.mapView.destroy();
        org.mapsforge.map.android.graphics.AndroidResourceBitmap.clearResourceBitmaps();
    }

    /**
     * Приблизить карту
     */
    @Override
    public void ZoomIn() {
        mapView.getModel().mapViewPosition.zoomIn();
    }

    /**
     * Отдалить карту
     */
    @Override
    public void ZoomOut() {
        mapView.getModel().mapViewPosition.zoomOut();
    }
}
