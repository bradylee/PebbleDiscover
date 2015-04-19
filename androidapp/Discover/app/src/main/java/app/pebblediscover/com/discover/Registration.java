package app.pebblediscover.com.discover;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tgdiriba on 4/19/15.
 */
public class Registration extends Activity {

    @Override
    protected void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setContentView(R.layout.activity_registration);
    }

    public void processUsername(View v) {
        try
        {
            JSONObject jobj = new JSONObject();

            EditText usernn_input = (EditText) findViewById(R.id.usernn_input);
            LinearLayout foods = (LinearLayout) findViewById(R.id.food);
            LinearLayout stores = (LinearLayout) findViewById(R.id.stores);
            LinearLayout venues = (LinearLayout) findViewById(R.id.venues);
            LinearLayout public_transport = (LinearLayout) findViewById(R.id.public_transport);

            Log.d("processUsername", "Reading in the username");
            String username = ((EditText)findViewById(R.id.usernn_input)).getText().toString();
            Log.d("processUsername", username);

            Log.d("processUsername", "Storing the username into jobj");
            jobj.put("username", username);
            HashMap<String, ArrayList<String>> preferences = new HashMap<String, ArrayList<String>>();

            Log.d("processUsername", "Storing foods into preferences");
            ArrayList<String> food_list = new ArrayList<String>();
            for(int i = 0; i < foods.getChildCount(); i++) {
                CheckBox tc = ((CheckBox)foods.getChildAt(i));
                if(tc.isChecked()) {
                    food_list.add(tc.getText().toString());
                }
            }
            Log.d("processUsername", "Storing foods into preferences");
            preferences.put("food", food_list);

            Log.d("processUsername", "Storing stores into preferences");
            ArrayList<String> stores_list = new ArrayList<String>();
            for(int i = 0; i < stores.getChildCount(); i++) {
                CheckBox tc = ((CheckBox)stores.getChildAt(i));
                if(tc.isChecked()) {
                    stores_list.add(tc.getText().toString());
                }
            }
            preferences.put("stores", stores_list);

            Log.d("processUsername", "Storing venues into preferences");
            ArrayList<String> venues_list = new ArrayList<String>();
            for(int i = 0; i < venues.getChildCount(); i++) {
                CheckBox tc = ((CheckBox)venues.getChildAt(i));
                if(tc.isChecked()) {
                    venues_list.add(tc.getText().toString());
                }
            }
            preferences.put("venues", venues_list);

            Log.d("processUsername", "Storing public transport into preferences");
            ArrayList<String> public_transport_list = new ArrayList<String>();
            for(int i = 0; i < public_transport.getChildCount(); i++) {
                CheckBox tc = ((CheckBox)public_transport.getChildAt(i));
                if(tc.isChecked()) {
                    public_transport_list.add(tc.getText().toString());
                }
            }
            preferences.put("public_transport", public_transport_list);

            Log.d("processUsername", "Storing preferences into jobj");
            // jobj.put("preferences", preferences);


            Log.d("processUsername", "");
            // Create Request to server and get response

            AsyncTask net_task = new AsyncTask<JSONObject, Void, Void>() {

                @Override
                protected Void doInBackground(JSONObject... v) {
                    Log.d("processUsername", "Starting HttpClient");
                    DefaultHttpClient Client = new DefaultHttpClient();
                    String URL = "http://104.236.213.197/register";

                    HttpPost httppost = new HttpPost(URL);
                    try {
                        StringEntity se = new StringEntity(v.toString());

                        //sets the post request as the resulting string
                        httppost.setEntity(se);
                        //sets a request header so the page receving the request
                        //will know what to do with it
                        httppost.setHeader("Accept", "application/json");
                        httppost.setHeader("Content-type", "application/json");

                        //Handles what is returned from the page
                        ResponseHandler responseHandler = new BasicResponseHandler();
                        Client.execute(httppost, responseHandler);
                        return null;
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

            };

            // Wait until allocation is complete
            try {
                net_task.execute(new ByteArrayEntity(jobj.toString().getBytes("UTF8")));
                net_task.get();
            }
            catch(Exception e) {
                e.printStackTrace();
            }

            Log.d("processUsername", "Calling the intent");
            Intent i = new Intent(this, PebbleDiscover.class);
            i.putExtra("username", username);
            startActivity(i);
        }
        catch(Exception ex)
        {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            sw.toString();
            Log.d("processUsername", "Could not successfully process the username. "+sw);
        }
    }


}