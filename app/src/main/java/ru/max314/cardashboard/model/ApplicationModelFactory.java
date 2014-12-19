package ru.max314.cardashboard.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import ru.max314.cardashboard.App;
import ru.max314.cardashboard.model.AppicationModel;
import ru.max314.util.LogHelper;
import ru.max314.util.SysUtils;

/**
 * Created by max on 15.12.2014.
 */
public class ApplicationModelFactory {
    private static LogHelper Log = new LogHelper(ApplicationModelFactory.class);
    private static AppicationModel appicationModel;
    private static final String SETTINGS_FILE_NALE = "model.config";


    static {
        appicationModel = new AppicationModel();
        appicationModel.setModelData(loadModel());
        appicationModel.initAftreCreate();
    }

    public static void saveModel(){
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(appicationModel.getModelData());
            SysUtils.writeStringAsFile(App.getInstance().getBaseContext(),SETTINGS_FILE_NALE,json);
            Log.d("save model success.");
        } catch (Throwable e) {
            Log.e("Error save application config",e);
        }
    }

    public static ModelData loadModel(){
        try {
            String buffer = SysUtils.readFileAsString(App.getInstance().getBaseContext(), SETTINGS_FILE_NALE);
            Gson gson = new Gson();
            ModelData modelData = gson.fromJson(buffer,ModelData.class);
            if (modelData == null){
                Log.d("create empty model success.");
                modelData = new ModelData();
            }
            Log.d("load model success.");
            return modelData;
        } catch (Throwable e) {
                Log.e("Error load application config",e);
            return new ModelData();
        }
    }

    public static AppicationModel getModel() {
        return appicationModel;
    }
}
