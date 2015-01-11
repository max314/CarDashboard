package ru.max314.cardashboard.view;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.max314.cardashboard.R;
import ru.max314.music.UniversalMusicOperation;

/**
 * A simple {@link Fragment} subclass.
 */
public class MusicUNIFragment extends Fragment {


    public MusicUNIFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_uni, container, false);
        ButterKnife.inject(this, view);
        // Inflate the layout for this fragment
        return view;
    }

    @OnClick(R.id.btPause)
    public void pauseClick(View view) {
        new UniversalMusicOperation(true).pause();
    }
    @OnClick(R.id.btNext)
    public void nextClick(View view) {
        new UniversalMusicOperation(true).nextTrack();
    }

    @OnClick(R.id.btStart)
    public void startClick(View view) {
        String a_package = "com.maxmpz.audioplaer";
        Intent LaunchIntent = this.getActivity().getPackageManager().getLaunchIntentForPackage(a_package);
        startActivity(LaunchIntent);
    }
}
