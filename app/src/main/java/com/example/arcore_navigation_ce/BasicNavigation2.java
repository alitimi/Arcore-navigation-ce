package com.example.arcore_navigation_ce;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
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

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.schedulers.Schedulers;

public class BasicNavigation2 extends AppCompatActivity implements SensorEventListener {

    private SensorManager SensorManage;
    private com.example.arcore_navigation_ce.StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private final float[] mLastAccelerometer = new float[3];
    private final float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    float[] rMat = new float[9];
    float[] orientation = new float[3];
    int mAbsoluteDir;
    // record the angle turned of the compass picture
    private float DegreeStart = 0f;
    private ArFragment fragment;
    String origin;
    String destLatLong;


    private PointerDrawable pointer = new PointerDrawable();
    private boolean isTracking;
    private boolean isHitting;
    Handler handler;
    Integer i = 0;
    Integer j = 0;

    int count = 0;

    boolean run =true;
    Vibrator v;
    Button done;

    double latitude;
    double longitude;
    ArrayList<String> index_location = new ArrayList<>();
    ArrayList<String> index_instruction = new ArrayList<>();
    ArrayList<String> index_maneuvers = new ArrayList<>();



    Runnable runnable;

    public BasicNavigation2(){


    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//        float degree = Math.round(event.values[0]);
//        DegreeStart = getRange((int) ((-degree + 360) %360));
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
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_detection);

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        simpleStepDetector = new com.example.arcore_navigation_ce.StepDetector();
//        simpleStepDetector.registerListener(this);
        sensorManager.registerListener(com.example.arcore_navigation_ce.BasicNavigation2.this, accelerometer,
                SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(com.example.arcore_navigation_ce.BasicNavigation2.this, magnetometer,
                SensorManager.SENSOR_DELAY_UI);


        //Source and Destination String
        Bundle b = getIntent().getExtras();
        origin = b.getString("origin");
        destLatLong = b.getString("destLatLong");

        try {
            Root root = ServiceModule.providePlaceService().getRoute(origin, "car", destLatLong)
                    .subscribeOn(Schedulers.io()).blockingFirst();
            for (int i = 0; i < root.routes.get(0).legs.get(0).steps.size(); i++) {
                index_instruction.add(root.routes.get(0).legs.get(0).steps.get(i).instruction);
                index_location.add(root.routes.get(0).legs.get(0).steps.get(i).start_location.get(0) +
                        "," + root.routes.get(0).legs.get(0).steps.get(i).start_location.get(1));
                index_maneuvers.add(root.routes.get(0).legs.get(0).steps.get(i).maneuver);
            }
            System.out.println(root.getRoutes().get(0).getLegs().get(0).summary);
        } catch (Exception e) {
            e.printStackTrace();
        }

        fragment = (ArFragment)
                getSupportFragmentManager().findFragmentById(R.id.cam_fragment);
        fragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
            fragment.onUpdate(frameTime);
            onUpdate();
        });

        if (isNetworkConnected()) {
            if (isGPSEnabled(BasicNavigation2.this)) {
                handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (run) {
                            GPSTracker gpsTracker = new GPSTracker(BasicNavigation2.this);
                            if (gpsTracker.canGetLocation()) {
                                latitude = gpsTracker.getLatitude();
                                longitude = gpsTracker.getLongitude();
                                startNavigation();
                            } else {
                                gpsTracker.showSettingsAlert();
                            }
                            handler.postDelayed(this, 10000);
                            if (index_maneuvers.get(j).equals("depart")) {
                                Toast.makeText(BasicNavigation2.this, String.valueOf(mAbsoluteDir), Toast.LENGTH_SHORT).show();
                                if (mAbsoluteDir == 2) {
                                    if (index_instruction.get(j).contains("شمال")) {
//                                    Toast.makeText(BasicNavigation2.this, "depart شمال", Toast.LENGTH_SHORT).show();
                                        addObject(Uri.parse("Arrow_Left_Zneg.sfb"));
                                    }
                                    if (index_instruction.get(j).contains("جنوب")) {
//                                    Toast.makeText(BasicNavigation2.this, "depart جنوب", Toast.LENGTH_SHORT).show();
                                        addObject(Uri.parse("Arrow_Right_Zneg.sfb"));
                                    }
                                    if (index_instruction.get(j).contains("غرب")) {
//                                    Toast.makeText(BasicNavigation2.this, "depart غرب", Toast.LENGTH_SHORT).show();
                                        addObject(Uri.parse("Arrow_straight_Zpos.sfb"));
                                    }
                                    if (index_instruction.get(j).contains("شرق")) {
//                                    Toast.makeText(BasicNavigation2.this, "depart شرق", Toast.LENGTH_SHORT).show();
                                        addObject(Uri.parse("Arrow_straight_Zneg.sfb"));
                                    }
                                }
                                if (mAbsoluteDir == 4) {
                                    if (index_instruction.get(j).contains("شمال")) {
//                                    Toast.makeText(BasicNavigation2.this, "depart شمال", Toast.LENGTH_SHORT).show();
                                        addObject(Uri.parse("Arrow_Right_Zneg.sfb"));
                                    }
                                    if (index_instruction.get(j).contains("شرق")) {
//                                    Toast.makeText(BasicNavigation2.this, "depart شرق", Toast.LENGTH_SHORT).show();
                                        addObject(Uri.parse("Arrow_straight_Zpos.sfb"));
                                    }
                                    if (index_instruction.get(j).contains("جنوب")) {
//                                    Toast.makeText(BasicNavigation2.this, "depart جنوب", Toast.LENGTH_SHORT).show();
                                        addObject(Uri.parse("Arrow_Left_Zneg.sfb"));
                                    }
                                    if (index_instruction.get(j).contains("غرب")) {
//                                    Toast.makeText(BasicNavigation2.this, "depart غرب", Toast.LENGTH_SHORT).show();
                                        addObject(Uri.parse("Arrow_straight_Zneg.sfb"));
                                    }
                                }
                                if (mAbsoluteDir == 1) {
                                    if (index_instruction.get(j).contains("شرق")) {
//                                    Toast.makeText(BasicNavigation2.this, "depart شرق", Toast.LENGTH_SHORT).show();
                                        addObject(Uri.parse("Arrow_Right_Zneg.sfb"));
                                    }
                                    if (index_instruction.get(j).contains("جنوب")) {
//                                    Toast.makeText(BasicNavigation2.this, "depart جنوب", Toast.LENGTH_SHORT).show();
                                        addObject(Uri.parse("Arrow_straight_Zpos.sfb"));
                                    }
                                    if (index_instruction.get(j).contains("غرب")) {
//                                    Toast.makeText(BasicNavigation2.this, "depart غرب", Toast.LENGTH_SHORT).show();
                                        addObject(Uri.parse("Arrow_Left_Zneg.sfb"));
                                    }
                                    if (index_instruction.get(j).contains("شمال")) {
//                                    Toast.makeText(BasicNavigation2.this, "depart شمال", Toast.LENGTH_SHORT).show();
                                        addObject(Uri.parse("Arrow_straight_Zneg.sfb"));
                                    }
                                }
                                if (mAbsoluteDir == 3) {
                                    if (index_instruction.get(j).contains("شمال")) {
//                                    Toast.makeText(BasicNavigation2.this, "depart شمال", Toast.LENGTH_SHORT).show();
                                        addObject(Uri.parse("Arrow_straight_Zpos.sfb"));
                                    }
                                    if (index_instruction.get(j).contains("شرق")) {
//                                    Toast.makeText(BasicNavigation2.this, "depart شرق", Toast.LENGTH_SHORT).show();
                                        addObject(Uri.parse("Arrow_Left_Zneg.sfb"));
                                    }
                                    if (index_instruction.get(j).contains("غرب")) {
//                                    Toast.makeText(BasicNavigation2.this, "depart غرب", Toast.LENGTH_SHORT).show();
                                        addObject(Uri.parse("Arrow_Right_Zneg.sfb"));
                                    }
                                    if (index_instruction.get(j).contains("جنوب")) {
//                                    Toast.makeText(BasicNavigation2.this, "depart جنوب", Toast.LENGTH_SHORT).show();
                                        addObject(Uri.parse("Arrow_straight_Zneg.sfb"));
                                    }
                                }
                                if (mAbsoluteDir == 0) {
                                    Toast.makeText(BasicNavigation2.this, "در حال تشخیص جهت جغرافیایی", Toast.LENGTH_SHORT).show();
                                }
                            }
                            if (index_maneuvers.get(j).equals("right")) {
                                Toast.makeText(BasicNavigation2.this, "right", Toast.LENGTH_SHORT).show();
                                addObject(Uri.parse("Arrow_Right_Zneg.sfb"));
                            }
                            if (index_maneuvers.get(j).equals("left")) {
                                Toast.makeText(BasicNavigation2.this, "left", Toast.LENGTH_SHORT).show();
                                addObject(Uri.parse("Arrow_Left_Zneg.sfb"));
                            }
                            if (index_maneuvers.get(j).equals("uturn")) {
                                Toast.makeText(BasicNavigation2.this, "uturn", Toast.LENGTH_SHORT).show();
                                addObject(Uri.parse("Arrow_Left_Zneg.sfb"));
                            }
                        }
                    }

                });

            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void startNavigation() {

        Location user = new Location("");
        user.setLatitude(latitude);
        user.setLongitude(longitude);
        Location location = new Location("");
        String[] loc = index_location.get(j+1).split(",");
        location.setLatitude(Double.parseDouble(loc[1]));
        location.setLongitude(Double.parseDouble(loc[0]));
        double distanceInMetersOne = meterDistanceBetweenPoints(Float.parseFloat(
                String.valueOf(user.getLatitude())), Float.parseFloat(String.valueOf(user.getLongitude())),
                Float.parseFloat(String.valueOf(location.getLatitude())), Float.parseFloat(String.valueOf(location.getLongitude())));
        if (j != index_location.size()) {
            if (distanceInMetersOne >= 2.0 && distanceInMetersOne <= 3.0) {
//            Toast.makeText(this, index_instruction.get(j) + "\n" + distanceInMetersOne, Toast.LENGTH_SHORT).show();
                j++;
            }
            Toast.makeText(this, index_instruction.get(j) + "\n" + Math.round(distanceInMetersOne), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "شما به مقصد رسیده اید.", Toast.LENGTH_SHORT).show();
        }


    }

    private double meterDistanceBetweenPoints(float lat_a, float lng_a, float lat_b, float lng_b) {
        float pk = (float) (180.f / Math.PI);

        float a1 = lat_a / pk;
        float a2 = lng_a / pk;
        float b1 = lat_b / pk;
        float b2 = lng_b / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000 * tt;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public boolean isGPSEnabled(Context mContext) {
        LocationManager lm = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

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
        //Handling Rotaional orientation using Quaternion
        TransformableNode node = new TransformableNode(fragment.getTransformationSystem());
//        node.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 90f));
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
        run = false;
        Intent intent = new Intent(BasicNavigation2.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);// optional depending on your needs
        finish();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //----------------------------------------------------------------------------------------------

}