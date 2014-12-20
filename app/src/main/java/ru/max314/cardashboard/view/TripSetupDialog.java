package ru.max314.cardashboard.view;


import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.text.NumberFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import ru.max314.cardashboard.R;
import ru.max314.cardashboard.model.AppicationModel;
import ru.max314.cardashboard.model.ApplicationModelFactory;

/**
 * A fragment with a Google +1 button.
 */
public class TripSetupDialog extends DialogFragment {

    private AppicationModel model;
    @InjectView(R.id.frTripSetupAll)
    EditText frTripSetupAll;
    @InjectView(R.id.frTripSetupTrip1)
    TextView frTripSetupTrip1;


    public TripSetupDialog() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        model = ApplicationModelFactory.getModel();
        // Inflate the layout for this fragment
        getDialog().setTitle("Пробег");
        View view = inflater.inflate(R.layout.fragment_trip_setup, container, false);

        ButterKnife.inject(this, view);
        loadData();
        return view;
    }

    @OnClick(R.id.frTripSetupSave)
    public void saveClick(View view) {
        saveData();
        dismiss();
    }

    private void saveData() {
        Double buff;
        buff = Double.parseDouble(frTripSetupAll.getText().toString())*1000;
        model.getModelData().getTripAllSumator().setStartOffset(buff);
        buff = Double.parseDouble(frTripSetupTrip1.getText().toString())*1000;
        model.getModelData().getTripOneSumator().setStartOffset(buff);
    }

    @OnClick(R.id.frTripSetupResetOwn)
    public void resetOwnClick(View view) {
        frTripSetupTrip1.setText("0");
    }

    private void loadData(){
        frTripSetupAll.setText(model.getModelData().getTripAllSumator().getTripKM());
        frTripSetupTrip1.setText(model.getModelData().getTripOneSumator().getTripKM());
    }
}
