package com.rafaelcarvalho.mybucketlist.util;

import android.content.Context;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by Rafael on 21/02/17.
 */

public class PropertiesUtil {

    private Context context;
    private Properties properties;


    public PropertiesUtil(Context context) {
        this.context = context;
        try {
            loadProperties();
        } catch (IOException e) {
            properties = new Properties();
        }
    }


    private void loadProperties() throws IOException {

        properties=new Properties();
        InputStreamReader inputStream = new InputStreamReader(context.getResources().getAssets().open("bucket.properties"));
        properties.load(inputStream);
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
