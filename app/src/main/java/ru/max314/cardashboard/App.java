package ru.max314.cardashboard;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import org.acra.ACRA;
import org.acra.ACRAConstants;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import ru.max314.cardashboard.model.ApplicationModelFactory;
import ru.max314.cardashboard.service.BootCarService;
import ru.max314.util.LogHelper;

/**
 * Created by max on 15.12.2014.
 */
@ReportsCrashes(
        formKey="",
        mode = ReportingInteractionMode.TOAST,
        customReportContent = {
                ReportField.USER_CRASH_DATE,
                ReportField.USER_COMMENT,
                ReportField.USER_EMAIL,
                ReportField.ANDROID_VERSION,
                ReportField.PHONE_MODEL,
                ReportField.BRAND,
                ReportField.APP_VERSION_CODE,
                ReportField.APP_VERSION_NAME,
                ReportField.STACK_TRACE,
//                ReportField.APPLICATION_LOG,
                ReportField.LOGCAT },
        mailTo = "max314.an21u@gmail.com",
        forceCloseDialogAfterToast = false, // optional, default false
        resToastText = R.string.crash_toast_text,
        logcatArguments = { "-t", "300", "-v", "long" }
)
public class App extends Application {
    protected static LogHelper Log = new LogHelper(App.class);
    private Thread.UncaughtExceptionHandler androidDefaultUEH;
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
        ACRA.init(this);
        LocalReportSender sender = new LocalReportSender(this);
        ACRA.getErrorReporter().addReportSender(sender);
        self = this;
        Log.d("App onCreate start");
        BootCarService.start(this);
        Log.d("After BootCarService.start(this);");
//        Runtime.getRuntime().addShutdownHook(new Thread());

//        androidDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
//        Thread.setDefaultUncaughtExceptionHandler(handler);

        // Setup handler for uncaught exceptions.
//        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//            @Override
//            public void uncaughtException(Thread thread, Throwable e) {
//                handleUncaughtException(thread, e);
//            }
//        });

    }

    private Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {

        public void uncaughtException(Thread thread, Throwable ex) {
            Log.e("Uncaught exception is: ", ex);
            // log it & phone home.
            androidDefaultUEH.uncaughtException(thread, ex);
        }

    };


    public static App getInstance() {
        return self;
    }

    @Override
    public void onTerminate() {
        ApplicationModelFactory.getModel().saveAll();
        super.onTerminate();
    }
    private class LocalReportSender implements ReportSender {

        private final Map<ReportField, String> mMapping = new HashMap<ReportField, String>();
        private FileWriter crashReport = null;

        public LocalReportSender(Context ctx) {
            // the destination
            File logFile = new File(Environment.getExternalStorageDirectory(), "ru.max314.cardashboard.error.log");

            try {
                crashReport = new FileWriter(logFile, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void send(CrashReportData report) throws ReportSenderException {
            final Map<String, String> finalReport = remap(report);

            try {
                BufferedWriter buf = new BufferedWriter(crashReport);

                Set<Map.Entry<String, String>> set = finalReport.entrySet();
                Iterator<Map.Entry<String, String>> i = set.iterator();

                while (i.hasNext()) {
                    Map.Entry<String, String> me = (Map.Entry<String, String>) i.next();
                    buf.append("[" + me.getKey() + "]=" + me.getValue());
                }

                buf.flush();
                buf.close();
            } catch (IOException e) {
                Log.e("IO ERROR", e);
            }
        }

        private boolean isNull(String aString) {
            return aString == null || ACRAConstants.NULL_VALUE.equals(aString);
        }

        private Map<String, String> remap(Map<ReportField, String> report) {

            ReportField[] fields = ACRA.getConfig().customReportContent();
            if (fields.length == 0) {
                fields = ACRAConstants.DEFAULT_REPORT_FIELDS;
            }

            final Map<String, String> finalReport = new HashMap<String, String>(
                    report.size());
            for (ReportField field : fields) {
                if (mMapping == null || mMapping.get(field) == null) {
                    finalReport.put(field.toString(), report.get(field));
                } else {
                    finalReport.put(mMapping.get(field), report.get(field));
                }
            }
            return finalReport;
        }

    }
//
//    public void handleUncaughtException(Thread thread, Throwable e) {
//        String fullFileName = extractLogToFile(); // code not shown
//
//        e.printStackTrace(); // not all Android versions will print the stack trace automatically
//
////        Intent intent = new Intent ();
////        intent.setAction ("com.mydomain.SEND_LOG"); // see step 5.
////        intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK); // required when starting from Application
////        startActivity (intent);
//
//        System.exit(1); // kill off the crashed app
//    }
//
//    private String extractLogToFile() {
//        PackageManager manager = this.getPackageManager();
//        PackageInfo info = null;
//        try {
//            info = manager.getPackageInfo(this.getPackageName(), 0);
//        } catch (PackageManager.NameNotFoundException e2) {
//        }
//        String model = Build.MODEL;
//        if (!model.startsWith(Build.MANUFACTURER))
//            model = Build.MANUFACTURER + " " + model;
//
//        // Make file name - file must be saved to external storage or it wont be readable by
//        // the email app.
//        String path = Environment.getExternalStorageDirectory() + "/";
//        String fullName = path + "ru.max314.error.log";
//
//        // Extract to file.
//        File file = new File(fullName);
//        InputStreamReader reader = null;
//        FileWriter writer = null;
//        try {
//            // For Android 4.0 and earlier, you will get all app's log output, so filter it to
//            // mostly limit it to your app's output.  In later versions, the filtering isn't needed.
//            String cmd = (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) ?
//                    "logcat -d -v time MyApp:v dalvikvm:v System.err:v *:s" :
//                    "logcat -d -v time";
//
//            // get input stream
//            Process process = Runtime.getRuntime().exec(cmd);
//            reader = new InputStreamReader(process.getInputStream());
//
//            // write output stream
//            writer = new FileWriter(file);
//            writer.write("Android version: " + Build.VERSION.SDK_INT + "\n");
//            writer.write("Device: " + model + "\n");
//            writer.write("App version: " + (info == null ? "(null)" : info.versionCode) + "\n");
//
//            char[] buffer = new char[10000];
//            do {
//                int n = reader.read(buffer, 0, buffer.length);
//                if (n == -1)
//                    break;
//                writer.write(buffer, 0, n);
//            } while (true);
//
//            reader.close();
//            writer.close();
//        } catch (IOException e) {
//            if (writer != null)
//                try {
//                    writer.close();
//                } catch (IOException e1) {
//                }
//            if (reader != null)
//                try {
//                    reader.close();
//                } catch (IOException e1) {
//                }
//
//            // You might want to write a failure message to the log here.
//            return null;
//        }
//
//        return fullName;
//    }
}
