package ru.max314.cardashboard;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import ru.max314.cardashboard.model.ApplicationModelFactory;
import ru.max314.util.DisplayToast;
import ru.max314.util.threads.TimerHelper;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = this.getIntent();
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    public void startFullScreen(View view) {
        Intent intent = new Intent(this, FullscreenActivity.class);
        intent.setAction(FullscreenActivity.START_EMPTY);
        new DisplayToast(App.getInstance(),"Загружаеться карта....",false).run();
        startActivity(intent);
    }

    public void StartTimer(View view) {
        TimerHelper timerHelper = new TimerHelper("foo",0,1*10,new Runnable() {
            @Override
            public void run() {
                Log.d("timer","timer");
            }
        });
        timerHelper.start();
    }

    public void startFullScreenGmap(View view) {
        Intent intent = new Intent(this, FullscreenActivity.class);
        intent.setAction(FullscreenActivity.START_GMAP);
        new DisplayToast(App.getInstance(),"Загружаеться карта....",false).run();
        startActivity(intent);

    }

    public void startFullScreenOSMap(View view) {
        Intent intent = new Intent(this, FullscreenActivity.class);
        intent.setAction(FullscreenActivity.START_OSAP);
        new DisplayToast(App.getInstance(),"Загружаеться карта....",false).run();
        startActivity(intent);
    }

    public void startFullScreenYAMap(View view) {
        Intent intent = new Intent(this, FullscreenActivity.class);
        intent.setAction(FullscreenActivity.START_YAMP);
        new DisplayToast(App.getInstance(),"Загружаеться карта....",false).run();
        startActivity(intent);

    }

    public void startFullScreenOSMMF(View view) {
        Intent intent = new Intent(this, FullscreenActivity.class);
        intent.setAction(FullscreenActivity.START_OSMMF);
        new DisplayToast(App.getInstance(),"Загружаеться карта....",false).run();
        startActivity(intent);
    }
}
