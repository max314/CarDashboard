package ru.max314.cardashboard.view;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Canvas;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Point;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.overlay.Circle;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.model.MapViewPosition;

import ru.max314.util.LocationUtils;

/**
 * Created by max on 25.12.2014.
 */
public class MyLocationOverlayOSMSF extends Layer {
    private static final GraphicFactory GRAPHIC_FACTORY = AndroidGraphicFactory.INSTANCE;

    /**
     * @param location
     *            the location whose geographical coordinates should be converted.
     * @return a new LatLong with the geographical coordinates taken from the given location.
     */
    public static LatLong locationToLatLong(Location location) {
        return new LatLong(location.getLatitude(), location.getLongitude(), true);
    }

    private static Paint getDefaultCircleFill() {
        return getPaint(GRAPHIC_FACTORY.createColor(48, 0, 0, 255), 0, Style.FILL);
    }

    private static Paint getDefaultCircleStroke() {
        return getPaint(GRAPHIC_FACTORY.createColor(160, 0, 0, 255), 2, Style.STROKE);
    }

    private static Paint getPaint(int color, int strokeWidth, Style style) {
        Paint paint = GRAPHIC_FACTORY.createPaint();
        paint.setColor(color);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(style);
        return paint;
    }

    private boolean centerAtNextFix;
    private final Circle circle;
    private Location lastLocation;
    private final LocationManager locationManager;
    private final MapViewPosition mapViewPosition;
    private final Marker marker;
    private boolean myLocationEnabled;
    private boolean snapToLocationEnabled;

    /**
     * Constructs a new {@code MyLocationOverlay} with the default circle paints.
     *
     * @param context
     *            a reference to the application context.
     * @param mapViewPosition
     *            the {@code MapViewPosition} whose location will be updated.
     * @param bitmap
     *            a bitmap to display at the current location (might be null).
     */
    public MyLocationOverlayOSMSF(Context context, MapViewPosition mapViewPosition, Bitmap bitmap) {
        this(context, mapViewPosition, bitmap, getDefaultCircleFill(), getDefaultCircleStroke());
    }

    /**
     * Constructs a new {@code MyLocationOverlay} with the given circle paints.
     *
     * @param context
     *            a reference to the application context.
     * @param mapViewPosition
     *            the {@code MapViewPosition} whose location will be updated.
     * @param bitmap
     *            a bitmap to display at the current location (might be null).
     * @param circleFill
     *            the {@code Paint} used to fill the circle that represents the accuracy of the current location (might be null).
     * @param circleStroke
     *            the {@code Paint} used to stroke the circle that represents the accuracy of the current location (might be null).
     */
    public MyLocationOverlayOSMSF(Context context, MapViewPosition mapViewPosition, Bitmap bitmap, Paint circleFill,
                             Paint circleStroke) {
        super();

        this.mapViewPosition = mapViewPosition;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.marker = new Marker(null, bitmap, 0, 0);
        this.circle = new Circle(null, 0, circleFill, circleStroke);
    }

    /**
     * Stops the receiving of location updates. Has no effect if location updates are already disabled.
     */
    public synchronized void disableMyLocation() {
        if (this.myLocationEnabled) {
            this.myLocationEnabled = false;
            // TODO trigger redraw?
        }
    }

    @Override
    public void draw(BoundingBox boundingBox, byte zoomLevel, Canvas canvas, Point topLeftPoint) {
        if (this.marker.getPosition()==null) {
            return;
        }
        this.marker.setDisplayModel(this.displayModel);
        this.circle.setDisplayModel(this.displayModel);
        this.circle.draw(boundingBox, zoomLevel, canvas, topLeftPoint);
        this.marker.draw(boundingBox, zoomLevel, canvas, topLeftPoint);
    }

    /**
     * Enables the receiving of location updates from the most accurate {@link android.location.LocationProvider} available.
     *
     * @param centerAtFirstFix
     *            whether the map should be centered to the first received location fix.
     * @return true if at least one location provider was found, false otherwise.
     */
    public synchronized boolean enableMyLocation(boolean centerAtFirstFix) {
        this.centerAtNextFix = centerAtFirstFix;
        this.circle.setDisplayModel(this.displayModel);
        this.marker.setDisplayModel(this.displayModel);
        return true;
    }

    /**
     * @return the most-recently received location fix (might be null).
     */
    public synchronized Location getLastLocation() {
        return this.lastLocation;
    }

    /**
     * @return true if the map will be centered at the next received location fix, false otherwise.
     */
    public synchronized boolean isCenterAtNextFix() {
        return this.centerAtNextFix;
    }

    /**
     * @return true if the receiving of location updates is currently enabled, false otherwise.
     */
    public synchronized boolean isMyLocationEnabled() {
        return this.myLocationEnabled;
    }

    /**
     * @return true if the snap-to-location mode is enabled, false otherwise.
     */
    public synchronized boolean isSnapToLocationEnabled() {
        return this.snapToLocationEnabled;
    }

    @Override
    public void onDestroy() {
        this.marker.onDestroy();
    }


    public void onLocationChanged(Location location) {

        synchronized (this) {
            if (LocationUtils.isEq(location,lastLocation))
                return;
            this.lastLocation = location;

            LatLong latLong = locationToLatLong(location);
            this.marker.setLatLong(latLong);
            this.circle.setLatLong(latLong);
            if (location.getAccuracy() != 0) {
                this.circle.setRadius(location.getAccuracy());
            } else {
                // on the emulator we do not get an accuracy
                this.circle.setRadius(40);
            }

            if (this.centerAtNextFix || this.snapToLocationEnabled) {
                this.centerAtNextFix = false;
                this.mapViewPosition.setCenter(latLong);

            }

            requestRedraw();
        }
    }

    /**
     * @param snapToLocationEnabled
     *            whether the map should be centered at each received location fix.
     */
    public synchronized void setSnapToLocationEnabled(boolean snapToLocationEnabled) {
        this.snapToLocationEnabled = snapToLocationEnabled;
    }

}

