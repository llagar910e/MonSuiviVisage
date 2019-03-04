package com.devandroid.monsuivivisage;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import static com.google.android.gms.vision.face.FaceDetector.FAST_MODE;

// build.gradle ------> implementation 'com.google.android.gms:play-services-vision:17.0.2'

public class MainActivity extends AppCompatActivity {

    CameraSource cameraSource;
    SurfaceView cameraView;
    final int RequestCameraPermissionId = 1001;
    SparseArray<Face> faces;





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionId: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraView = (SurfaceView) findViewById(R.id.cameraView);

        FaceDetector detector = new FaceDetector.Builder(this)
                .setTrackingEnabled(true)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FAST_MODE)
                .build();

        if (!detector.isOperational()) {
            Log.w("MainActivity", "FaceDetector dependencies not available");
        } else {
            cameraSource = new CameraSource.Builder(getApplicationContext(), detector)
                    .setFacing(CameraSource.CAMERA_FACING_FRONT)
                    .setRequestedFps(60.0f)
                    .setRequestedPreviewSize(1920, 1080)
                    .setAutoFocusEnabled(true)
                    .build();
            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {

                    try {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, RequestCameraPermissionId);
                            return;
                        }
                        cameraSource.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    cameraSource.stop();
                }
            });

            detector.setProcessor(new Detector.Processor<Face>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<Face> detections) {
                    faces = detections.getDetectedItems();

                    Log.i("Visage", "Nombre de visage(s) détecté(s) : " + faces.size());

                    if (faces.size() != 0) {
                        for (int i = 0; i < faces.size(); i++) {
                            Log.i("Visage", "Coordonnées du visage : " + faces.valueAt(i).getPosition().x + ";" + faces.valueAt(i).getPosition().y);
                            Log.i("Visage", "Taille du visage : " + faces.valueAt(i).getWidth() + "x" + faces.valueAt(i).getHeight());
                        }
                    }

                }
            });

        }

    }
}
