package ru.max314.cardashboard.view;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ru.max314.cardashboard.R;
import ru.max314.util.threads.TimerUIHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClockFragment extends Fragment {

    @InjectView(R.id.frClockDate)
    TextView frClockDate;
    @InjectView(R.id.frClockTime)
    TextView frClockTime;

    TimerUIHelper timerUIHelper;



    public ClockFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clock, container, false);
        ButterKnife.inject(this, view);
//        //upDate();
//        new TimerHelper("DateTime виджет",1000*30,new Runnable() {
//            @Override
//            public void run() {
//                upDate();
//            }
//        }).start();
        return view;
    }

    private void upDate(){

        Date date = new Date();
        SimpleDateFormat sf;
        sf = new SimpleDateFormat("EEEE\ndd LLLL yyyy");
        String buff;
        buff = sf.format(date);
        frClockDate.setText(buff);
        sf = new SimpleDateFormat("HH:mm");
        buff = sf.format(date);
        frClockTime.setText(buff);
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
