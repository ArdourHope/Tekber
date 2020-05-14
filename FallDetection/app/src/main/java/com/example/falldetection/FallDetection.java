package com.example.falldetection;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class FallDetection extends Activity implements SensorEventListener {

    private float lastX, lastY, lastZ;
    private float lastgX, lastgY, lastgZ;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;
    private float deltagX = 0;
    private float deltagY = 0;
    private float deltagZ = 0;

    public int startstate, startstateg;

    private SensorManager sensorManager;
    private SensorManager sensorManagerG;
    private Sensor accelerometer;
    private Sensor gyrometer;

    private TextView statustext;


    private float vibrateThreshold = 0;

    private Switch startswitch;

    public Vibrator v;


    private int[] gyrX,gyrY,gyrZ;

    private List<Integer> list = new ArrayList<Integer>();

    private ArrayList<Float> accX = new ArrayList<Float>();
    private ArrayList<Float> accY = new ArrayList<Float>();
    private ArrayList<Float> accZ = new ArrayList<Float>();






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fall_detection);

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        startstate = 0;
        startstateg = 0;

        statustext = findViewById(R.id.textstatus);
        statustext.setText("OFF");

        startswitch = findViewById(R.id.fallswitch);
        startswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    initializesensor();
                    onResume();
                } else {
                    onPause();
                    Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    statustext.setText("OFF");
                }

            }

        });



    }

    protected void initializesensor(){

        statustext.setText("ON");

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        } else {
            // fail we dont have an accelerometer!
        }

        sensorManagerG = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManagerG.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null){

            gyrometer = sensorManagerG.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            sensorManagerG.registerListener(this, gyrometer,SensorManager.SENSOR_DELAY_NORMAL);

        }



        //initialize vibration
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        sensorManagerG.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            deltaX = Math.abs(lastX - event.values[0]);
            deltaY = Math.abs(lastY - event.values[1]);
            deltaZ = Math.abs(lastZ - event.values[2]);

            float anx =  event.values[0];
            float any =  event.values[1];
            float anz =  event.values[2];



            accX.add((float) anx);
            accY.add((float) any);
            accZ.add((float) anz);

            int beforestate = startstate - 12;

            if(startstate > 20 ){
                if (anx > accX.get(beforestate) ){
                    if (anx - accX.get(beforestate) >5){
                        onPause();
                        statustext.setText("FALL");
                        v.vibrate(200);
                        try {
                            sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        onResume();
                    }
                }
                else if (anx < accX.get(beforestate)){
                    if (accX.get(beforestate) - anx >5){
                        onPause();
                        statustext.setText("FALL");
                        v.vibrate(200);
                        try {
                            sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        onResume();

                    }
                }

                if (any > accY.get(beforestate) ){
                    if (any - accY.get(beforestate) >5){
                        onPause();
                        statustext.setText("FALL");
                        v.vibrate(200);
                        try {
                            sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        onResume();
                    }
                }
                else if (any < accY.get(beforestate)){
                    if (accY.get(beforestate) - any >5){
                        onPause();
                        statustext.setText("FALL");
                        v.vibrate(200);
                        try {
                            sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        onResume();
                    }
                }
                if (anz > accZ.get(beforestate) ){
                    if (anz - accZ.get(beforestate) >5){
                        onPause();
                        statustext.setText("FALL");
                        v.vibrate(200);
                        try {
                            sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        onResume();
                    }
                }
                else if (anz < accZ.get(beforestate)){
                    if (accZ.get(beforestate) - anz >5){
                        onPause();
                        statustext.setText("FALL");
                        v.vibrate(200);
                        try {
                            sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        onResume();
                    }
                }

            }


            startstate +=1;
        }
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {

            deltagX = Math.abs(lastgX - event.values[0]);
            deltagY = Math.abs(lastgY - event.values[1]);
            deltagZ = Math.abs(lastgZ - event.values[2]);
            ArrayList<String> temp3 = new ArrayList<>();
            temp3.add(event.values[0]+" ");
            temp3.add(event.values[1]+" ");
            temp3.add(event.values[2]+" ");
            String[] temp4 = new String[temp3.size()];
            temp4 = temp3.toArray(temp4);

            startstateg +=1;
        }

    }
}
