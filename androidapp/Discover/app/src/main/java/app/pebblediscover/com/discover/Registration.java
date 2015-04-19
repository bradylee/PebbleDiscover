package app.pebblediscover.com.discover;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tgdiriba on 4/19/15.
 */
public class Registration extends Activity {

    private EditText username_input;
    private ViewGroup foods;
    private ViewGroup stores;
    private ViewGroup venues;
    private ViewGroup public_transport;

    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);

        username_input = (EditText) findViewById(R.id.username_input);
    }

    public void processUsername(View v) {
        try
        {
            JSONObject jobj = new JSONObject();

            String username = username_input.getText().toString();

            jobj.put("username", username);
            HashMap<String, ArrayList<String>> preferences = new HashMap<String, ArrayList<String>>();

            ArrayList<String> food_list = new ArrayList<String>();
            for(int i = 0; i < foods.getChildCount(); i++) {
                food_list.add(((CheckBox)foods.getChildAt(i)).getText().toString());
            }
            preferences.put("food", food_list);

            ArrayList<String> stores_list = new ArrayList<String>();
            for(int i = 0; i < stores.getChildCount(); i++) {
                stores_list.add(((CheckBox)stores.getChildAt(i)).getText().toString());
            }
            preferences.put("stores", stores_list);

            ArrayList<String> venues_list = new ArrayList<String>();
            for(int i = 0; i < venues.getChildCount(); i++) {
                food_list.add(((CheckBox)venues.getChildAt(i)).getText().toString());
            }
            preferences.put("venues", venues_list);

            ArrayList<String> public_transport_list = new ArrayList<String>();
            for(int i = 0; i < public_transport.getChildCount(); i++) {
                food_list.add(((CheckBox)public_transport.getChildAt(i)).getText().toString());
            }
            preferences.put("public_transport", public_transport_list);

            jobj.put("preferences", preferences);

            HttpClient Client = new DefaultHttpClient();
            String URL = "http://104.236.213.197/register";

            // Create Request to server and get response
            HttpPost httppost = new HttpPost(URL);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            httppost.setEntity(new ByteArrayEntity(jobj.toString().getBytes("UTF8")));
            String httpResponse = Client.execute(httppost, responseHandler);

            Intent i = new Intent(this, PebbleDiscover.class);
            i.putExtra("username", username);
            startActivity(i);
        }
        catch(Exception ex)
        {
            // Could not proceed with request
        }
    }


}