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
import android.os.Environment;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FallRecord extends Activity implements SensorEventListener {

    private float lastX, lastY, lastZ;
    private float lastgX, lastgY, lastgZ;

    public int startstate;

    private SensorManager sensorManager;
    private SensorManager sensorManagerG;
    private Sensor accelerometer;
    private Sensor gyrometer;

    private float deltaXMax = 0;
    private float deltaYMax = 0;
    private float deltaZMax = 0;
    private float deltagXMax = 0;
    private float deltagYMax = 0;
    private float deltagZMax = 0;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;
    private float deltagX = 0;
    private float deltagY = 0;
    private float deltagZ = 0;



    private TextView currentX, currentY, currentZ, maxX, maxY, maxZ;
    private TextView currentgX, currentgY, currentgZ, maxgX, maxgY, maxgZ;
    private Button Start, Reset , Save;

    public Vibrator v;

    private List<ScanResult> results;
    private List<String[]> results_all = new ArrayList<String[]>();
    private List<String[]> results_allg = new ArrayList<String[]>();
    private ArrayList<String> arrayList = new ArrayList<>();

    private String csv,csvg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fall_record);
        initializeViews();

        csv = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/Accelero"+".csv");
        csvg = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/Gyro"+".csv");

        startstate = 0;
        Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startstate == 0){

                    initializesensor();
                    onResume();
                    startstate = 1;
                    Start.setText("Pause");
                }
                else {
                    onPause();
                    startstate =0;
                    Start.setText("Resume");
                }
            }
        });

        Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);

            }
        });

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printCSV();
            }
        });

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

    public void initializeViews() {
        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
        currentZ = (TextView) findViewById(R.id.currentZ);

        maxX = (TextView) findViewById(R.id.maxX);
        maxY = (TextView) findViewById(R.id.maxY);
        maxZ = (TextView) findViewById(R.id.maxZ);

        currentgX = (TextView) findViewById(R.id.currentgX);
        currentgY = (TextView) findViewById(R.id.currentgY);
        currentgZ = (TextView) findViewById(R.id.currentgZ);

        maxgX = (TextView) findViewById(R.id.maxgX);
        maxgY = (TextView) findViewById(R.id.maxgY);
        maxgZ = (TextView) findViewById(R.id.maxgZ);

        Start = findViewById(R.id.Start);
        Reset = findViewById(R.id.Reset);
        Save = findViewById(R.id.Save);
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
            // clean current values
            displayCleanValues();
            // display the current x,y,z accelerometer values
            displayCurrentValues();
            // display the max x,y,z accelerometer values
            displayMaxValues();

            // get the change of the x,y,z values of the accelerometer
            deltaX = Math.abs(lastX - event.values[0]);
            deltaY = Math.abs(lastY - event.values[1]);
            deltaZ = Math.abs(lastZ - event.values[2]);

            // if the change is below 2, it is just plain noise
            ArrayList<String> temp = new ArrayList<>();
            temp.add(event.values[0]+" ");
            temp.add(event.values[1]+" ");
            temp.add(event.values[2]+" ");
            String[] temp2 = new String[temp.size()];
            temp2 = temp.toArray(temp2);
            results_all.add(temp2);
        }
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            // clean current values
            displayCleanValues();
            // display the current x,y,z accelerometer values
            displayCurrentValues();
            // display the max x,y,z accelerometer values
            displayMaxValues();

            // get the change of the x,y,z values of the accelerometer
            deltagX = Math.abs(lastgX - event.values[0]);
            deltagY = Math.abs(lastgY - event.values[1]);
            deltagZ = Math.abs(lastgZ - event.values[2]);
            ArrayList<String> temp3 = new ArrayList<>();
            temp3.add(event.values[0]+" ");
            temp3.add(event.values[1]+" ");
            temp3.add(event.values[2]+" ");
            String[] temp4 = new String[temp3.size()];
            temp4 = temp3.toArray(temp4);
            results_allg.add(temp4);
        }

    }


    public void displayCleanValues() {
        currentX.setText("0.0");
        currentY.setText("0.0");
        currentZ.setText("0.0");
        currentgX.setText("0.0");
        currentgY.setText("0.0");
        currentgZ.setText("0.0");
    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
        currentX.setText(Float.toString(deltaX));
        currentY.setText(Float.toString(deltaY));
        currentZ.setText(Float.toString(deltaZ));
        currentgX.setText(Float.toString(deltagX));
        currentgY.setText(Float.toString(deltagY));
        currentgZ.setText(Float.toString(deltagZ));
    }

    // display the max x,y,z accelerometer values
    public void displayMaxValues() {
        if (deltaX > deltaXMax) {
            deltaXMax = deltaX;
            maxX.setText(Float.toString(deltaXMax));
        }
        if (deltaY > deltaYMax) {
            deltaYMax = deltaY;
            maxY.setText(Float.toString(deltaYMax));
        }
        if (deltaZ > deltaZMax) {
            deltaZMax = deltaZ;
            maxZ.setText(Float.toString(deltaZMax));
        }
        if (deltagX > deltagXMax) {
            deltagXMax = deltagX;
            maxgX.setText(Float.toString(deltagXMax));
        }
        if (deltagY > deltagYMax) {
            deltagYMax = deltagY;
            maxgY.setText(Float.toString(deltagYMax));
        }
        if (deltagZ > deltagZMax) {
            deltagZMax = deltagZ;
            maxgZ.setText(Float.toString(deltagZMax));
        }
    }
    private void printCSV(){
        try {
            CSVWriter writer = null;
            writer = new CSVWriter(new FileWriter(csv));
            writer.writeAll(results_all);
            writer.close();
            CSVWriter writerg = null;
            writerg = new CSVWriter(new FileWriter(csvg));
            writerg.writeAll(results_allg);
            writerg.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
