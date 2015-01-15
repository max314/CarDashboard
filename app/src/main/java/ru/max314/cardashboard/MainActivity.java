package ru.max314.cardashboard;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

import java.util.Locale;

import ru.max314.cardashboard.model.ApplicationModelFactory;
import ru.max314.cardashboard.view.TripSetupDialog;
import ru.max314.util.DisplayToast;
import ru.max314.util.LogHelper;
import ru.max314.util.SpeechUtils;


public class MainActivity extends SherlockActivity {
    protected static LogHelper Log = new LogHelper(FullscreenActivity.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = this.getIntent();
        SpeechUtils.speech("Привет. Мы, приложение car dashboard запустились.",true);
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

    private final int MY_DATA_CHECK_CODE = 100;
    private TextToSpeech textToSpeech;

    public void TestSpeechInstall(View view) {
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
    }
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
                if (textToSpeech == null){
                    textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if (status == TextToSpeech.SUCCESS){
                                textToSpeech.setLanguage(new Locale("ru","RU"));
                                textToSpeech.setSpeechRate(0.8f);
                                textToSpeech.playEarcon("Проверка голосового движка. раз два три",TextToSpeech.QUEUE_FLUSH, null);
                                textToSpeech.speak("Проверка голосового движка. раз два три",TextToSpeech.QUEUE_ADD, null);
                            }
                            else {
                                new DisplayToast(App.getInstance(),"Error init test to speech engine: "+status,false).run();
                            }

                        }
                    });
                }
                else {
                    textToSpeech.speak("Хозяин.",TextToSpeech.QUEUE_FLUSH, null);
                    textToSpeech.playSilence(750, TextToSpeech.QUEUE_ADD, null);
                    textToSpeech.speak("Кто зздеся", TextToSpeech.QUEUE_ADD, null);


                    //textToSpeech.speak("Проверка голосового движка. раз два три",TextToSpeech.QUEUE_ADD, null);
                }
            } else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    }

    public void startFullScreenGmap(View view) {
        Intent intent = new Intent(this, FullscreenActivity.class);
        intent.setAction(FullscreenActivity.START_GMAP);
        new DisplayToast(App.getInstance(),"Загружаеться карта....",false).run();
        SpeechUtils.speech("Запуск гугло карты",true);
        startActivity(intent);

    }

    public void startFullScreenOSMap(View view) {
        Intent intent = new Intent(this, FullscreenActivity.class);
        intent.setAction(FullscreenActivity.START_OSAP);
        new DisplayToast(App.getInstance(),"Загружаеться карта....",false).run();
        SpeechUtils.speech("Запуск open street map карты",true);
        startActivity(intent);
    }

    public void startFullScreenYAMap(View view) {
        Intent intent = new Intent(this, FullscreenActivity.class);
        intent.setAction(FullscreenActivity.START_YAMP);
        new DisplayToast(App.getInstance(),"Загружаеться карта....",false).run();
        SpeechUtils.speech("Запуск Яндекс карты.",true);
        startActivity(intent);

    }

    public void startFullScreenOSMMF(View view) {
        Intent intent = new Intent(this, FullscreenActivity.class);
        intent.setAction(FullscreenActivity.START_OSMMF);
        new DisplayToast(App.getInstance(),"Загружаеться карта....",false).run();
        SpeechUtils.speech("Запуск open street map карты. режим карта офлайн",true);
        startActivity(intent);
    }

    public void TestSpeech(View view) {
        //SpeechUtils.speech("Привет. Мы, приложение car dashboard запустились. Считаем 1 2 3 4",true);
        SpeechUtils.speech("Местоположение GPS. статус. запущенно",true);
    }


    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        getSupportMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuItemSetupTrip:
                tripSetup();
                break;
            case R.id.menuItemClearAGPS:
                clearAGPS();
                break;
            case R.id.menuItemUpdateAGPS:
                updateAGPS();
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    private void updateAGPS() {
        try
        {
            LocationManager locationmanager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Bundle bundle = new Bundle();
            locationmanager.sendExtraCommand("gps", "force_xtra_injection", bundle);
            locationmanager.sendExtraCommand("gps", "force_time_injection", bundle);
            Toast.makeText(this, "AGPS запрос на обновление данных", Toast.LENGTH_LONG).show();
            return;
        }
        catch(Exception exception)
        {
            Log.e("clear AGPS",exception);
        }
    }

    private void clearAGPS() {
        try
        {
            ((LocationManager)this.getSystemService(LOCATION_SERVICE)).sendExtraCommand("gps", "delete_aiding_data", null);
            Toast.makeText(this, "AGPS запрос на сброс данных", Toast.LENGTH_LONG).show();
            return;
        }
        catch(Exception exception)
        {
            Log.e("clear AGPS",exception);
        }
    }

    /**
     * настройка пробегов
     */
    public void tripSetup() {
        TripSetupDialog tripSetupDialog = new TripSetupDialog();
        tripSetupDialog.show(getFragmentManager(), "trip");
    }


    public void testPcture(View view) {
        ApplicationModelFactory.getModel().dumpLocationService();

    }
}
