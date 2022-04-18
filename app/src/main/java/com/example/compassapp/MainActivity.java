package com.example.compassapp;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    TextView compass_angle;
    ImageView iv_compass;
    Float azimuth_angle;
    private SensorManager compassSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;
    private float current_degree = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        compassSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = compassSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = compassSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    protected void onResume() {
        super.onResume();
        compassSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        compassSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    protected void onPause() {
        super.onPause();
        compassSensorManager.unregisterListener(this);
    }

    float[] accel_raed;
    float[] magnetic_raed;

    @Override
    public void onSensorChanged(SensorEvent event) {
        TextView compass_angle = (TextView) findViewById(R.id.compass_angle);
        ImageView iv_compass = (ImageView) findViewById(R.id.iv_compass);
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            accel_raed = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            magnetic_raed = event.values;
        if(accel_raed != null && magnetic_raed != null){
            float R[] = new float[9];
            float I[] = new float[9];
            boolean successful_read = SensorManager.getRotationMatrix(R,I,accel_raed,magnetic_raed);

            if (successful_read) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimuth_angle = orientation[0];
                float degrees = ((azimuth_angle * 180f) / 3.14f);
                int degreesInt = Math.round(degrees);
                compass_angle.setText(Integer.toString(degreesInt) + (char) 0x00B0 + " to absolute north.");

                RotateAnimation rotate = new RotateAnimation(current_degree, -degreesInt, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(100);
                rotate.setFillAfter(true);
                iv_compass.startAnimation(rotate);
                current_degree = -degreesInt;
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}