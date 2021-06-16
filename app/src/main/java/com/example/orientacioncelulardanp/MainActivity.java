package com.example.orientacioncelulardanp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    TextView x,y,z,orientationPhone;
    private SensorManager sm;
    private Sensor sensorMagnetometro, sensorGravedad;
    private static final String TAG = "MyActivity";
    float [] mGravedad;
    float [] mMagnetometro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        x = (TextView) findViewById(R.id.x);
        y = (TextView) findViewById(R.id.y);
        z = (TextView) findViewById(R.id.z);
        orientationPhone = (TextView) findViewById(R.id.orientation);
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE); //Aceso al sensor
        sensorMagnetometro = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorGravedad = sm.getDefaultSensor(Sensor.TYPE_GRAVITY);
        if(sensorMagnetometro == null || sensorGravedad == null){
            finish();
        }
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()){
            case Sensor.TYPE_MAGNETIC_FIELD:
                mGravedad = event.values.clone();
                break;
            case Sensor.TYPE_GRAVITY:
                mMagnetometro = event.values.clone();
                break;
            default:
                break;
        }
        if(mGravedad != null && mMagnetometro !=null){
            float [] r = new float [9];
            boolean status = SensorManager.getRotationMatrix(r, null, mGravedad, mMagnetometro);
            if(status){
                float [] orientation = new float[3];
                SensorManager.getOrientation(r,orientation);
                z.setText("Azimuth " + Math.toDegrees(orientation[0]));
                x.setText("Pitch" + Math.toDegrees(orientation[1])+"");
                y.setText("Roll" + Math.toDegrees(orientation[2])+"");
                if(Math.toDegrees(orientation[0]) - 75 > 0 && Math.toDegrees(orientation[1]) < 0) {
                    orientationPhone.setText("Horizontal 1");
                }else if(Math.toDegrees(orientation[1]) < 0 && Math.toDegrees(orientation[2]) > 0){
                    orientationPhone.setText("Vertical 1");
                }else if(Math.toDegrees(orientation[1]) > 0 && Math.toDegrees(orientation[2]) < 0){
                    orientationPhone.setText("Vertical 2");
                }else if(Math.toDegrees(orientation[1]) - 75 <0 && Math.toDegrees(orientation[1]) > 0){
                    orientationPhone.setText("Horizontal 2");
                }
                Log.d("Message", "X" + Math.toDegrees(orientation[0])+ " / Y " + Math.toDegrees(orientation[1]));
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    @Override
    protected void onResume() {
        super.onResume();
        sm.registerListener(this,sensorMagnetometro,SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(this,sensorGravedad,SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }
}