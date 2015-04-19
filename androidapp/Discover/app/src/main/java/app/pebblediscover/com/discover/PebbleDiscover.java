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
import android.widget.TextView;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.UUID;


public class PebbleDiscover extends Activity {

    private PebbleKit.PebbleDataLogReceiver mReceiver;

    final private double DeviationToggle = 100.0;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location locationTrack;
    private String username;

    class Place {

    }

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

                            HttpClient Client = new DefaultHttpClient();
                            String URL = "http://104.236.213.197?username=" + username + "&latitude=" + location.getLatitude() + "&longitude=" + location.getLongitude();

                            // Create Request to server and get response
                            HttpGet httpget = new HttpGet(URL);
                            HttpResponse httpResponse = Client.execute(httpget);
                            String json_string = EntityUtils.toString(httpResponse.getEntity());
                            JSONObject jobj = new JSONObject(json_string);

                            // Send data to the pebble
                            PebbleDictionary dict = new PebbleDictionary();
                            dict.fromJson(json_string);
                            PebbleKit.sendDataToPebble(getApplicationContext(), UUID.fromString("2fc99a5d-ee35-4057-aa9b-0d4dd8e35ef5"), dict);
                        } catch (Exception ex) {
                            // Could not proceed with request
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
