package com.example.myapplication.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ImageReader;
import android.net.MailTo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;


import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;


import android.os.Bundle;
import android.view.View;
import android.view.Menu;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;



public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    
    private LocationManager mLocationManagerGPS;
    private LocationListener mLocationListenerGPS;
    private LocationManager mLocationManagerNetwork;
    private LocationListener mLocationListenerNetwork;

    private JSONArray Globalarr;
    private JSONArray displayArray;

    private String strLatGPS, strLongGPS , strLatNetwork, strLongNetwork;
    private TextView txtViewCloud, txtViewWndSpd, txtViewWndDir;

    private ImageView imageViewWeather;

    private static final String TAG = "AndroidCameraApi";

    private TextureView textureView;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();



    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private String cameraId;
    public CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest captureRequest;
    protected CaptureRequest.Builder captureRequestBuilder;

    private Size imageDimension;
    private ImageReader imageReader;
    private File file;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private boolean mFlashSupported;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final TextView textView = root.findViewById(R.id.text_home);

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });






        ///////



        //Initialize slider
        Slider slider = root.findViewById(R.id.discreteSlider);

        txtViewCloud = root.findViewById(R.id.cloudstextView);
        txtViewWndSpd = root.findViewById(R.id.wndSpdView);
        txtViewWndDir = root.findViewById(R.id.wndDirView);

        imageViewWeather = (ImageView) root.findViewById(R.id.displayWeather);



        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                for (int i =0;i<8;i++) {
                    float j = (float) i;

                    if (value == j) {
                        String test = null;
                        String strwinddeg = null;
                        String strCloud = null;

                        String numIcon = null;

                        try {
                            test = Globalarr.getJSONObject(i).getString("wind_speed");
                            strwinddeg = Globalarr.getJSONObject(i).getString("wind_deg");
                            strCloud = Globalarr.getJSONObject(i).getString("clouds");


                            displayArray = Globalarr.getJSONObject(i).getJSONArray("weather");
                            numIcon = displayArray.getJSONObject(0).getString("icon");


                            txtViewWndSpd.setText(test);
                            txtViewWndDir.setText(strwinddeg);
                            txtViewCloud.setText(strCloud);


                            displayWheather(numIcon);

                        } catch (NullPointerException | JSONException e) {
                            Log.d("SLIDER", "ERREUR");
                        }
                    }
                }
            }
        });




        slider.setLabelFormatter(new LabelFormatter() {
            @NonNull
            @Override
            public String getFormattedValue(float value) {


                //It is just an example
                if (value == 0.0f)
                    return "Today";
                if (value == 1.0f)
                    return "Tomorrow";

                for (int i =2;i<8;i++) {


                    float j = (float) i;

                    if (value == j) {

                        String test = null;
                        try {
                            test = Globalarr.getJSONObject(i).getString("dt");
                        } catch (NullPointerException | JSONException e) {
                            Log.d("SLIDER", "ERREUR");
                        }



                        long value1 = Long.parseLong(test);
                        java.util.Date time = new java.util.Date((long) value1 * 1000);
                        String day = new SimpleDateFormat("EEEE dd", Locale.US).format(time);
                        return (day);
                    }


                }



                return ("ERROR");
            }
        });



        getPositionGPS();
        getPositionNetwork();




        textureView = (TextureView) root.findViewById(R.id.texture);
        assert textureView != null;
        textureView.setSurfaceTextureListener(textureListener);










        return root;
    }













    private void getPositionGPS() {
        mLocationManagerGPS = (LocationManager) MainActivity.getSystemService(Context.LOCATION_SERVICE);

        mLocationListenerGPS = new LocationListener() {
            public void onLocationChanged(Location location) {
                strLatGPS = Double.toString(location.getLatitude());
                strLongGPS = Double.toString(location.getLongitude());

                getWETHEAR(strLatGPS, strLongGPS);
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
                mLocationManagerGPS.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, mLocationListenerGPS);
            }
        }
    }



    private void getPositionNetwork() {
        mLocationManagerNetwork = (LocationManager) MainActivity.getSystemService(Context.LOCATION_SERVICE);
        mLocationListenerNetwork = new LocationListener() {
            public void onLocationChanged(Location location) {

                strLatNetwork = Double.toString(location.getLatitude());
                strLongNetwork = Double.toString(location.getLongitude());

                getWETHEAR(strLatNetwork, strLongNetwork);


            }
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
            public void onProviderEnabled(String provider) {
            }
            public void onProviderDisabled(String provider) {
                //showAlert(R.string.Network_disabled);
            }
        };

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestLocationPermission();
            } else {
                mLocationManagerNetwork.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 200, mLocationListenerNetwork);
            }
        }
    }



    private void getWETHEAR(String lat_URL, String long_URL) {

        String url = String.format("https://api.openweathermap.org/data/2.5/onecall?lat=%s&lon=%s&exclude=current,minutely,hourly&appid=%s&lang=fr&units=metric"
                ,lat_URL, long_URL, "f5bd199759b2f5803c1a5f0d31b5a436");

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Globalarr = response.getJSONArray("daily");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // below line is use to display a toast message along with our error.
                Toast.makeText(MainActivity.this, "Fail to get data..", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonObjectRequest);
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


    private void displayWheather(String numIcon){

        Log.d("SWITCH", numIcon);
        //imageViewWeather.setImageResource(getDrawable(R.drawable.broken_clouds));


        switch(numIcon)
        {

            case "01d":
                imageViewWeather.setImageResource(R.drawable.soleil);
                break;
            case "02d":
                imageViewWeather.setImageResource(R.drawable.few_clouds);
                break;
            case "03d":
                imageViewWeather.setImageResource(R.drawable.scattered_clouds);
                break;
            case "04d":
                imageViewWeather.setImageResource(R.drawable.broken_clouds);
                break;
            case "09d":
                imageViewWeather.setImageResource(R.drawable.shower_rain);
                break;
            case "10d":
                imageViewWeather.setImageResource(R.drawable.rain);
                break;
            case "11d":
                imageViewWeather.setImageResource(R.drawable.thunderstorm);
                break;
            case "13d":
                imageViewWeather.setImageResource(R.drawable.snow);
                break;
            case "50d":
                imageViewWeather.setImageResource(R.drawable.mist);
                break;

            default:
                Log.d("TEST", "no match");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mLocationManagerGPS != null) {
            mLocationManagerGPS.removeUpdates(mLocationListenerGPS);
        }

        Log.d(TAG, "onPause");

        closeCamera();
        stopBackgroundThread();
        super.onPause();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(MainActivity.this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");
        startBackgroundThread();
        if (textureView.isAvailable()) {
            openCamera();
        } else {
            textureView.setSurfaceTextureListener(textureListener);
        }
    }




}