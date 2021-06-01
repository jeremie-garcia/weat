package com.example.my_weat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
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
import android.view.Display;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.Sun;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;


public class MainActivity<url> extends AppCompatActivity {

    private LocationManager mLocationManagerGPS;
    private LocationListener mLocationListenerGPS;
    private LocationManager mLocationManagerNetwork;
    private LocationListener mLocationListenerNetwork;

    private JSONArray Globalarr;
    private JSONArray displayArray;
    private JSONObject tempObject;

    private String strLatGPS, strLongGPS , strLatNetwork, strLongNetwork;
    private TextView txtViewCloud, txtViewWndSpd, txtViewWndDir, txtViewTempMin, txtViewTempsMax;


    private Scene scene;
    private Point point;
    private CustomArFragment fragment;
    private Camera camera;


    private static final String TAG = "AndroidCameraApi";
    private static final int REQUEST_CAMERA_PERMISSION = 200;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        fragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        scene = fragment.getArSceneView().getScene();
        camera = scene.getCamera();


        //Initialize slider
        Slider slider = findViewById(R.id.discreteSlider);

        //Initialize Textview
        txtViewCloud = findViewById(R.id.cloudstextView);
        txtViewWndSpd = findViewById(R.id.wndSpdView);
        txtViewWndDir = findViewById(R.id.wndDirView);
        txtViewTempMin = findViewById(R.id.tempMinView);
        txtViewTempsMax = findViewById(R.id.tempMaxView);




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
                        String strminTemp = null;
                        String strmaxTemp = null;



                        try {
                            test = Globalarr.getJSONObject(i).getString("wind_speed");
                            strwinddeg = Globalarr.getJSONObject(i).getString("wind_deg");
                            strCloud = Globalarr.getJSONObject(i).getString("clouds");


                            displayArray = Globalarr.getJSONObject(i).getJSONArray("weather");
                            numIcon = displayArray.getJSONObject(0).getString("icon");


                            tempObject = Globalarr.getJSONObject(i).getJSONObject("temp");
                            strminTemp = tempObject.getString("min");
                            strmaxTemp = tempObject.getString("max");

                            txtViewTempMin.setText(strminTemp);
                            txtViewTempsMax.setText(strmaxTemp);
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
    }



    private void getPositionGPS() {
        mLocationManagerGPS = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

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
        mLocationManagerNetwork = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
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
                showAlert(R.string.Network_disabled);
            }
        };

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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



        switch(numIcon)
        {

            case "01d": //ClearSky
                cleanAllnodes();

                ModelRenderable.builder()
                        .setSource(this, Uri.parse("sun.sfb"))
                        .build()
                        .thenAccept(modelRenderable -> {

                            Node node = new Node();
                            node.setRenderable(modelRenderable);
                            // node.setLocalScale(scale);
                            Random random = new Random();
                            float x = 0;
                            float y = 1;
                            float z = 0;

                            Vector3 position = new Vector3( x, y, -4 );
                            Vector3 worldPosition = scene.getCamera().getWorldPosition();

                            node.setWorldPosition(position);
                            node.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 200));
                            scene.addChild(node);
                        });


                break;

            case "02d"://Fewclouds
                cleanAllnodes();

                ModelRenderable.builder()
                        .setSource(this, Uri.parse("sun.sfb"))
                        .build()
                        .thenAccept(modelRenderable -> {


                            Node node = new Node();
                            node.setRenderable(modelRenderable);
                            // node.setLocalScale(scale);
                            Random random = new Random();
                            float x = -1;
                            float y = 1;
                            float z = 0;

                            Vector3 position = new Vector3( x, y, -4 );
                            Vector3 worldPosition = scene.getCamera().getWorldPosition();

                            node.setWorldPosition(position);
                            node.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 200));
                            scene.addChild(node);

                        });

                ModelRenderable.builder()
                        .setSource(this, Uri.parse("cloud_02.sfb"))
                        .build()
                        .thenAccept(modelRenderable -> {


                            Vector3 scale = new Vector3( 1f, 1f,1f);

                            Node node = new Node();
                            node.setRenderable(modelRenderable);
                            node.setLocalScale(scale);
                            Random random = new Random();
                            float x = 1;
                            float y = 1;
                            float z = 4;

                            Vector3 position = new Vector3( x, y, -z );
                            Vector3 worldPosition = scene.getCamera().getWorldPosition();

                            node.setWorldPosition(position);
                            node.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 0));
                            scene.addChild(node);

                        });
                break;


            case "03d": //Scattered cloud
                ModelRenderable.builder()
                        .setSource(this, Uri.parse("cloud_02.sfb"))
                        .build()
                        .thenAccept(modelRenderable -> {


                            Vector3 scale = new Vector3( 1f, 1f,1f);

                            Node node = new Node();
                            node.setRenderable(modelRenderable);
                            node.setLocalScale(scale);
                            Random random = new Random();
                            float x = 0;
                            float y = 1;
                            float z = 4;

                            Vector3 position = new Vector3( x, y, -z );
                            Vector3 worldPosition = scene.getCamera().getWorldPosition();

                            node.setWorldPosition(position);
                            node.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 0));
                            scene.addChild(node);
                        });

                break;

            case "04d": //broken clouds
                cleanAllnodes();
                ModelRenderable.builder()
                        .setSource(this, Uri.parse("cloud_02.sfb"))
                        .build()
                        .thenAccept(modelRenderable -> {


                            Vector3 scale = new Vector3( 1f, 1f,1f);

                            Node node = new Node();
                            node.setRenderable(modelRenderable);
                            node.setLocalScale(scale);
                            Random random = new Random();
                            float x = 0;
                            float y = 1;
                            float z = 4;

                            Vector3 position = new Vector3( x, y, -z );
                            Vector3 worldPosition = scene.getCamera().getWorldPosition();

                            node.setWorldPosition(position);
                            node.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 0));
                            scene.addChild(node);

                        });

                ModelRenderable.builder()
                        .setSource(this, Uri.parse("cloud_02.sfb"))
                        .build()
                        .thenAccept(modelRenderable -> {


                            Vector3 scale = new Vector3( 1f, 1f,1f);

                            Node node = new Node();
                            node.setRenderable(modelRenderable);
                            node.setLocalScale(scale);
                            Random random = new Random();
                            float x = 2;
                            float y = 0;
                            float z = 4;

                            Vector3 position = new Vector3( x, y, -z );
                            Vector3 worldPosition = scene.getCamera().getWorldPosition();

                            node.setWorldPosition(position);
                            node.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 0));
                            scene.addChild(node);

                        });
                break;


            case "09d": //Shower Rain
            case "10d":
                cleanAllnodes();
                ModelRenderable.builder()
                        .setSource(this, Uri.parse("model.sfb"))
                        .build()
                        .thenAccept(modelRenderable -> {


                            Node node = new Node();
                            node.setRenderable(modelRenderable);
                            // node.setLocalScale(scale);
                            Random random = new Random();
                            float x = 0;
                            float y = 0;
                            float z = 0;

                            Vector3 position = new Vector3( x, y, -2 );
                            Vector3 worldPosition = scene.getCamera().getWorldPosition();

                            node.setWorldPosition(position);
                            node.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 200));
                            scene.addChild(node);
                        });
                break;


            case "11d": // Thunderstorm
                cleanAllnodes();

                ModelRenderable.builder()
                        .setSource(this, Uri.parse("lightning_bolt.sfb"))
                        .build()
                        .thenAccept(modelRenderable -> {


                            Node node = new Node();
                            node.setRenderable(modelRenderable);
                            // node.setLocalScale(scale);
                            Random random = new Random();
                            float x = 0;
                            float y = 0;
                            float z = 0;

                            Vector3 position = new Vector3( x, y, -5 );
                            Vector3 worldPosition = scene.getCamera().getWorldPosition();

                            node.setWorldPosition(position);
                            node.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 200));
                            scene.addChild(node);

                        });


                break;

            case "13d"://Snow
                cleanAllnodes();





                break;
            case "50d":

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
        super.onPause();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(MainActivity.this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");

    }


    private void cleanAllnodes(){
        List<Node> children = new ArrayList<>(fragment.getArSceneView().getScene().getChildren());
        for (Node node : children) {
            if (node instanceof AnchorNode) {
                if (((AnchorNode) node).getAnchor() != null) {
                    ((AnchorNode) node).getAnchor().detach();
                }
            }
            if (!(node instanceof Camera) && !(node instanceof Sun)) {
                node.setParent(null);
            }
        }
    }

    private void display3dNode(Uri uri, Vector3 scale, int j) {

        Node node = new Node();

        for(int i=0;i<j;i++){

                Random random = new Random();

                float x = random.nextInt(8) - 4f;
                float y = random.nextInt(2) + 1;
                float z = random.nextInt(6) + 2;

                Vector3 position = new Vector3(x, y, -z - 5f);


                ModelRenderable.builder()
                    .setSource(this, uri)
                    .build()
                    .thenAccept(modelRenderable -> {



                        node.setRenderable(modelRenderable);
                        node.setLocalScale(scale);

                        Vector3 worldPosition = scene.getCamera().getWorldPosition();

                        node.setWorldPosition(position);
                        node.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 2f, 0), 0));
                        scene.addChild(node);

                    });
        }

    }


}