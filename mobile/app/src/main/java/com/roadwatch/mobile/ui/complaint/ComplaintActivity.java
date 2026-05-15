package com.roadwatch.mobile.ui.complaint;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;
import com.roadwatch.mobile.R;
import com.roadwatch.mobile.data.AppDatabase;
import com.roadwatch.mobile.data.ComplaintEntity;
import com.roadwatch.mobile.workers.SyncWorker;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ComplaintActivity extends AppCompatActivity {

    private ImageCapture imageCapture;
    private File outputDirectory;
    private ExecutorService cameraExecutor;
    private com.google.android.gms.location.FusedLocationProviderClient fusedLocationClient;
    private String pendingImagePath;
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[] { Manifest.permission.CAMERA };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        android.view.View captureButton = findViewById(R.id.captureButton);
        captureButton.setOnClickListener(v -> takePhoto());

        outputDirectory = getOutputDirectory();
        cameraExecutor = Executors.newSingleThreadExecutor();
        fusedLocationClient = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(this);
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                PreviewView viewFinder = findViewById(R.id.viewFinder);
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder().build();

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

            } catch (ExecutionException | InterruptedException e) {
                Log.e("CameraX", "Use case binding failed", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void takePhoto() {
        if (imageCapture == null)
            return;

        File photoFile = new File(outputDirectory, System.currentTimeMillis() + ".jpg");

        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        saveToRoomAndSync(photoFile.getAbsolutePath());
                        Toast.makeText(ComplaintActivity.this, "Complaint Recorded!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e("CameraX", "Photo capture failed: " + exception.getMessage(), exception);
                    }
                });
    }

    private void saveToRoomAndSync(String imagePath) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            pendingImagePath = imagePath;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        fusedLocationClient.getCurrentLocation(com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .addOnSuccessListener(this, location -> finalizeComplaintSave(imagePath, location))
                .addOnFailureListener(this, e -> {
                    Log.e("CameraX", "Location fetch failed", e);
                    Toast.makeText(this, "Error fetching location. Saving without location.", Toast.LENGTH_LONG).show();
                    finalizeComplaintSave(imagePath, null);
                });
    }

    private void finalizeComplaintSave(String imagePath, android.location.Location location) {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Edge AI Inference
            android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeFile(imagePath);
            com.roadwatch.mobile.ml.DefectAnalyzer analyzer = new com.roadwatch.mobile.ml.DefectAnalyzer();
            String aiVerdict = analyzer.analyzeCapturedPhoto(bitmap);

            ComplaintEntity entity = new ComplaintEntity();
            entity.imagePath = imagePath;
            if (location != null) {
                entity.setLocation("POINT(" + location.getLongitude() + " " + location.getLatitude() + ")");
                runOnUiThread(() -> Toast.makeText(ComplaintActivity.this, "Location secured!", Toast.LENGTH_SHORT).show());
            } else {
                entity.setLocation(null); // Fallback: save anyway
                runOnUiThread(() -> Toast.makeText(ComplaintActivity.this, "Warning: GPS signal lost. Saving without location.", Toast.LENGTH_LONG).show());
            }
            entity.timestamp = System.currentTimeMillis();
            entity.description = "User reported complaint";
            entity.roadType = "NH"; // Mocked RoadType
            entity.isSynced = false;
            entity.setSeverity(aiVerdict);

            AppDatabase.getDatabase(ComplaintActivity.this).complaintDao().insert(entity);

            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            OneTimeWorkRequest syncWorkRequest = new OneTimeWorkRequest.Builder(SyncWorker.class)
                    .setConstraints(constraints)
                    .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                            java.util.concurrent.TimeUnit.MILLISECONDS)
                    .build();

            WorkManager.getInstance(ComplaintActivity.this).enqueue(syncWorkRequest);
        });
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private File getOutputDirectory() {
        File[] mediaDirs = getExternalMediaDirs();
        File mediaDir = mediaDirs != null && mediaDirs.length > 0 ? new File(mediaDirs[0], "RoadWatch") : null;
        if (mediaDir != null && !mediaDir.exists()) {
            mediaDir.mkdirs();
        }
        return mediaDir != null && mediaDir.exists() ? mediaDir : getFilesDir();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (pendingImagePath != null) {
                    saveToRoomAndSync(pendingImagePath);
                    pendingImagePath = null;
                }
            } else {
                Toast.makeText(this, "Location required to log defects", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
