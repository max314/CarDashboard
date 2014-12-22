package ru.max314.cardashboard.view;


import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_osmap, container, false);

        map = (MapView) view.findViewById(R.id.frOSMapView);
        //map.setTileSource(TileSourceFactory.MAPNIK);
        //map.setTileSource(TileSourceFactory.CYCLEMAP);
        map.setTileSource(TileSourceFactory.MAPQUESTOSM);
        modelData = ApplicationModelFactory.getModel().getModelData();
//        map.setBuiltInZoomControls(true);
//        map.setMultiTouchControls(true);
        return view;
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally
     * Activity's lifecycle.
     */
    @Override
    public void onResume() {
        super.onResume();
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
        GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        mapController.animateTo(startPoint);
        map.setMapOrientation(location.getBearing());


    }

    @Override
    public void onPause() {
        super.onPause();
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



}
