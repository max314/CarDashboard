package ru.max314.cardashboard.view;


import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maxmpz.poweramp.player.PowerampAPI;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemLongClick;
import butterknife.OnLongClick;
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
        this.getActivity().startService(new Intent(PowerampAPI.ACTION_API_COMMAND).putExtra(PowerampAPI.COMMAND, PowerampAPI.Commands.TOGGLE_PLAY_PAUSE));
    }
    @OnClick(R.id.btNext)
    public void nextClick(View view) {
        this.getActivity().startService(new Intent(PowerampAPI.ACTION_API_COMMAND).putExtra(PowerampAPI.COMMAND, PowerampAPI.Commands.NEXT));
    }

    @OnLongClick(R.id.btNext)
    public boolean nextLongClick(View view) {
        this.getActivity().startService(new Intent(PowerampAPI.ACTION_API_COMMAND).putExtra(PowerampAPI.COMMAND, PowerampAPI.Commands.NEXT_IN_CAT));
        return true;
    }

    @OnClick(R.id.btStart)
    public void startClick(View view) {
        startActivity(
                new Intent()
                        .setComponent(new ComponentName(PowerampAPI.PACKAGE_NAME, PowerampAPI.ACTIVITY_PLAYER_UI))
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP)
        );
//        startActivity(new Intent(PowerampAPI.ACTION_PLASHOW_CURRENT));
//        String a_package = "com.maxmpz.audioplayer";
//        Intent LaunchIntent = this.getActivity().getPackageManager().getLaunchIntentForPackage(a_package);
//        startActivity(LaunchIntent);
    }
}
