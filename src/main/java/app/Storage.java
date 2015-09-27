package app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import models.Profile;

import java.io.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by jesse on 9/15/15.
 */
public class Storage {

    private static Storage ourInstance = new Storage();

    public static Storage getInstance() {
        return ourInstance;
    }

    private static final String DATA_FILE = "data.json";

    private Gson mJson;
    private List<Profile> mProfiles;

    public List<Profile> getProfiles() {
        return mProfiles;
    }

    private Storage() {
        mJson = new GsonBuilder().setPrettyPrinting().create();

        File f = new File(DATA_FILE);
        if(f.exists() && !f.isDirectory()) {
            try {
                mProfiles = mJson.fromJson(new FileReader(f), new TypeToken<CopyOnWriteArrayList<Profile>>() {
                }.getType());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (mProfiles == null) {
            mProfiles = new CopyOnWriteArrayList<Profile>();
        }
    }

    public void saveProfiles() {
        String json = mJson.toJson(mProfiles);
        File f = new File(DATA_FILE);
        try {
            FileWriter fw = new FileWriter(f);
            fw.write(json);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
