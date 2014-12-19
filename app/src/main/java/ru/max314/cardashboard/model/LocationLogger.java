package ru.max314.cardashboard.model;

import android.location.Location;
import android.util.TimeUtils;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import ru.max314.cardashboard.App;

/**
 * Created by max on 17.12.2014.
 */
public class LocationLogger extends EntityLogger<Location> {

    @Override
    protected String getFileName() {
        Format formatter = new SimpleDateFormat("yyyy-MM-dd-HH-00");
        String fileName = String.format("%s-location.log",  formatter.format(new Date()));
        String fullFileName = new File(App.getInstance().getFilesDir().getPath(), fileName).getPath();
        return fullFileName;
    }

    @Override
    protected IEntityLogFormater<Location> getFormater() {
        return new IEntityLogFormater<Location>() {
            @Override
            public String getHeader() {
                return "Date;Lat;Long;Accuracy;Altitude;Speed;Bearing";
            }

            @Override
            public String getLine(Location entity) {
                StringBuilder s = new StringBuilder();

                //region date
                Format format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                Date date = new Date(entity.getTime());
                String formatted = format.format(date);
                s.append(formatted+";");
                //endregion
                s.append(String.format("%.6f;%.6f;", entity.getLatitude(), entity.getLongitude()));
                s.append(String.format("%.0f;", entity.getAccuracy()));
                s.append(entity.getAltitude()+";");
                s.append(entity.getSpeed() + ";");
                s.append(entity.getBearing());
                String buff = s.toString();
                return buff;

            }
        };
    }

}
