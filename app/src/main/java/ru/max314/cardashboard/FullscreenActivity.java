package ru.max314.cardashboard;

import ru.max314.cardashboard.model.ApplicationModelFactory;
import ru.max314.cardashboard.model.BackgroundEnum;
import ru.max314.cardashboard.model.ModelData;
import ru.max314.cardashboard.util.SystemUiHider;
import ru.max314.cardashboard.view.EmptyFragment;
import ru.max314.cardashboard.view.GMapFragment;
import ru.max314.cardashboard.view.IBackgroudFrame;
import ru.max314.cardashboard.view.IBackgroudMapFrame;
import ru.max314.cardashboard.view.OSMMFFragment;
import ru.max314.cardashboard.view.OSMapFragment;
import ru.max314.cardashboard.view.SpeedFragment;
import ru.max314.cardashboard.view.TripSetupDialog;
import ru.max314.cardashboard.view.YaMapFragment;
import ru.max314.util.LogHelper;
import ru.max314.util.threads.TimerUIHelper;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.actionbarsherlock.app.SherlockActivity;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class FullscreenActivity extends SherlockActivity {
    public static final String START_EMPTY = "ru.max314.FullscreenActivity.empty";
    public static final String START_GMAP = "ru.max314.FullscreenActivity.gmap";
    public static final String START_OSAP = "ru.max314.FullscreenActivity.osmap";
    public static final String START_YAMP = "ru.max314.FullscreenActivity.yamap";
    public static final String START_OSMMF = "ru.max314.FullscreenActivity.osmmf";

    protected static LogHelper Log = new LogHelper(FullscreenActivity.class);

    /**
     * модель
     */
    private ModelData modelData;
    //region ShowHide
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;
    //endregion

    private BackgroudFrameHolder backgroudFrame;
    private View contentView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        modelData = ApplicationModelFactory.getModel().getModelData();

        setContentView(R.layout.activity_fullscreen);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);

        contentView = findViewById(R.id.fullscreen_content);

        final View speedView = findViewById(R.id.speedFragment);
        final View clockView = findViewById(R.id.clockFragment);

        Intent intent = this.getIntent();
        createContentByType(intent);


        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
            // Cached values.
            int mControlsHeight;
            int mShortAnimTime;

            @Override
            @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
            public void onVisibilityChange(boolean visible) {
                Log.d("onVisibilityChange(boolean visible) {: " + visible);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                    // If the ViewPropertyAnimator API is available
                    // (Honeycomb MR2 and later), use it to animate the
                    // in-layout UI controls at the bottom of the
                    // screen.
                    Log.d("Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2: true");
                    if (mControlsHeight == 0) {
                        mControlsHeight = controlsView.getHeight();
                    }
                    if (mShortAnimTime == 0) {
                        mShortAnimTime = getResources().getInteger(
                                android.R.integer.config_shortAnimTime);
                    }
                    controlsView.animate()
                            .translationY(visible ? 0 : mControlsHeight)
                            .setDuration(mShortAnimTime);
                } else {
                    // If the ViewPropertyAnimator APIs aren't
                    // available, simply show or hide the in-layout UI
                    // controls.
                    Log.d("Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2: false");
                    controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);

                }
                speedView.setVisibility(visible ? View.GONE : View.VISIBLE);
                clockView.setVisibility(visible ? View.GONE : View.VISIBLE);
                if (visible && AUTO_HIDE) {
                    Log.d("if (visible && AUTO_HIDE) {");
                    // Schedule a hide().
                    delayedHide(AUTO_HIDE_DELAY_MILLIS);
                }
                Log.d("onVisibilityChange }: " + visible);
            }
        });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });


        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
        getActionBar().addOnMenuVisibilityListener(new ActionBar.OnMenuVisibilityListener() {
            @Override
            public void onMenuVisibilityChanged(boolean isVisible) {
                if (isVisible) {
                    if (AUTO_HIDE) {
                        delayedHide(AUTO_HIDE_DELAY_MILLIS);
                    }
                }
            }
        });
    }



    private void createContentByType(Intent intent) {
        if (START_EMPTY.equals(intent.getAction()))
            createContent(BackgroundEnum.EMPTY);
        else if(START_GMAP.equals(intent.getAction()))
                createContent(BackgroundEnum.GOOGLE_MAP);
        else if(START_OSAP.equals(intent.getAction()))
                createContent(BackgroundEnum.OSM_MAP);
        else if(START_YAMP.equals(intent.getAction()))
                createContent(BackgroundEnum.YA_MAP);
        else if(START_OSMMF.equals(intent.getAction()))
                createContent(BackgroundEnum.OSM_MF_MAP);
        else
            createContent(BackgroundEnum.EMPTY);
    }

    private void createContent(BackgroundEnum backgroundEnum){

        Fragment fragment = null;
        switch (backgroundEnum) {
            case EMPTY:
                fragment = new EmptyFragment();
                break;
            case GOOGLE_MAP:
                fragment = new GMapFragment();
                break;
            case OSM_MAP:
                fragment = new OSMapFragment();
                break;
            case YA_MAP:
                fragment = new YaMapFragment();
                break;
            case OSM_MF_MAP:
                fragment = new OSMMFFragment();
                break;
        }
        if (fragment==null)
            throw new RuntimeException("Error create background content");
        this.backgroudFrame = new BackgroudFrameHolder((IBackgroudFrame) fragment);
        FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
        transaction.add(contentView.getId(),fragment);
        transaction.commitAllowingStateLoss();
    }




    @Override
    public void onDestroy() {
        Log.d("public void onDestroy()");
        ApplicationModelFactory.saveModel();
        super.onDestroy();
    }

//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        Log.d("protected void onPostCreate(Bundle savedInstanceState) {");
//
//        super.onPostCreate(savedInstanceState);
//
//        // Trigger the initial hide() shortly after the activity has been
//        // created, to briefly hint to the user that UI controls
//        // are available.
//        delayedHide(100);
//    }

    @Override
    protected void onResume() {
        super.onResume();
        mSystemUiHider.show();
        delayedHide(100);
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            Log.d("View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {");
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d("Runnable mHideRunnable.run()");
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        Log.d("private void delayedHide(int delayMillis) ");
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public void Plus(View view) {
        if (backgroudFrame.isMap()){
            backgroudFrame.getMap().ZoomIn();
        }
    }

    public void Minus(View view) {
        if (backgroudFrame.isMap()){
            backgroudFrame.getMap().ZoomOut();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        getSupportMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void tripSetupClick(MenuItem item) {
        TripSetupDialog tripSetupDialog = new TripSetupDialog();
        tripSetupDialog.show(getFragmentManager(), "trip");
    }

    private class BackgroudFrameHolder{
        IBackgroudFrame frame;

        private BackgroudFrameHolder(IBackgroudFrame frame) {
            this.frame = frame;
        }

        public IBackgroudFrame getValue(){
            return frame;
        }
        public boolean isMap(){
            return (frame instanceof IBackgroudMapFrame);
        }
        public IBackgroudMapFrame getMap(){
            if (!(frame instanceof IBackgroudMapFrame)){
                throw new RuntimeException("Bakground frame is not map");
            }
            return (IBackgroudMapFrame) frame;
        }
    }
}
