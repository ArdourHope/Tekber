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
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    private TextView statustext, counterText;


    private float vibrateThreshold = 0;

    private Switch startswitch;

    public Vibrator v;


    private int[] gyrX,gyrY,gyrZ;

    private List<Integer> list = new ArrayList<Integer>();

    private ArrayList<Float> accX = new ArrayList<Float>();
    private ArrayList<Float> accY = new ArrayList<Float>();
    private ArrayList<Float> accZ = new ArrayList<Float>();

    DatabaseHelper mDatabasehelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fall_detection);

        mDatabasehelper = new DatabaseHelper(this);

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
                    statustext.setText("ON");
                    initializesensor();

                } else {
                    onPause();

                    statustext.setText("OFF");
            }

            }

        });

        Button ButtonDat = findViewById(R.id.History);
        ButtonDat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(FallDetection.this, ViewData.class));
            }

        });

        counterText = findViewById(R.id.counterFall);
        String countertext = mDatabasehelper.count();
        counterText.setText(countertext);



    }

    public void AddData(String History){
        boolean insertData = mDatabasehelper.addData(History);

        if (insertData) {
            toastMessage("Data Recorded");
        }
        else {
            toastMessage("Data Recorded");
        }

    }

    protected void initializesensor(){



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

    protected void onResume() {
        super.onResume();
        statustext.setText("ON");
        initializesensor();

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
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                if (anx > accX.get(beforestate) ){
                    if (anx - accX.get(beforestate) >5){
                        onPause();
                        statustext.setText("FALL");
                        AddData(date);
                        v.vibrate(200);
                        try {
                            sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }



                    }
                }
                else if (anx < accX.get(beforestate)){
                    if (accX.get(beforestate) - anx >5){
                        onPause();
                        statustext.setText("FALL");
                        AddData(date);
                        v.vibrate(200);
                        try {
                            sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }



                    }
                }

                if (any > accY.get(beforestate) ){
                    if (any - accY.get(beforestate) >5){
                        onPause();
                        statustext.setText("FALL");
                        AddData(date);
                        v.vibrate(200);

                        try {
                            sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }



                    }
                }
                else if (any < accY.get(beforestate)){
                    if (accY.get(beforestate) - any >5){
                        onPause();
                        statustext.setText("FALL");
                        AddData(date);
                        v.vibrate(200);
                        try {
                            sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }




                    }
                }
                if (anz > accZ.get(beforestate) ){
                    if (anz - accZ.get(beforestate) >5){
                        onPause();
                        statustext.setText("FALL");
                        AddData(date);
                        v.vibrate(200);
                        try {
                            sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }




                    }
                }
                else if (anz < accZ.get(beforestate)){
                    if (accZ.get(beforestate) - anz >5){
                        onPause();
                        statustext.setText("FALL");
                        AddData(date);
                        v.vibrate(200);
                        try {
                            sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }




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

    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
