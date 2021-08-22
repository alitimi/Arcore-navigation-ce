package com.example.arcore_navigation_ce;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.schedulers.Schedulers;

public class BasicNavigation2 extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private ArFragment fragment;
    String origin;
    String destLatLong;

    private PointerDrawable pointer = new PointerDrawable();
    private boolean isTracking;
    private boolean isHitting;

    Vibrator v;
    Button done;


    public BasicNavigation2() throws JSONException {
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_detection);

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        //Source and Destination String
        Bundle b = getIntent().getExtras();
        origin = b.getString("origin");
        destLatLong = b.getString("destLatLong");
        try {
            Root root = ServiceModule.providePlaceService().getRoute(origin, "car", destLatLong).subscribeOn(Schedulers.io()).blockingFirst();
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
        startNavigation();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void startNavigation() {
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onSensorChanged(SensorEvent event) {

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

    //----------------------------------------------------------------------------------------------
}