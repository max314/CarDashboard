package ru.max314.cardashboard.view;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.max314.cardashboard.model.ApplicationModelFactory;
import ru.max314.cardashboard.model.ModelData;
import ru.max314.util.LogHelper;
import ru.max314.util.threads.TimerUIHelper;
import ru.yandex.yandexmapkit.*;


import ru.max314.cardashboard.R;
import ru.yandex.yandexmapkit.map.MapEvent;
import ru.yandex.yandexmapkit.map.OnMapListener;
import ru.yandex.yandexmapkit.overlay.Overlay;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.overlay.balloon.BalloonItem;
import ru.yandex.yandexmapkit.overlay.location.MyLocationOverlay;
import ru.yandex.yandexmapkit.utils.GeoPoint;

/**
 * A simple {@link Fragment} subclass.
 */
public class YaMapFragment extends Fragment implements IBackgroudMapFrame {
    protected static LogHelper Log = new LogHelper(OSMapFragment.class);

    private final String PREF_ZOOM = "YMAP_ZOOM";
    private final String PREF_LAN = "YMAP_LAN";
    private final String PREF_LON = "YMAP_LON";

    public YaMapFragment() {
        // Required empty public constructor

    }
    MapController mapController;
    MapView map;
    Overlay overlayMyLocation;
    Drawable drawableLocation;
    OverlayItem overlayItemCenter;

    private ModelData modelData; // model
    private boolean mapBussy = false;
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
//        public static final int MSG_EMPTY = 0;
//        public static final int MSG_SCROLL_BEGIN = 1;
//        public static final int MSG_SCROLL_MOVE = 2;
//        public static final int MSG_SCROLL_END = 3;
//        public static final int MSG_ZOOM_BEGIN = 4;
//        public static final int MSG_ZOOM_MOVE = 5;
//        public static final int MSG_ZOOM_END = 6;
//        public static final int MSG_SCALE_BEGIN = 7;
//        public static final int MSG_SCALE_MOVE = 8;
//        public static final int MSG_SCALE_END = 9;
//        public static final int MSG_LONG_PRESS = 10;

        mapController.addMapListener(new OnMapListener() {
            @Override
            public void onMapActionEvent(MapEvent mapEvent) {
                switch (mapEvent.getMsg()){
                    case  MapEvent.MSG_SCROLL_BEGIN:
                    case  MapEvent.MSG_ZOOM_BEGIN:
                    case  MapEvent.MSG_SCALE_BEGIN:
                        mapBussy = true;
                        break;
                    case  MapEvent.MSG_SCROLL_END:
                    case  MapEvent.MSG_ZOOM_END:
                    case  MapEvent.MSG_SCALE_END:
                        mapBussy = false;
                        break;
                }
            }
        });

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

            // при первой загрузке востановим данные
            SharedPreferences pref = getActivity().getPreferences(getActivity().MODE_PRIVATE);
            ru.yandex.yandexmapkit.utils.GeoPoint center = new ru.yandex.yandexmapkit.utils.GeoPoint(
                    pref.getFloat(PREF_LAN, (float) modelData.getDefaultLocation().getLatitude()),
                    pref.getFloat(PREF_LON, (float) modelData.getDefaultLocation().getLongitude())
            );
            mapController.setZoomCurrent(pref.getFloat(PREF_ZOOM, 12.0f));
            mapController.setPositionAnimationTo(center);
            overlayItemCenter = new OverlayItem(center, drawableLocation);
            overlayMyLocation.addOverlayItem(overlayItemCenter);

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


    float lastKnowBearing = 0;
    private void updateData() {
        if (mapBussy)
            return;

        Location location = modelData.getCurrentLocation();
        if (location == null)
            return;
        //overlayMyLocation.clearOverlayItems();
        ru.yandex.yandexmapkit.utils.GeoPoint point = new ru.yandex.yandexmapkit.utils.GeoPoint(location.getLatitude() , location.getLongitude());
        mapController.setPositionNoAnimationTo(point);
//        OverlayItem overlayItemCenter = new OverlayItem(point, drawableLocation);
//        overlayMyLocation.addOverlayItem(overlayItemCenter);
        overlayItemCenter.setGeoPoint(point);

        // не нарвиться мне как работает поворот
        if (lastKnowBearing!=location.getBearing()){
            lastKnowBearing = location.getBearing();
            // крутилко честно спиженно http://habrahabr.ru/post/167807/
            //ru.yandex.yandexmapkit.utils.GeoPoint geoPoint = mapController.getMapCenter(); //получает географические координаты центра карты
            OverlayItem overlayItem = new OverlayItem(point, null); // необходимо для получения недоступного экземпляра класса cp,  представляет внутренние координаты центра карты библиотеки
            mapController.getMapRotator().a(location.getBearing()); //задаем угол поворота
            mapController.getMapRotator().a(overlayItem.getPoint()); //задаем точку относительно которой производится вращение
        }

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

        SharedPreferences pref = getActivity().getPreferences(getActivity().MODE_PRIVATE);
        SharedPreferences.Editor ed = pref.edit();
        ed.putFloat(PREF_ZOOM,mapController.getZoomCurrent());
        GeoPoint center  = mapController.getMapCenter();
        ed.putFloat(PREF_LAN, (float) center.getLat());
        ed.putFloat(PREF_LON, (float) center.getLon());
        ed.commit();

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
