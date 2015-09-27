package utils;

import models.Key;
import org.jasypt.util.text.BasicTextEncryptor;

import java.io.File;
import java.io.FileReader;

/**
 * Created by jesse on 9/15/15.
 */
public class TextEncryptor {
    private static TextEncryptor ourInstance = new TextEncryptor();

    public static TextEncryptor getInstance() {
        return ourInstance;
    }

    private static final String KEY_FILE = "key.txt";
    private BasicTextEncryptor textEncryptor;
    private String key;

    private TextEncryptor() {

        File f = new File(KEY_FILE);
        if(f.exists() && !f.isDirectory()) {
            try {
                Key k = JsonHelper.getInstance().json.fromJson(new FileReader(f), Key.class);
                key = k.key;
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
        }

        textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(key);
    }

    public static String encrypt (String str) {
        return TextEncryptor.getInstance().textEncryptor.encrypt(str);
    }

    public static String decrypt (String str) {
        return TextEncryptor.getInstance().textEncryptor.decrypt(str);
    }
}
