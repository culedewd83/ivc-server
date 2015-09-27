package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by jesse on 9/24/15.
 */
public class JsonHelper {
    private static JsonHelper ourInstance = new JsonHelper();

    public static JsonHelper getInstance() {
        return ourInstance;
    }

    public Gson json;

    private JsonHelper() {
        json = new GsonBuilder().setPrettyPrinting().create();
    }
}
