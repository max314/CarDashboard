package ru.max314.cardashboard;

import android.app.Application;

import ru.max314.cardashboard.model.ApplicationModelFactory;
import ru.max314.cardashboard.service.BootCarService;
import ru.max314.util.LogHelper;

/**
 * Created by max on 15.12.2014.
 */
public class App extends Application {
    LogHelper Log = new LogHelper(App.class);
    static App self;

    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects (excluding content providers) have been created.
     * Implementations should be as quick as possible (for example using
     * lazy initialization of state) since the time spent in this function
     * directly impacts the performance of starting the first activity,
     * service, or receiver in a process.
     * If you override this method, be sure to call super.onCreate().
     */
    @Override
    public void onCreate() {
        super.onCreate();
        self = this;
        Log.d("App onCreate start");
        BootCarService.start(this);
        Log.d("After BootCarService.start(this);");
        Runtime.getRuntime().addShutdownHook(new Thread());
    }


    public static App getInstance(){
        return self;
    }

    @Override
    public void onTerminate() {
        ApplicationModelFactory.getModel().saveAll();
        super.onTerminate();
    }
}
