package com.example.ultimateapp.app;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class UltimateApp extends ActionBarActivity{

    private LocationManager locationManager = null;
    private LocationListener locationListener = null;

    private Button btnGetLocation = null;
    private Button send = null;
    private Button audio = null;
    private EditText editLocation = null;

    private Button flash = null;
    private Camera cam;
    private int flag=0;

    private Socket client;
    private String msg = "";

    private double[] coord;

    private MediaPlayer mp;

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ultimate_app);

        //to lock screen for always Portrait mode
        setRequestedOrientation(ActivityInfo
                .SCREEN_ORIENTATION_PORTRAIT);

        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        editLocation = (EditText) findViewById(R.id.editTextLocation);

        btnGetLocation = (Button) findViewById(R.id.btnLocation);

        btnGetLocation.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                locationListener = new MyLocationListener();

                locationManager.requestLocationUpdates(LocationManager
                        .GPS_PROVIDER, 2000, 0.2f, locationListener);

            }
        });

        send = (Button) findViewById(R.id.send);

        send.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                SendMessage sendMessageTask = new SendMessage();
                sendMessageTask.execute();
                //startAudio();
                controlFlash();
            }
        });

        coord = new double[2];

        audio = (Button)findViewById(R.id.audio);

        audio.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                startAudio();
            }
        });

        flash = (Button)findViewById(R.id.flash);

        flash.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                controlFlash();
            }
        });

    }

    public void controlFlash(){

        if(msg.equals("F") || msg.equals("")){
            if(flag==0) {
                cam = Camera.open();
                Camera.Parameters p = cam.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                cam.setParameters(p);
                cam.startPreview();
                flag = 1;
            }
            else {
                Camera.Parameters p = cam.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                cam.setParameters(p);
                cam.startPreview();
            }
        } else if(msg.equals("S")){
            cam.stopPreview();
            cam.release();
            flag = 0;
        }

    }

    public void startAudio(){

        mp = new MediaPlayer();

        try {
            mp.reset();
            mp.setDataSource("/storage/sdcard0/Music/"+msg+".wav");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mp.prepare();
            mp.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ultimate_app, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*----------Listener class to get coordinates ------------- */
    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {

            editLocation.setText("");

            Toast.makeText(getBaseContext(), "Location changed : Lat: " +
                            loc.getLatitude() + " Lng: " + loc.getLongitude(),
                    Toast.LENGTH_SHORT
            ).show();

            String latitude = "Latitude: " +loc.getLatitude();
            String longitude = "Longitude: " +loc.getLongitude();

            coord[0] = loc.getLatitude();
            coord[1] = loc.getLongitude();

            String s = latitude+"\n"+longitude;
            editLocation.setText(s);

            send.performClick();

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }

    private class SendMessage extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {

                client = new Socket("192.168.1.106", 4444);

                OutputStream os = client.getOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(os);
                oos.writeObject(coord);

                InputStreamReader inputStreamReader = new InputStreamReader(client.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                msg = bufferedReader.readLine();

                client.close();

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
