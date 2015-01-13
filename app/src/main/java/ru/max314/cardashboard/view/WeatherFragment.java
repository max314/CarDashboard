package ru.max314.cardashboard.view;


import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ru.max314.cardashboard.R;
import ru.max314.cardashboard.model.ApplicationModelFactory;
import ru.max314.util.LogHelper;
import ru.max314.util.threads.TimerUIHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherFragment extends Fragment {
    protected static LogHelper Log = new LogHelper(WeatherFragment.class);

    @InjectView(R.id.webViewWaether)
    WebView webView;
    TimerUIHelper timerUIHelper;


    public WeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        ButterKnife.inject(this, view);
        webView.setBackgroundColor(Color.TRANSPARENT);
        return view;
    }

    private void upDate(){
        String pattern = "http://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&mode=html&lang=ru";
        Location loc = ApplicationModelFactory.getModel().getModelData().getCurrentLocation();
        if (loc!=null){
            String url = String.format(pattern,loc.getLatitude(),loc.getLongitude());

            try {
                webView.loadUrl(url);
                webView.setBackgroundColor(Color.TRANSPARENT);
            } catch (Exception e) {
                Log.e("error load page:"+url,e);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        timerUIHelper = new TimerUIHelper(1000*30,new Runnable() {
            @Override
            public void run() {
                upDate();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        timerUIHelper.cancel();
        timerUIHelper = null;
    }



}
