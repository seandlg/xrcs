/*
 * Copyright 2016 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.xrcs.tensorflow;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.Image.Plane;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Trace;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Locale;

import com.example.android.xrcs.tensorflow.env.ImageUtils;
import com.example.android.xrcs.tensorflow.env.Logger;
import com.example.android.xrcs.R; // Explicit import needed for internal Google builds.
import com.example.android.xrcs.TimerRedirectActivity;


public abstract class CameraActivity extends Activity
        implements OnImageAvailableListener, Camera.PreviewCallback {
    private static final Logger LOGGER = new Logger();

    private static final int PERMISSIONS_REQUEST = 1;

    private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    private static final String PERMISSION_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public TextToSpeech t1;

    private boolean debug = false;

    private Handler handler;
    private HandlerThread handlerThread;
    private boolean useCamera2API;
    private boolean isProcessingFrame = false;
    private byte[][] yuvBytes = new byte[3][];
    private int[] rgbBytes = null;
    private int yRowStride;

    protected int previewWidth = 0;
    protected int previewHeight = 0;

    private Runnable postInferenceCallback;
    private Runnable imageConverter;

    public workoutLogger myWorkoutLogger;
    public TextView noRepsTV;
    public TextView noSetsTV;
    public TextView workoutHeading;
    public TextView restBetween;
    public TextView setTargetTime;
    public static Handler tvHandler;
    public String workoutType;
    public int currentSet;

    public static class workoutLogger {
        private LinkedList<RectF> locationHistory; // History of last rep
        private int repetitionCount;

        private enum change {neutral, decreasing, increasing}

        public workoutLogger() {
            locationHistory = new LinkedList<RectF>();
            repetitionCount = 0;
        }

        private void resetHistory() {
            locationHistory.clear();
        }

        public int addLocationAndEvaluateTotalReps(RectF newLocation) {
            // Reset history if nothing has happened for 50 iterations
            if (locationHistory.size() > 50) {
                resetHistory();
            }
            // Add the new location
            locationHistory.add(newLocation);
            // Evaluate the History - History is reset if a rep is detected
            repetitionCount += evaluateHistoryForRep();
            return repetitionCount;
        }

        // Returns 0 if no rep found, otherwise returns 1 and deletes history
        private int evaluateHistoryForRep() {
            int no_deltas = locationHistory.size() - 1;
            int ans = 0;
            if (no_deltas >= 2) {
                LinkedList<change> deltaList = new LinkedList<change>();
                for (int i = 0; i < no_deltas; i++) {
                    deltaList.add(deltaChange(locationHistory.get(i).width(), locationHistory.get(i + 1).width()));
                }
                Log.d("LIST", deltaList.toString());
                int firstDecreasePosition = 0;
                int firstIncreasePosition = 0;
                for (int i = 0; i < deltaList.size(); i++) {
                    switch (deltaList.get(i)) {
                        case decreasing:
                            firstDecreasePosition = i;
                            break;
                        case increasing:
                            firstIncreasePosition = i;
                            break;
                    }
                }
                if (firstIncreasePosition > firstDecreasePosition) {
                    ans = 1;
                    resetHistory();
                }
            } else {
                ans = 0;
            }
            return ans;
        }

        private change deltaChange(float val1, float val2) {
            float percentageChange = ((val1 - val2) / val2) * 100;
            if (percentageChange > 20) {
                return change.increasing;
            } else if (percentageChange < -20) {
                return change.decreasing;
            } else {
                return change.neutral;
            }
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        LOGGER.d("onCreate " + this);
        super.onCreate(null);
        setContentView(R.layout.activity_camera);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        final Intent intent = getIntent();
        myWorkoutLogger = new workoutLogger();
        final Bundle workoutDataBundle = intent.getBundleExtra("workoutDataBundle");
        noRepsTV = findViewById(R.id.activity_camera_reps_tv);
        final int repTarget = Integer.parseInt(workoutDataBundle.getString("repTarget"));
        noRepsTV.setText("Rep 0/" + repTarget);
        noSetsTV = findViewById(R.id.activity_camera_sets_tv);
        final int setTarget = Integer.parseInt(workoutDataBundle.getString("setTarget"));
        currentSet = intent.getIntExtra("currentSet", 1);
        noSetsTV.setText("Set " + String.valueOf(currentSet) + "/" + setTarget);
        workoutType = workoutDataBundle.getString("workoutType");
        workoutHeading = findViewById(R.id.activity_camera_heading_tv);
        workoutHeading.setText(workoutDataBundle.getString("workoutName"));
        if (workoutDataBundle.getString("timeTargetMode").equals("Time Target Mode")) {
            restBetween = findViewById(R.id.activity_camera_rest_between_tv);
            restBetween.setText("Rest between: " + workoutDataBundle.getString("restBetween") + "s");
            setTargetTime = findViewById(R.id.activity_camera_target_time_tv);
            setTargetTime.setText("Target time: " + workoutDataBundle.getString("targetTime") + "s");
        }
        // Initialize a Handler that updates the current workout status
        tvHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                int noReps = Integer.parseInt(bundle.getString("reps")); // the reps done in this workout iteration so far (restart counting on new set)
                int setFinished = noReps / repTarget; // this is 0 until a set has been finished and it becomes 1
                if (!("Rep " + noReps + "/" + repTarget).equals(String.valueOf(noRepsTV.getText()))) { // if a new rep has been performed, i.e. noReps changed
                    if (setFinished == 1) { // if a new set is initialized
                        String setFinishedText = "Set number " + (currentSet) + " finished.";
                        noReps = repTarget;
                        t1.speak(String.valueOf(noReps), TextToSpeech.QUEUE_ADD, null, null);
                        t1.speak(setFinishedText, TextToSpeech.QUEUE_ADD, null, null);
                        Intent timerRedirectIntent = new Intent(getApplicationContext(), TimerRedirectActivity.class);
                        timerRedirectIntent.putExtra("workoutDataBundle", workoutDataBundle);
                        timerRedirectIntent.putExtra("timerHeading", "Get ready for next set!");
                        timerRedirectIntent.putExtra("timerStartValue", Integer.parseInt(workoutDataBundle.getString("restBetween")));
                        timerRedirectIntent.putExtra("currentSet", (currentSet+1));
                        startActivity(timerRedirectIntent);
                        finish();
                    } else {
                        t1.speak(String.valueOf(noReps), TextToSpeech.QUEUE_ADD, null, null);
                    }
                }
                noRepsTV.setText("Rep " + noReps + "/" + repTarget);
                noSetsTV.setText("Set " + currentSet + "/" + setTarget);
            }
        };
        if (hasPermission()) {
            setFragment();
        } else {
            requestPermission();
        }
        // Initialize the TextToSpeech Engine
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                    String speechOutputText = "";
                    if (currentSet==1){
                        speechOutputText = "Begin " + workoutType + " workout. Set " + (currentSet) + " of " + setTarget + " of " + workoutDataBundle.getString("workoutName");
                    }
                    else {
                        speechOutputText = "Continue " + workoutType + " workout. Set " + (currentSet) + " of " + setTarget + " of " + workoutDataBundle.getString("workoutName");
                    }
                    t1.speak(speechOutputText, TextToSpeech.QUEUE_FLUSH, null, null);
                }
            }
        });
    }

    private byte[] lastPreviewFrame;

    protected int[] getRgbBytes() {
        imageConverter.run();
        return rgbBytes;
    }

    protected int getLuminanceStride() {
        return yRowStride;
    }

    protected byte[] getLuminance() {
        return yuvBytes[0];
    }

    /**
     * Callback for android.hardware.Camera API
     */
    @Override
    public void onPreviewFrame(final byte[] bytes, final Camera camera) {
        if (isProcessingFrame) {
            LOGGER.w("Dropping frame!");
            return;
        }

        try {
            // Initialize the storage bitmaps once when the resolution is known.
            if (rgbBytes == null) {
                Camera.Size previewSize = camera.getParameters().getPreviewSize();
                previewHeight = previewSize.height;
                previewWidth = previewSize.width;
                rgbBytes = new int[previewWidth * previewHeight];
                onPreviewSizeChosen(new Size(previewSize.width, previewSize.height), 90);
            }
        } catch (final Exception e) {
            LOGGER.e(e, "Exception!");
            return;
        }

        isProcessingFrame = true;
        lastPreviewFrame = bytes;
        yuvBytes[0] = bytes;
        yRowStride = previewWidth;

        imageConverter =
                new Runnable() {
                    @Override
                    public void run() {
                        ImageUtils.convertYUV420SPToARGB8888(bytes, previewWidth, previewHeight, rgbBytes);
                    }
                };

        postInferenceCallback =
                new Runnable() {
                    @Override
                    public void run() {
                        camera.addCallbackBuffer(bytes);
                        isProcessingFrame = false;
                    }
                };
        processImage();
    }

    /**
     * Callback for Camera2 API
     */
    @Override
    public void onImageAvailable(final ImageReader reader) {
        //We need wait until we have some size from onPreviewSizeChosen
        if (previewWidth == 0 || previewHeight == 0) {
            return;
        }
        if (rgbBytes == null) {
            rgbBytes = new int[previewWidth * previewHeight];
        }
        try {
            final Image image = reader.acquireLatestImage();

            if (image == null) {
                return;
            }

            if (isProcessingFrame) {
                image.close();
                return;
            }
            isProcessingFrame = true;
            Trace.beginSection("imageAvailable");
            final Plane[] planes = image.getPlanes();
            fillBytes(planes, yuvBytes);
            yRowStride = planes[0].getRowStride();
            final int uvRowStride = planes[1].getRowStride();
            final int uvPixelStride = planes[1].getPixelStride();

            imageConverter =
                    new Runnable() {
                        @Override
                        public void run() {
                            ImageUtils.convertYUV420ToARGB8888(
                                    yuvBytes[0],
                                    yuvBytes[1],
                                    yuvBytes[2],
                                    previewWidth,
                                    previewHeight,
                                    yRowStride,
                                    uvRowStride,
                                    uvPixelStride,
                                    rgbBytes);
                        }
                    };

            postInferenceCallback =
                    new Runnable() {
                        @Override
                        public void run() {
                            image.close();
                            isProcessingFrame = false;
                        }
                    };

            processImage();
        } catch (final Exception e) {
            LOGGER.e(e, "Exception!");
            Trace.endSection();
            return;
        }
        Trace.endSection();
    }

    @Override
    public synchronized void onStart() {
        LOGGER.d("onStart " + this);
        super.onStart();
    }

    @Override
    public synchronized void onResume() {
        LOGGER.d("onResume " + this);
        super.onResume();

        handlerThread = new HandlerThread("inference");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    @Override
    public synchronized void onPause() {
        LOGGER.d("onPause " + this);

        if (!isFinishing()) {
            LOGGER.d("Requesting finish");
            finish();
        }

        handlerThread.quitSafely();
        try {
            handlerThread.join();
            handlerThread = null;
            handler = null;
        } catch (final InterruptedException e) {
            LOGGER.e(e, "Exception!");
        }

        super.onPause();
    }

    @Override
    public synchronized void onStop() {
        LOGGER.d("onStop " + this);
        super.onStop();
    }

    @Override
    public synchronized void onDestroy() {
        LOGGER.d("onDestroy " + this);
        super.onDestroy();
    }

    protected synchronized void runInBackground(final Runnable r) {
        if (handler != null) {
            handler.post(r);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            final int requestCode, final String[] permissions, final int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                setFragment();
            } else {
                requestPermission();
            }
        }
    }

    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(PERMISSION_STORAGE) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(PERMISSION_CAMERA) ||
                    shouldShowRequestPermissionRationale(PERMISSION_STORAGE)) {
                Toast.makeText(CameraActivity.this,
                        "Camera AND storage permission are required for this demo", Toast.LENGTH_LONG).show();
            }
            requestPermissions(new String[]{PERMISSION_CAMERA, PERMISSION_STORAGE}, PERMISSIONS_REQUEST);
        }
    }

    // Returns true if the device supports the required hardware level, or better.
    private boolean isHardwareLevelSupported(
            CameraCharacteristics characteristics, int requiredLevel) {
        int deviceLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
        if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
            return requiredLevel == deviceLevel;
        }
        // deviceLevel is not LEGACY, can use numerical sort
        return requiredLevel <= deviceLevel;
    }

    private String chooseCamera() {
        final CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            for (final String cameraId : manager.getCameraIdList()) {
                final CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

                // We don't use a front facing camera in this sample.
                final Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK) {
                    continue;
                }

                final StreamConfigurationMap map =
                        characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                if (map == null) {
                    continue;
                }

                // Fallback to camera1 API for internal cameras that don't have full support.
                // This should help with legacy situations where using the camera2 API causes
                // distorted or otherwise broken previews.
                useCamera2API = (facing == CameraCharacteristics.LENS_FACING_FRONT)
                        || isHardwareLevelSupported(characteristics,
                        CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL);
                LOGGER.i("Camera API lv2?: %s", useCamera2API);
                return cameraId;
            }
        } catch (CameraAccessException e) {
            LOGGER.e(e, "Not allowed to access camera");
        }

        return null;
    }

    protected void setFragment() {
        String cameraId = chooseCamera();
        if (cameraId == null) {
            Toast.makeText(this, "No Camera Detected", Toast.LENGTH_SHORT).show();
            finish();
        }

        Fragment fragment;
        if (useCamera2API) {
            com.example.android.xrcs.tensorflow.CameraConnectionFragment camera2Fragment =
                    com.example.android.xrcs.tensorflow.CameraConnectionFragment.newInstance(
                            new com.example.android.xrcs.tensorflow.CameraConnectionFragment.ConnectionCallback() {
                                @Override
                                public void onPreviewSizeChosen(final Size size, final int rotation) {
                                    previewHeight = size.getHeight();
                                    previewWidth = size.getWidth();
                                    CameraActivity.this.onPreviewSizeChosen(size, rotation);
                                }
                            },
                            this,
                            getLayoutId(),
                            getDesiredPreviewFrameSize());

            camera2Fragment.setCamera(cameraId);
            fragment = camera2Fragment;
        } else {
            fragment =
                    new com.example.android.xrcs.tensorflow.LegacyCameraConnectionFragment(this, getLayoutId(), getDesiredPreviewFrameSize());
        }

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    protected void fillBytes(final Plane[] planes, final byte[][] yuvBytes) {
        // Because of the variable row stride it's not possible to know in
        // advance the actual necessary dimensions of the yuv planes.
        for (int i = 0; i < planes.length; ++i) {
            final ByteBuffer buffer = planes[i].getBuffer();
            if (yuvBytes[i] == null) {
                LOGGER.d("Initializing buffer %d at size %d", i, buffer.capacity());
                yuvBytes[i] = new byte[buffer.capacity()];
            }
            buffer.get(yuvBytes[i]);
        }
    }

/*    public boolean isDebug() {
        return debug;
    }

    public void requestRender() {
        final com.example.android.xrcs.tensorflow.OverlayView overlay = (com.example.android.xrcs.tensorflow.OverlayView) findViewById(R.id.debug_overlay);
        if (overlay != null) {
            overlay.postInvalidate();
        }
    }

    public void addCallback(final com.example.android.xrcs.tensorflow.OverlayView.DrawCallback callback) {
        final com.example.android.xrcs.tensorflow.OverlayView overlay = (com.example.android.xrcs.tensorflow.OverlayView) findViewById(R.id.debug_overlay);
        if (overlay != null) {
            overlay.addCallback(callback);
        }
    }

    public void onSetDebug(final boolean debug) {
    }

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP
                || keyCode == KeyEvent.KEYCODE_BUTTON_L1 || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            debug = !debug;
            requestRender();
            onSetDebug(debug);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }*/

    protected void readyForNextImage() {
        if (postInferenceCallback != null) {
            postInferenceCallback.run();
        }
    }

    protected int getScreenOrientation() {
        switch (getWindowManager().getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_270:
                return 270;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_90:
                return 90;
            default:
                return 0;
        }
    }

    protected abstract void processImage();

    protected abstract void onPreviewSizeChosen(final Size size, final int rotation);

    protected abstract int getLayoutId();

    protected abstract Size getDesiredPreviewFrameSize();
}
