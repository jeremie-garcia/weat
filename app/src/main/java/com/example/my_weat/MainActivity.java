package com.example.my_weat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DateFormatSymbols;

public class MainActivity<url> extends AppCompatActivity {


    private TextView  day1TV, day2TV;


    private TextView txtViewLatGPS;
    private TextView txtViewLongGPS;
    private TextView txtViewAltGPS;

    private TextView txtViewLatNetwork;
    private TextView txtViewLongNetwork;
    private TextView txtViewAltNetwork;

    private LocationManager mLocationManagerGPS;
    private LocationListener mLocationListenerGPS;

    private LocationManager mLocationManagerNetwork;
    private LocationListener mLocationListenerNetwork;




    // url openweatherMap
   // String url = "https://api.openweathermap.org/data/2.5/onecall?lat=43&lon=1&appid=f5bd199759b2f5803c1a5f0d31b5a436&lang=fr&units=metric";






  //  String url = String.format("https://api.openweathermap.org/data/2.5/onecall?lat=%s&lon=%s&appid=%s&lang=fr&units=metric","43", "1", "f5bd199759b2f5803c1a5f0d31b5a436");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        //Initializing our all views.


        txtViewLatGPS = findViewById(R.id.txtViewLatGPS);
        txtViewLongGPS = findViewById(R.id.txtViewLonGPS);
        txtViewAltGPS = findViewById(R.id.txtViewAltGPS);

        txtViewLatNetwork = findViewById(R.id.txtViewLatNetwork);
        txtViewLongNetwork = findViewById(R.id.txtViewLonNetwork);
        txtViewAltNetwork = findViewById(R.id.txtViewAltNetwork);


        day1TV = findViewById(R.id.DAY1txt);
        day2TV = findViewById(R.id.DAY2txt);

        getPositionGPS();
        getPositionNetwork();



        String lat_1 = txtViewLatNetwork.getText().toString();
        String long_1 = txtViewLongNetwork.getText().toString();

        String url = String.format("https://api.openweathermap.org/data/2.5/onecall?lat=%s&lon=%s&appid=%s&lang=fr&units=metric"
                ,"44", "5", "f5bd199759b2f5803c1a5f0d31b5a436");

        



        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        // in below line we are making a json object
        // request and creating a new json object request

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // now we get our response from API in json object format.
                    // in below line we are extracting a string with its key
                    // value from our json object


                    JSONArray arr = response.getJSONArray("daily");
                    String day1 = arr.getJSONObject(0).getString("weather");
                    String day2 = arr.getJSONObject(2).getString("weather");

                    // setting that data to all our views.

                    day1TV.setText(day1);
                    day2TV.setText(day2);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            // this is the error listener method which
            // we will call if we get any error from API.

            @Override
            public void onErrorResponse(VolleyError error) {
                // below line is use to display a toast message along with our error.
                Toast.makeText(MainActivity.this, "Fail to get data..", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonObjectRequest);
    }


    private void getPositionGPS() {
        mLocationManagerGPS = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        mLocationListenerGPS = new LocationListener() {
            public void onLocationChanged(Location location) {
                txtViewLatGPS.setText(Double.toString(location.getLatitude()));
                txtViewLongGPS.setText(Double.toString(location.getLongitude()));
                txtViewAltGPS.setText(Double.toString(location.getAltitude()));
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
                showAlert(R.string.GPS_disabled);
            }
        };

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestLocationPermission();
            } else {
               // btnGPS.setEnabled(false);
                mLocationManagerGPS.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 0, mLocationListenerGPS);
            }
        }
    }


    private void getPositionNetwork() {
        mLocationManagerNetwork = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        mLocationListenerNetwork = new LocationListener() {
            public void onLocationChanged(Location location) {
                txtViewLatNetwork.setText(Double.toString(location.getLatitude()));
                txtViewLongNetwork.setText(Double.toString(location.getLongitude()));
                txtViewAltNetwork.setText(Double.toString(location.getAltitude()));
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
                showAlert(R.string.Network_disabled);
            }
        };

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestLocationPermission();
            } else {
                //btnNetwork.setEnabled(false);
                mLocationManagerNetwork.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5, 0, mLocationListenerNetwork);
            }
        }
    }


    private void showAlert(int messageId) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(messageId).setCancelable(false).setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton(R.string.btn_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void requestLocationPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.GPS_permissions).setCancelable(false).setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            }).show();
        } else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.GPS_permissions).setCancelable(false).setPositiveButton(R.string.btn_watch_permissions, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName())));
                }
            }).setNegativeButton(R.string.btn_close, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).show();
        }
    }






    @Override
    protected void onPause() {
        super.onPause();

        if (mLocationManagerGPS != null) {
            mLocationManagerGPS.removeUpdates(mLocationListenerGPS);
        }


        txtViewLatGPS.setText(null);
        txtViewLongGPS.setText(null);
        txtViewAltGPS.setText(null);

        txtViewLatNetwork.setText(null);
        txtViewLongNetwork.setText(null);
        txtViewAltNetwork.setText(null);


    }

    @Override
    protected void onResume() {
        super.onResume();



    }




}