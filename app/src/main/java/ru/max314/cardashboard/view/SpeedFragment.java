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
        return view;

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        modelData = ApplicationModelFactory.getModel().getModelData();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        modelData = null;
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
        frSpeedSpeed.setText(String.format("%.0f", location.getSpeed() * 1000 / 360));
        frSpeedTripAll.setText(String.format(modelData.getTripAllSumator().getTripKM()));
        frSpeedTripOwn.setText(String.format(modelData.getTripOneSumator().getTripSmallKM()));
        frSpeedTripStart.setText(String.format(modelData.getTripStartSumator().getTripSmallKM()));
        frSpeedTripDay.setText(String.format(modelData.getTripTodaySumator().getTripSmallKM()));

    }

}
