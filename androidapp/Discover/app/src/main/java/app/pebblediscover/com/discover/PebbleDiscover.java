package app.pebblediscover.com.discover;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;


public class PebbleDiscover extends Activity {

    private PebbleKit.PebbleDataLogReceiver mReceiver;

    final private double DeviationToggle = 100.0;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location locationTrack;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        locationTrack = null;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        username = getIntent().getStringExtra("username");

        if(username != null) {
            // Define a listener that responds to location updates
            locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider.
                    if (locationTrack == null || locationTrack.distanceTo(location) < DeviationToggle && username != null) {
                        locationTrack = location;

                        try {
                            String encodedUsername = URLEncoder.encode(username, "UTF-8");
                            String encodedLatitude = URLEncoder.encode(Double.toString(location.getLatitude()), "UTF-8");
                            String encodedLongitude = URLEncoder.encode(Double.toString(location.getLongitude()), "UTF-8");

                            ArrayList<Double> inputs = new ArrayList<>();
                            inputs.add(location.getLatitude());
                            inputs.add(location.getLongitude());

                            AsyncTask net_task = new AsyncTask<ArrayList<Double>, Void, JSONObject>() {

                                @Override
                                protected JSONObject doInBackground(ArrayList<Double>... l) {
                                    HttpClient Client = new DefaultHttpClient();
                                    String URL = "http://104.236.213.197?username=" + username + "&latitude=" + l[0] + "&longitude=" + l[1];

                                    // Create Request to server and get response
                                    try {
                                        HttpGet httpget = new HttpGet(URL);
                                        HttpResponse httpResponse = Client.execute(httpget);
                                        String json_string = EntityUtils.toString(httpResponse.getEntity());
                                        return new JSONObject(json_string);
                                    }
                                    catch(Exception e) {
                                        e.printStackTrace();
                                    }

                                    return new JSONObject();
                                }



                            };

                            JSONObject jobj = (JSONObject)net_task.execute(inputs).get();

                            // Send data to the pebble
                            /*PebbleDictionary dict = new PebbleDictionary();
                            dict.fromJson(json_string);
                            PebbleKit.sendDataToPebble(getApplicationContext(), UUID.fromString("5f306ed8-575b-48b2-9a7c-ba7b861b587f"), dict);*/

                            LinearLayout foods_table = (LinearLayout) findViewById(R.id.food_table);
                            LinearLayout stores_table = (LinearLayout) findViewById(R.id.stores_table);
                            LinearLayout venues_table = (LinearLayout) findViewById(R.id.venues_table);
                            LinearLayout public_transport_table = (LinearLayout) findViewById(R.id.public_transport_table);


                            foods_table.removeAllViews();

                            Iterator<?> keys = jobj.keys();
                            while( keys.hasNext() ) {
                                String key = (String)keys.next();
                                if ( jobj.get(key) instanceof JSONObject ) {
                                    JSONObject results = ((JSONObject) jobj.get(key)).optJSONObject("results");
                                    JSONArray types = ((JSONArray)results.get("types"));
                                    String type = (String)types.get(0);

                                    if(type.equals("food")) {
                                        // Check if name exists
                                        String name = ((String)results.get("name"));
                                        if(name != null) {
                                            TextView tv = new TextView(getApplicationContext());
                                            tv.setText("Name: " + name);
                                            foods_table.addView(tv);
                                        }

                                        String price_level = ((String)results.get("price_level"));
                                        if(price_level != null) {
                                            // Check if price level exists
                                            TextView tv = new TextView(getApplicationContext());
                                            tv.setText("Price Level: " + price_level);
                                        }

                                        String rating = ((String)results.get("rating"));
                                        if(rating != null) {
                                            TextView tv = new TextView(getApplicationContext());
                                            tv.setText("Rating: " + rating);
                                            foods_table.addView(tv);
                                        }
                                    }
                                    else if(type.equals("store")) {
                                        String name = ((String)results.get("name"));
                                        if(name != null) {
                                            TextView tv = new TextView(getApplicationContext());
                                            tv.setText("Name: " + name);
                                            foods_table.addView(tv);
                                        }

                                        String price_level = ((String)results.get("price_level"));
                                        if(price_level != null) {
                                            // Check if price level exists
                                            TextView tv = new TextView(getApplicationContext());
                                            tv.setText("Price Level: " + price_level);
                                        }

                                        String rating = ((String)results.get("rating"));
                                        if(rating != null) {
                                            TextView tv = new TextView(getApplicationContext());
                                            tv.setText("Rating: " + rating);
                                            foods_table.addView(tv);
                                        }
                                    }
                                    else if(type.equals("venues")) {
                                        String name = ((String)results.get("name"));
                                        if(name != null) {
                                            TextView tv = new TextView(getApplicationContext());
                                            tv.setText("Name: " + name);
                                            foods_table.addView(tv);
                                        }

                                        String price_level = ((String)results.get("price_level"));
                                        if(price_level != null) {
                                            // Check if price level exists
                                            TextView tv = new TextView(getApplicationContext());
                                            tv.setText("Price Level: " + price_level);
                                        }

                                        String rating = ((String)results.get("rating"));
                                        if(rating != null) {
                                            TextView tv = new TextView(getApplicationContext());
                                            tv.setText("Rating: " + rating);
                                            foods_table.addView(tv);
                                        }
                                    }
                                    else if(type.equals("public_transport")) {
                                        String name = ((String)results.get("name"));
                                        if(name != null) {
                                            TextView tv = new TextView(getApplicationContext());
                                            tv.setText("Name: " + name);
                                            foods_table.addView(tv);
                                        }

                                        String price_level = ((String)results.get("price_level"));
                                        if(price_level != null) {
                                            // Check if price level exists
                                            TextView tv = new TextView(getApplicationContext());
                                            tv.setText("Price Level: " + price_level);
                                        }

                                        String rating = ((String)results.get("rating"));
                                        if(rating != null) {
                                            TextView tv = new TextView(getApplicationContext());
                                            tv.setText("Rating: " + rating);
                                            foods_table.addView(tv);
                                        }
                                    }
                                }
                            }

                        } catch (Exception ex) {
                            StringWriter sw = new StringWriter();
                            PrintWriter pw = new PrintWriter(sw);
                            ex.printStackTrace(pw);
                            sw.toString();
                            Log.d("Location", "Error parsing the location"+sw);
                        }
                    }
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            };

            // Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
        else {
            Log.d("PebbleDiscover", "No username defined.");
        }

        setContentView(R.layout.activity_pebble_discover);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mReceiver = new PebbleKit.PebbleDataLogReceiver(UUID.fromString("2fc99a5d-ee35-4057-aa9b-0d4dd8e35ef5")) {

            public void receiveData(Context context, int transactionId, PebbleDictionary data) {
                //ACK the message
                PebbleKit.sendAckToPebble(context, transactionId);

                // Process
            }

        };

        PebbleKit.registerDataLogReceiver(this, mReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

}
