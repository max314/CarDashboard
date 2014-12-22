package ru.max314.cardashboard.view;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ru.max314.cardashboard.R;
import ru.max314.cardashboard.model.ApplicationModelFactory;
import ru.max314.cardashboard.model.ModelData;
import ru.max314.util.threads.TimerUIHelper;

/**
 * fragment fro display speed and trip
 */
public class SpeedFragment extends Fragment {


    public SpeedFragment() {
        // Required empty public constructor
    }

    @InjectView(R.id.frSpeedLatitude)
    TextView frSpeedLatitude;
    @InjectView(R.id.frSpeedLongitude)
    TextView frSpeedLongitude;
    @InjectView(R.id.frSpeedSpeed)
    TextView frSpeedSpeed;
    @InjectView(R.id.frSpeedTripAll)
    TextView frSpeedTripAll;
    @InjectView(R.id.frSpeedTripOwn)
    TextView frSpeedTripOwn;
    @InjectView(R.id.frSpeedTripStart)
    TextView frSpeedTripStart;
    @InjectView(R.id.frSpeedTripDay)
    TextView frSpeedTripDay;

    ModelData modelData;
    TimerUIHelper timerUIHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_speed, container, false);
        ButterKnife.inject(this, view);
        modelData = ApplicationModelFactory.getModel().getModelData();
        return view;

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        modelData = null;
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally
     * tied to {@link android.app.Activity#onResume() Activity.onResume} of the containing
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

    /**
     * Called when the Fragment is no longer resumed.  This is generally
     * tied to {@link android.app.Activity#onPause() Activity.onPause} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onPause() {
        super.onPause();
        if (timerUIHelper != null) {
            timerUIHelper.cancel();
            timerUIHelper = null;
        }

    }

    /**
     * Перечитать данные
     */
    public void updateData() {
        if (modelData == null)
            return;
        Location location = modelData.getCurrentLocation();
        if (location == null) {
            // fake object
            location = new Location("emulate");
            location.setSpeed(0);
            location.setLatitude(0);
            location.setLongitude(0);
        }
        frSpeedLatitude.setText(Location.convert(location.getLatitude(), Location.FORMAT_DEGREES));
        frSpeedLongitude.setText(Location.convert(location.getLongitude(), Location.FORMAT_DEGREES));
        frSpeedSpeed.setText(String.format("%.0f", location.getSpeed() * 3.6));
        frSpeedTripAll.setText(String.format(modelData.getTripAllSumator().getTripKM()));
        frSpeedTripOwn.setText(String.format(modelData.getTripOneSumator().getTripSmallKM()));
        frSpeedTripStart.setText(String.format(modelData.getTripStartSumator().getTripSmallKM()));
        frSpeedTripDay.setText(String.format(modelData.getTripTodaySumator().getTripSmallKM()));

    }

}
