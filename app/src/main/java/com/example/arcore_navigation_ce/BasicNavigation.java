package com.example.arcore_navigation_ce;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class BasicNavigation extends AppCompatActivity implements SensorEventListener {

    private com.example.arcore_navigation_ce.StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private int numSteps = 0;
    private ArFragment fragment;

    String source;
    String sourceFloor;
    String sourcePlace;
    String destinationFloor;
    String destination;
    String src_dst;
    ArrayList<String> myPath = new ArrayList();
    private PointerDrawable pointer = new PointerDrawable();
    private boolean isTracking;
    private boolean isHitting;

    private int stepsFromDetector = 0;
    private int stepsFromCounter = 0;


    private int mListenerRegistered = 0;

    private final float[] mLastAccelerometer = new float[3];
    private final float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    float[] rMat = new float[9];
    float[] orientation = new float[3];
    int mAbsoluteDir;
    boolean goNext = false;
    private int mInstructionNum;
    int index = 0;
    Vibrator v;

    private String destinationPlace;

    public BasicNavigation() throws JSONException {
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_detection);
        requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, REQUEST_CODE_ASK_PERMISSIONS);
//        done = findViewById(R.id.done);
//        done.setOnClickListener(view -> {
//            Toast.makeText(com.example.arcore_navigation_ce.BasicNavigation.this, "Steps : " + numSteps, Toast.LENGTH_SHORT).show();
//        });


        mInstructionNum = 0;
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        //Source and Destination String
        Bundle b = getIntent().getExtras();
        try {
            JSONObject directionsJson = new JSONObject(loadJSONFromAsset("directions.json"));
            JSONArray dirs = directionsJson.getJSONArray("dirs");
            JSONObject dictionaryJson = new JSONObject(loadJSONFromAsset("dictionary.json"));
            JSONArray dictionary = dictionaryJson.getJSONArray("dict");
            JSONObject dictObj = new JSONObject(dictionary.get(0).toString());
            source = dictObj.get(b.getString("source")).toString();
            sourcePlace = source.split("_")[0];
            sourceFloor = source.split("_")[1];
            destination = dictObj.get(b.getString("destination")).toString();
            destinationPlace = destination.split("_")[0];
            destinationFloor = destination.split("_")[1];
            if (!sourceFloor.equals(destinationFloor)) {
                Toast.makeText(BasicNavigation.this, "به آسانسور هدایت می شوید", Toast.LENGTH_SHORT).show();
                src_dst = sourcePlace + sourceFloor + "_Elevator" + sourceFloor;
            } else {
                src_dst = sourcePlace + sourceFloor + '_' + destinationPlace + destinationFloor;
            }
            JSONArray path = new JSONObject(dirs.get(0).toString()).getJSONArray(src_dst);
            for (int i = 0; i < path.length(); i++) {
                String dir = String.valueOf(path.getJSONObject(i).toString().charAt(2));
                Integer steps = Integer.valueOf(new JSONObject((path.getJSONObject(i).toString())).get(dir).toString());
                myPath.add(dir + ' ' + String.valueOf(steps));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        fragment = (ArFragment)
                getSupportFragmentManager().findFragmentById(R.id.cam_fragment);
        fragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
            fragment.onUpdate(frameTime);
            onUpdate();
        });

        startNavigation();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void startNavigation() {

        //Sensors
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        Sensor stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        simpleStepDetector = new com.example.arcore_navigation_ce.StepDetector();
//        simpleStepDetector.registerListener(this);
        numSteps = 0;
        sensorManager.registerListener(com.example.arcore_navigation_ce.BasicNavigation.this, accelerometer,
                SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(com.example.arcore_navigation_ce.BasicNavigation.this, magnetometer,
                SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(com.example.arcore_navigation_ce.BasicNavigation.this, stepDetector, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(com.example.arcore_navigation_ce.BasicNavigation.this, stepCounter, SensorManager.SENSOR_DELAY_UI);
        mListenerRegistered = 1;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccelerometer(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccelerometer(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(rMat, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(rMat, orientation);
            mAbsoluteDir = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
            mAbsoluteDir = getRange(Math.round(mAbsoluteDir));
        }

//        Log.e("ALi", "sensor");
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            numSteps++;
            Log.e("ALi", "step" + numSteps);
            Toast.makeText(BasicNavigation.this, String.valueOf(numSteps), Toast.LENGTH_SHORT).show();
            if (index < myPath.size()) {
                String[] strings = myPath.get(index).split(" ");
                Integer key = Integer.valueOf(strings[0]);
                Integer value = Integer.valueOf(strings[1]);
                if (mAbsoluteDir == key) {
                    Toast.makeText(this, "در جهت " + mapDirection(key) + " : " + numSteps, Toast.LENGTH_SHORT).show();

                    if (numSteps == value) {
                        numSteps = 0;
                        index++;
                        if (index == myPath.size()) {
                            Toast.makeText(BasicNavigation.this, "شما به مقصد رسیده اید.", Toast.LENGTH_SHORT).show();
                        } else {
                            String[] strings2 = myPath.get(index).split(" ");
                            Integer key2 = Integer.valueOf(strings2[0]);
                            Toast.makeText(BasicNavigation.this, "لطفا در جهت " + mapDirection(key2) + " قرار بگیرید.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (mAbsoluteDir == 1) {
                        if (key == 2) {
                            addObject(Uri.parse("Arrow_Right_Zneg.sfb"));
                        }
                        if (key == 3) {
                            addObject(Uri.parse("Arrow_straight_Zpos.sfb"));
                        }
                        if (key == 4) {
                            addObject(Uri.parse("Arrow_Left_Zneg.sfb"));
                        }
                    }
                    if (mAbsoluteDir == 2) {
                        if (key == 1) {
                            addObject(Uri.parse("Arrow_Left_Zneg.sfb"));
                        }
                        if (key == 3) {
                            addObject(Uri.parse("Arrow_Right_Zneg.sfb"));
                        }
                        if (key == 4) {
                            addObject(Uri.parse("Arrow_straight_Zneg.sfb"));
                        }
                    }
                    if (mAbsoluteDir == 3) {
                        if (key == 1) {
                            addObject(Uri.parse("Arrow_straight_Zneg.sfb"));
                        }
                        if (key == 2) {
                            addObject(Uri.parse("Arrow_Left_Zneg.sfb"));
                        }
                        if (key == 4) {
                            addObject(Uri.parse("Arrow_Right_Zneg.sfb"));
                        }
                    }
                    if (mAbsoluteDir == 4) {
                        if (key == 1) {
                            addObject(Uri.parse("Arrow_Right_Zneg.sfb"));
                        }
                        if (key == 2) {
                            addObject(Uri.parse("Arrow_straight_Zneg.sfb"));
                        }
                        if (key == 3) {
                            addObject(Uri.parse("Arrow_Left_Zneg.sfb"));
                        }
                    }
                    Toast.makeText(BasicNavigation.this, "لطفا در جهت " + mapDirection(key) + " قرار بگیرید.", Toast.LENGTH_SHORT).show();
                    numSteps = 0;
                }


            } else if (index == myPath.size()) {
                Toast.makeText(BasicNavigation.this, "شما به مقصد خود رسیده اید", Toast.LENGTH_SHORT).show();
                if (!sourceFloor.equals(destinationFloor)) {
                    Intent intent = new Intent(BasicNavigation.this, Destination.class);
                    startActivity(intent);
                }
            }
            Toast.makeText(com.example.arcore_navigation_ce.BasicNavigation.this, "From Detector : " + numSteps, Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

//    @RequiresApi(api = Build.VERSION_CODES.N)
//    @Override
//    public void step(long timeNs) {
//        Snackbar snackbar;
//        int limNumSteps = -1;
//        if (numSteps == limNumSteps) {
//            mListenerRegistered = 0;
//            sensorManager.unregisterListener(BasicNavigation.this);
//            numSteps = 0;
//        }
//
//        if (index < myPath.size()) {
//            String[] strings = myPath.get(index).split(" ");
//            Integer key = Integer.valueOf(strings[0]);
//            Integer value = Integer.valueOf(strings[1]);
//            if (mAbsoluteDir == key) {
//                numSteps++;
//                Toast.makeText(this, "Correct Direction " + key + " : " + numSteps, Toast.LENGTH_SHORT).show();
////                addObject(Uri.parse("Arrow_straight_Zneg.sfb"));
//
//                if (numSteps == value) {
////                    Toast.makeText(BasicNavigation.this, "You walked " + numSteps, Toast.LENGTH_SHORT).show();
//                    numSteps = 0;
//                    index++;
//
//                    if (index == myPath.size()) {
//                        Toast.makeText(BasicNavigation.this, "You've reached your destination", Toast.LENGTH_SHORT).show();
//                    } else {
//                        String[] strings2 = myPath.get(index).split(" ");
//                        Integer key2 = Integer.valueOf(strings2[0]);
//                        Toast.makeText(BasicNavigation.this, "Please Turn" + key2, Toast.LENGTH_SHORT).show();
//                    }
//                }
//            } else {
//                if (mAbsoluteDir == 1) {
//                    if (key == 2) {
//                        addObject(Uri.parse("Arrow_Right_Zneg.sfb"));
//                    }
//                    if (key == 3) {
//                        addObject(Uri.parse("Arrow_straight_Zpos.sfb"));
//                    }
//                    if (key == 4) {
//                        addObject(Uri.parse("Arrow_Left_Zneg.sfb"));
//                    }
//                }
//                if (mAbsoluteDir == 2) {
//                    if (key == 1) {
//                        addObject(Uri.parse("Arrow_Left_Zneg.sfb"));
//                    }
//                    if (key == 3) {
//                        addObject(Uri.parse("Arrow_Right_Zneg.sfb"));
//                    }
//                    if (key == 4) {
//                        addObject(Uri.parse("Arrow_straight_Zneg.sfb"));
//                    }
//                }
//                if (mAbsoluteDir == 3) {
//                    if (key == 1) {
//                        addObject(Uri.parse("Arrow_straight_Zneg.sfb"));
//                    }
//                    if (key == 2) {
//                        addObject(Uri.parse("Arrow_Left_Zneg.sfb"));
//                    }
//                    if (key == 4) {
//                        addObject(Uri.parse("Arrow_Right_Zneg.sfb"));
//                    }
//                }
//                if (mAbsoluteDir == 4) {
//                    if (key == 1) {
//                        addObject(Uri.parse("Arrow_Right_Zneg.sfb"));
//                    }
//                    if (key == 2) {
//                        addObject(Uri.parse("Arrow_straight_Zneg.sfb"));
//                    }
//                    if (key == 3) {
//                        addObject(Uri.parse("Arrow_Left_Zneg.sfb"));
//                    }
//                }
//                Toast.makeText(BasicNavigation.this, "Wrong direction, Turn" + key, Toast.LENGTH_SHORT).show();
//            }
//
//        } else if (index == myPath.size()) {
//            Toast.makeText(BasicNavigation.this, "You've reached your destination2", Toast.LENGTH_SHORT).show();
//        }
//
//    }


    public int getRange(int degree) {
        int mRangeVal = 0;
        if (degree > 335 || degree < 25)
            mRangeVal = 1;    //N
        else if (degree > 65 && degree < 115)
            mRangeVal = 2;    //E
        else if (degree > 155 && degree < 205)
            mRangeVal = 3;    //S
        else if (degree > 245 && degree < 295)
            mRangeVal = 4;    //W
        return mRangeVal;
    }

    public String mapDirection(int key) {
        if (key == 1)
            return "شمال";
        else if (key == 2)
            return "شرق";
        else if (key == 3)
            return "جنوب";
        else if (key == 4)
            return "غرب";
        return "جهت جغرافیایی تشخیص داده نشده است";
    }

    public String loadJSONFromAsset(String address) {
        String json = null;
        try {
            InputStream is = this.getAssets().open(address);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    //-----------------------------AR Object placement----------------------------------------------

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void addObject(Uri uri) {
        Frame frame = fragment.getArSceneView().getArFrame();
        android.graphics.Point pt = getScreenCenter();
        if (frame != null) {
//            hits.addAll(frame.hitTest(pt.x, pt.y));
            for (HitResult hit : frame.hitTest(pt.x, pt.y)) {
                Trackable trackable = hit.getTrackable();
                if (trackable instanceof Plane &&
                        ((Plane) trackable).isPoseInPolygon(hit.getHitPose())) {
                    placeObject(fragment, hit.createAnchor(), uri);
                    break;
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void placeObject(ArFragment fragment, Anchor anchor, Uri model) {
        ModelRenderable.builder()
                .setSource(fragment.getContext(), model)
                .build()
                .thenAccept(modelRenderable -> addNodeToScene(fragment, anchor, modelRenderable))
                .exceptionally(throwable -> {
                            Toast.makeText(fragment.getContext(), "Error:" + throwable.getMessage(), Toast.LENGTH_LONG).show();
                            return null;
                        }

                );
    }

    private void addNodeToScene(ArFragment fragment, Anchor anchor, Renderable renderable) {
        AnchorNode anchorNode = new AnchorNode(anchor);

        //Handling Rotational orientation using Quaternion
        TransformableNode node = new TransformableNode(fragment.getTransformationSystem());

        node.setRenderable(renderable);
        node.setParent(anchorNode);
        fragment.getArSceneView().getScene().addChild(anchorNode);
        node.select();
    }

    //---------------------------AR green dot center detection methods------------------------------

    private void onUpdate() {
        boolean trackingChanged = updateTracking();
        View contentView = findViewById(android.R.id.content);
        if (trackingChanged) {
            if (isTracking) {
                contentView.getOverlay().add(pointer);
            } else {
                contentView.getOverlay().remove(pointer);
            }
            contentView.invalidate();
        }

        if (isTracking) {
            boolean hitTestChanged = updateHitTest();
            if (hitTestChanged) {
                pointer.setEnabled(isHitting);
                contentView.invalidate();
            }
        }
    }

    private boolean updateTracking() {
        Frame frame = fragment.getArSceneView().getArFrame();
        boolean wasTracking = isTracking;
        isTracking = frame != null &&
                frame.getCamera().getTrackingState() == TrackingState.TRACKING;
        return isTracking != wasTracking;
    }

    private boolean updateHitTest() {
        Frame frame = fragment.getArSceneView().getArFrame();
        android.graphics.Point pt = getScreenCenter();
        List<HitResult> hits;
        boolean wasHitting = isHitting;
        isHitting = false;
        if (frame != null) {
            hits = frame.hitTest(pt.x, pt.y);
            for (HitResult hit : hits) {
                Trackable trackable = hit.getTrackable();
                if (trackable instanceof Plane &&
                        ((Plane) trackable).isPoseInPolygon(hit.getHitPose())) {
                    isHitting = true;
                    break;
                }
            }
        }
        return wasHitting != isHitting;
    }

    private android.graphics.Point getScreenCenter() {
        View vw = findViewById(android.R.id.content);
        return new android.graphics.Point(vw.getWidth() / 2, vw.getHeight() / 2);
    }

    @Override
    public void onBackPressed() {
        // code here to show dialog
        Intent intent = new Intent(BasicNavigation.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);// optional depending on your needs
        finish();
    }

}