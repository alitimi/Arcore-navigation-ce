package com.example.arcore_navigation_ce;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.ar.sceneform.ux.ArFragment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ArNavigate extends AppCompatActivity implements SensorEventListener {
    private String source, destination;
    //AR variables
    ArFragment fragment;
    private PointerDrawable pointer = new PointerDrawable();
    private boolean isTracking;
    private boolean isHitting;

    //Sensor variables
    private com.example.arcore_navigation_ce.StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accelerometer, magnetometer, barometer;
    private static int numSteps = 0;
    boolean magSensor = false;
    float[] rMat = new float[9];
    float[] orientation = new float[3];
    int mAbsoluteDir;
    int mCross = 0;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;

    private double MagnitudePrevious = 0;
    private com.example.arcore_navigation_ce.StepDetector mStepDetector;
    private SensorManager mSensorManager;
    private Sensor mSensor;


    //Instruction List variables
//    Path[] mAllInstructionList = new Path[10];
    static int mInstructionNum = 0;
    private int mInstructionCnt = 0;
//    private StepDisplayer mStepDisplayer;
//    private SharedPreferences mState;

    Boolean running = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_detection);
        fragment = (ArFragment)
                getSupportFragmentManager().findFragmentById(R.id.cam_fragment);
//        mState = getSharedPreferences("state", 0);
        startNavigation();

    }


    public void startNavigation() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

//        mStepDetector = new StepDetector();
//        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//        mStepDisplayer = new StepDisplayer();
//        mStepDisplayer.setSteps(numSteps = mState.getInt("steps", 0));
//        mStepDisplayer.addListener(mStepListener);
//        mStepDetector.addStepListener(mStepDisplayer);
//        registerDetector();
//        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
//        barometer = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
//        simpleStepDetector = new StepDetector();
//        simpleStepDetector.registerListener(this);
//        numSteps = 0;
//        sensorManager.registerListener((SensorEventListener) ArNavigate.this, accelerometer,
//                SensorManager.SENSOR_DELAY_FASTEST);
//        sensorManager.registerListener((SensorEventListener) ArNavigate.this, magnetometer,
//                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onResume() {
        super.onResume();
        running = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Sensor not found!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        running = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (running) {
            numSteps = (int) event.values[0];
            runOnUiThread(() -> Toast.makeText(com.example.arcore_navigation_ce.ArNavigate.this, String.valueOf(numSteps), Toast.LENGTH_SHORT).show());
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }



    public int getRange(int degree){
        int mRangeVal=0;
        if (degree>335 || degree <25)
            mRangeVal=1;    //N
        else if (degree>65 && degree <115)
            mRangeVal=2;    //E
        else if (degree>155 && degree <205)
            mRangeVal=3;    //S
        else if (degree>245 && degree <295)
            mRangeVal=4;
        return mRangeVal;
    }


//    @Override
//    public void onSensorChanged(SensorEvent sensorEvent) {
//        Sensor sensor = sensorEvent.sensor;
//        float[] values = sensorEvent.values;
//        int value = -1;
//
//        if (values.length > 0) {
//            value = (int) values[0];
//        }
//
//
//        if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
//            numSteps++;
//        }
//
//
//    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int i) {
////        sensorManager.registerListener(stepDetector, sensor, SensorManager.SENSOR_DELAY_NORMAL);
//
//    }

//    @Override
//    public void step(long timeNs) {
//        Toast.makeText(this, String.valueOf(numSteps), Toast.LENGTH_SHORT).show();
//        numSteps++;
//    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


}
