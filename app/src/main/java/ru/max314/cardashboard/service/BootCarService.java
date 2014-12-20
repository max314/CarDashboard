package ru.max314.cardashboard.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import ru.max314.cardashboard.model.ApplicationModelFactory;
import ru.max314.util.LogHelper;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class BootCarService extends IntentService {
    public static final String ACTION_START = "ru.max314.cardashboard.START";
    LogHelper Log = new LogHelper(BootCarService.class);

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void start(Context context) {
        Intent intent = new Intent(context, BootCarService.class);
        intent.setAction(ACTION_START);
        context.startService(intent);
    }


    public BootCarService() {
        super("BootCarService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                onStartup();
            }
//            else if (ACTION_BAZ.equals(action)) {
//                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
//                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
//                handleActionBaz(param1, param2);
//            }
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void onStartup() {
        ApplicationModelFactory.getModel().startLocationService();
    }
}
