package ru.max314.cardashboard.view;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;

import ru.max314.cardashboard.model.ApplicationModelFactory;
import ru.max314.cardashboard.model.ModelData;
import ru.max314.util.LogHelper;
import ru.max314.util.threads.TimerUIHelper;
import ru.yandex.yandexmapkit.*;


import ru.max314.cardashboard.R;
import ru.yandex.yandexmapkit.overlay.Overlay;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.overlay.balloon.BalloonItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class YaMapFragment extends Fragment implements IBackgroudMapFrame {
    protected static LogHelper Log = new LogHelper(OSMapFragment.class);
    public YaMapFragment() {
        // Required empty public constructor

    }
    MapController mapController;
    MapView map;
    Overlay overlayMyLocation;
    Drawable drawableLocation;

    private ModelData modelData; // model
    TimerUIHelper timerUIHelper; // auto update data from model


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        modelData = ApplicationModelFactory.getModel().getModelData();
        View view = inflater.inflate(R.layout.fragment_ya_map, container, false);

        // Inflate the layout for this fragment
        map = (MapView) view.findViewById(R.id.yamap);
//        map.showBuiltInScreenButtons(true);
        mapController = map.getMapController();
        // Disable determining the user's location
        mapController.getOverlayManager().getMyLocation().setEnabled(false);
        overlayMyLocation = new Overlay(mapController);
        mapController.getOverlayManager().addOverlay(overlayMyLocation);
        mapController.getMapRotator().a(true); // разрешаем вращение
        mapController.setJamsVisible(true);

        drawableLocation = getResources().getDrawable(R.drawable.ymk_user_location_gps);
        //mMapController.z
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
        try {
            mapController.setZoomCurrent(modelData.getCurrentYaZoom());
        } catch (Exception e) {
            Log.e("Error set zoom level",e);
        }
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
        overlayMyLocation.clearOverlayItems();
        ru.yandex.yandexmapkit.utils.GeoPoint point = new ru.yandex.yandexmapkit.utils.GeoPoint(location.getLatitude() , location.getLongitude());
        mapController.setPositionNoAnimationTo(point);
        OverlayItem kremlin = new OverlayItem(point, drawableLocation);
        overlayMyLocation.addOverlayItem(kremlin);
        // крутилко честно спиженно http://habrahabr.ru/post/167807/
        //ru.yandex.yandexmapkit.utils.GeoPoint geoPoint = mapController.getMapCenter(); //получает географические координаты центра карты
        OverlayItem overlayItem = new OverlayItem(point, null); // необходимо для получения недоступного экземпляра класса cp,  представляет внутренние координаты центра карты библиотеки
        mapController.getMapRotator().a(location.getBearing()); //задаем угол поворота
        mapController.getMapRotator().a(overlayItem.getPoint()); //задаем точку относительно которой производится вращение

//        map.
//        myLocationOverlay.setEnabled(true);
//
//        GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
//        myLocationOverlay.setEnabled(true);
//        mapController.animateTo(startPoint);
//        myLocationOverlay.setLocation(startPoint);
//        myLocationOverlay.setAccuracy((int)location.getAccuracy());
//        myLocationOverlay.setBearing(location.getBearing());
//        map.setMapOrientation(-location.getBearing());
        //map.invalidate();
    }

    @Override
    public void onPause() {
        modelData.setCurrentYaZoom(mapController.getZoomCurrent());
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
        mapController.zoomIn();

    }

    /**
     * Отдалить карту
     */
    @Override
    public void ZoomOut() {
        mapController.zoomOut();
    }
}
