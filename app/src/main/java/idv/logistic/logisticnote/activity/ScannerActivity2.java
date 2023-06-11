package idv.logistic.logisticnote.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import idv.kw.utils.switchable.ViewUpdater;
import idv.kw.utils.switchable.barcode.BarcodeSwitcher;
import idv.kw.utils.switchable.barcode.FlashLightSwitcher;
import idv.logistic.logisticnote.R;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ScannerActivity2 extends AppCompatActivity {

    //private static final String HAS_LAST4 = "+4碼";
    //private static final String HAS_NO_LAST4 = "-4碼";
    private static final String LIGHT_ON = "開燈";
    private static final String LIGHT_OFF = "關燈";

    private static final int CAMERA_PERMISSION_REQUEST = 1;

    private Button btn_confirm, btn_tail4code, btn_scan, btn_light, btn_backup, btn_paste;
    private TextView txtv_result, txtv_bkp1, txtv_bkp2, txtv_bkp3, txtv_bkp4;
    private TextView txtv_snoRecnod;
    private EditText edtv_input;
    private SoundPool soundPool;
    private int soundId;
    private Vibrator vibrator;
    private TextView selectedBkpItem;

    private CameraManager cameraManager;

    private CameraSource cameraSource;
    BarcodeDetector barcodeDetector;
    SurfaceView scannerView2;

    BarcodeSwitcher barcodeSwitcher;
    FlashLightSwitcher flashLightSwitcher;
    Handler scanHandler;
    Runnable runnable;
    Runnable run;

    int forScanCount;

    private static final long SCAN_DURATION_MS = 1000; // 控制掃描時間為1秒
    private static final long SCAN_INTERVAL_MS = 100; // 控制掃描間隔為100毫秒

    private Map<String, Integer> barcodeFrequencyMap = new HashMap<>(); // 用於記錄條碼出現頻率的映射
    private Camera camera;
    private Camera.Parameters params;

    private CameraDevice mCameraDevice;
    private CameraCaptureSession mCaptureSession;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private CaptureRequest mPreviewRequest;

    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            mCameraDevice = camera;
            startPreview();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            camera.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            camera.close();
            mCameraDevice = null;
        }
    };

    private void openFlashlight(boolean isOpen) throws CameraAccessException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                String cameraId = cameraManager.getCameraIdList()[0];
                //cameraManager.setTorchMode(cameraId, isOpen); // 打開燈光

                /*
                if (camera == null) {
                    camera = Camera.open();
                }
                if (params == null) {
                    params = camera.getParameters();
                }
                if (isOpen) {
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                } else {
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                }
                camera.setParameters(params);
                camera.startPreview();

                // */

            } catch (CameraAccessException e) {
                // 錯誤處理
                e.printStackTrace();

            }
        }
    }

    //xxx oc
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner2);


        initComps();


        requestCameraPermission();

        //恢復備分檔
        restoreBkpTextValue();

        flashLightSwitcher = new FlashLightSwitcher(this, new ViewUpdater() {
            @Override
            public void updateText(Object o, boolean isTrue) {
                try {
                    openFlashlight(isTrue);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                    Toast.makeText(ScannerActivity2.this, "light err: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                if (isTrue) {
                    btn_light.setText("FL OFF");
                } else {
                    btn_light.setText("FL ON");
                }
            }

            @Override
            public Object getData() {
                return null;
            }
        });


        barcodeSwitcher = new BarcodeSwitcher(this, new ViewUpdater<String>() {
            @Override
            public void updateText(String data, boolean isTrue) {
                edtv_input.setText(data);
                edtv_input.setSelection(edtv_input.getText().length());

                if (isTrue) {
                    btn_tail4code.setText("-4碼");
                } else {
                    btn_tail4code.setText("+4碼");
                }
            }

            @Override
            public String getData() {
                return txtv_result.getText().toString();
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        }


        flashLightSwitcher.refresh();


        Intent intent = this.getIntent();

        initScanSoundAndVibrator();
        String no = intent.getStringExtra("no");
        txtv_result.setText(no);
        barcodeSwitcher.refresh();

        // 創建條碼探測器
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();


        // 創建相機源
        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(640, 480)
                .setRequestedFps(30.0f)
                .setAutoFocusEnabled(true)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .build();


        // 設置探測器監聽器

        /*
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                // 如果條碼掃描結果不為空
                if (barcodes.size() > 0) {
                    // 找到條碼，處理結果
                    final String barcodeValue = barcodes.valueAt(0).displayValue;

                    // 將結果顯示在 Toast 中
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            // 記錄條碼出現頻率
                            if (barcodeFrequencyMap.containsKey(barcodeValue)) {
                                int currentFrequency = barcodeFrequencyMap.get(barcodeValue);
                                barcodeFrequencyMap.put(barcodeValue, currentFrequency + 1);
                            } else {
                                barcodeFrequencyMap.put(barcodeValue, 1);
                            }
                        }
                    });
                }
            }
        });

        // */

        //*
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() > 0) {
                    Barcode barcode = barcodes.valueAt(0);

                    // 找到條碼，處理結果
                    final String barcodeValue = barcode.displayValue;

                    // 將結果顯示在 Toast 中
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            if (forScanCount++ < 10) {
                                // 記錄條碼出現頻率
                                if (barcodeFrequencyMap.containsKey(barcodeValue)) {
                                    int currentFrequency = barcodeFrequencyMap.get(barcodeValue);
                                    barcodeFrequencyMap.put(barcodeValue, currentFrequency + 1);
                                } else {
                                    barcodeFrequencyMap.put(barcodeValue, 1);
                                }
                            } else {
                                String finalBarcodeValue = "";
                                int maxFrequency = 0;
                                for (Map.Entry<String, Integer> entry : barcodeFrequencyMap.entrySet()) {
                                    if (entry.getValue() > maxFrequency) {
                                        finalBarcodeValue = entry.getKey();
                                        maxFrequency = entry.getValue();
                                    }
                                }
                                handleBarcodeResult(finalBarcodeValue);

                            }

                        }
                    });
                }
            }
        });

        // */

        /*
        runnable = new Runnable() {
            @Override
            public void run() {
                // 在掃描結束後，選擇出現頻率最高的條碼作為最終結果值
                String finalBarcodeValue = "";
                int maxFrequency = 0;
                for (Map.Entry<String, Integer> entry : barcodeFrequencyMap.entrySet()) {
                    if (entry.getValue() > maxFrequency) {
                        finalBarcodeValue = entry.getKey();
                        maxFrequency = entry.getValue();
                    }
                }

                // 將最終結果顯示在TextView中
                final String displayText = finalBarcodeValue;

                 run=new Runnable() {
                    @Override
                    public void run() {
                        handleBarcodeResult(displayText);

                    }
                };

                runOnUiThread(run);

                // 清除barcodeFrequencyMap，以便下一次掃描
                barcodeFrequencyMap.clear();

                // 重新啟動掃描，繼續下一輪掃描
                scanHandler.postDelayed(this, SCAN_INTERVAL_MS);
            }
        };

        // */


        /*
        // 建立一個Handler用於控制掃描時間
        scanHandler = new Handler();
        scanHandler.postDelayed(runnable, SCAN_DURATION_MS);


//         */

        //按下掃描按紐
        btn_scan.setOnClickListener(view -> {
            //openCamera();
            startScan();


        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent();
                intent.putExtra("no", edtv_input.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        btn_tail4code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barcodeSwitcher.toggle();

            }
        });

        btn_light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flashLightSwitcher.toggle();


            }
        });

        txtv_bkp1.setOnLongClickListener(bkpOnLongClickListener);
        txtv_bkp2.setOnLongClickListener(bkpOnLongClickListener);
        txtv_bkp3.setOnLongClickListener(bkpOnLongClickListener);
        txtv_bkp4.setOnLongClickListener(bkpOnLongClickListener);

        txtv_bkp1.setOnClickListener(bkpOnClickListener);
        txtv_bkp2.setOnClickListener(bkpOnClickListener);
        txtv_bkp3.setOnClickListener(bkpOnClickListener);
        txtv_bkp4.setOnClickListener(bkpOnClickListener);

        btn_backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedBkpItem != null) {
                    if (selectedBkpItem == txtv_bkp1) {
                        ProductExchangeActivity.bkp1 = edtv_input.getText().toString();
                        selectedBkpItem.setText(ProductExchangeActivity.bkp1);
                    } else if (selectedBkpItem == txtv_bkp2) {
                        ProductExchangeActivity.bkp2 = edtv_input.getText().toString();
                        selectedBkpItem.setText(ProductExchangeActivity.bkp2);
                    } else if (selectedBkpItem == txtv_bkp3) {
                        ProductExchangeActivity.bkp3 = edtv_input.getText().toString();
                        selectedBkpItem.setText(ProductExchangeActivity.bkp3);
                    } else if (selectedBkpItem == txtv_bkp4) {
                        ProductExchangeActivity.bkp4 = edtv_input.getText().toString();
                        selectedBkpItem.setText(ProductExchangeActivity.bkp4);
                    }

                }
            }
        });

        //openCamera();


        btn_paste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboard.hasPrimaryClip() && clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                    String text = item.getText().toString();
                    edtv_input.setText(text);
                }


            }
        });

        txtv_snoRecnod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ScannerActivity2.this);
                builder.setTitle("Shelf NO record");

                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_shelf_no_record, null);
                builder.setView(dialogView);

                AlertDialog dialog_sno_record = builder.create();

                dialog_sno_record.show();

            }
        });

    }

    View.OnClickListener bkpOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            selectedBkpItem = (TextView) view;
            //reverseBKPBackgroundColor();
            //view.setBackgroundColor(Color.BLUE);
        }
    };

    View.OnLongClickListener bkpOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            txtv_result.setText(((TextView) view).getText().toString());
            barcodeSwitcher.refresh();
            return true;
        }
    };

    private void restoreBkpTextValue() {
        txtv_bkp1.setText(ProductExchangeActivity.bkp1);
        txtv_bkp2.setText(ProductExchangeActivity.bkp2);
        txtv_bkp3.setText(ProductExchangeActivity.bkp3);
        txtv_bkp4.setText(ProductExchangeActivity.bkp4);
    }

    private void reverseBKPBackgroundColor() {
        txtv_bkp1.setBackgroundColor(Color.TRANSPARENT);
        txtv_bkp2.setBackgroundColor(Color.TRANSPARENT);
        txtv_bkp3.setBackgroundColor(Color.TRANSPARENT);
        txtv_bkp4.setBackgroundColor(Color.TRANSPARENT);
    }


    public String processBarcode(String barcode, boolean hasLast4, boolean ingoreErr) {
        String result = null;


        if (barcode.length() == 13) {
            if (hasLast4) {
                return barcode; // 還原尾4碼

            } else {
                return barcode.substring(0, barcode.length() - 4); // 去除尾4碼

            }
        } else {
            if (!ingoreErr) {
                Toast.makeText(ScannerActivity2.this, "code 格式錯誤", Toast.LENGTH_LONG).show();

            }
        }
        return result;
    }

    private void initScanSoundAndVibrator() {
        // 初始化 SoundPool
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }

        // 載入音效檔案
        soundId = soundPool.load(this, R.raw.scan_beep, 1);


        // 初始化 Vibrator
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }


    private void startScan() {


        if (cameraSource != null) {
            cameraSource.stop();
            if (ActivityCompat.checkSelfPermission(ScannerActivity2.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            try {

                cameraSource.start(scannerView2.getHolder());


                // 準備開啟相機
                int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext());
                if (code != ConnectionResult.SUCCESS) {
                    GoogleApiAvailability.getInstance().getErrorDialog(ScannerActivity2.this, code, 1).show();
                } else {
                    try {
                        cameraSource.start(scannerView2.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                //openFlashlight();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /*
        //判斷有沒有給CAMERA權限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                //跳是否允許相機權限視窗
                requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
            } else {
                scannerView.startCamera();
                scannerView.setResultHandler(this);
            }
        }

        // */
    }

    private void stopScan() {
        if (cameraSource != null) {
            try {
                cameraSource.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void openCamera() {
        if (!isFlashSupported()) {
            return;
        }

        try {
            final String cameraId = cameraManager.getCameraIdList()[0];
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            cameraManager.openCamera(cameraId, mStateCallback, null);


        } catch (Exception e) {
            // handle exception
        }
    }


    private void openFlashlight() {
        Toast.makeText(ScannerActivity2.this, "cam lt start", Toast.LENGTH_SHORT).show();
        try {
            if (getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_CAMERA_FLASH)) {
                Camera cam = Camera.open();
                Camera.Parameters p = cam.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                cam.setParameters(p);
                //cam.startPreview();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ScannerActivity2.this, "Exception flashLightOn():"+e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

/*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            CameraManager camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            String cameraId = null;
            try {
                Toast.makeText(ScannerActivity2.this, "cam lt start", Toast.LENGTH_SHORT).show();
                cameraId = camManager.getCameraIdList()[0];
                camManager.setTorchMode(cameraId, true);   //Turn ON
                Toast.makeText(ScannerActivity2.this, "cam id=" + cameraId, Toast.LENGTH_SHORT).show();
            } catch (CameraAccessException e) {
                e.printStackTrace();
                Toast.makeText(ScannerActivity2.this, "cam lt err:" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

 */
    }

    private void startPreview() {
        try {
            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(scannerView2.getHolder().getSurface());
            mPreviewRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);

            mCameraDevice.createCaptureSession(Arrays.asList(scannerView2.getHolder().getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    mCaptureSession = session;
                    try {
                        mPreviewRequest = mPreviewRequestBuilder.build();
                        mCaptureSession.setRepeatingRequest(mPreviewRequest, null, null);
                    } catch (CameraAccessException e) {
                        // handle exception
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                    // handle failure
                }
            }, null);
        } catch (CameraAccessException e) {
            // handle exception
        }
    }

    private void stopPreview() {
        try {
            if (mCaptureSession != null) {
                mCaptureSession.stopRepeating();
                mCaptureSession.abortCaptures();
                mCaptureSession.close();
                mCaptureSession = null;
            }
            if (mCameraDevice != null) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            mPreviewRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);
            mPreviewRequest = mPreviewRequestBuilder.build();
            mCaptureSession.setRepeatingRequest(mPreviewRequest, null, null);
        } catch (CameraAccessException e) {
            // handle exception
        }
    }


    private boolean isFlashSupported() {
        try {
            final String cameraId = cameraManager.getCameraIdList()[0];
            final CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
            return characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
        } catch (Exception e) {
            return false;
        }
    }

    //xxx init
    private void initComps() {
        btn_scan = findViewById(R.id.scn_btn_scan);
        btn_confirm = findViewById(R.id.scn_btn_confirm);
        btn_tail4code = findViewById(R.id.scn_btn_tail4code);
        txtv_result = findViewById(R.id.scn_txtv_result);
        edtv_input = findViewById(R.id.scn_edtv_input);
        btn_light = findViewById(R.id.scn_btn_light);
        btn_backup = findViewById(R.id.scn_btn_backup);
        txtv_bkp1 = findViewById(R.id.scn_txtv_bkp_1);
        txtv_bkp2 = findViewById(R.id.scn_txtv_bkp_2);
        txtv_bkp3 = findViewById(R.id.scn_txtv_bkp_3);
        txtv_bkp4 = findViewById(R.id.scn_txtv_bkp_4);
        scannerView2 = findViewById(R.id.scn_sv_scanner2);
        btn_paste = findViewById(R.id.scn_btn_paste);
        txtv_snoRecnod = findViewById(R.id.scn_txtv_sno_recond);
    }


    private void handleBarcodeResult(String result) {

        txtv_result.setText(result);
        barcodeSwitcher.refresh();


        // 播放音效
        int playid = soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
        soundPool.setVolume(playid, 0.05f, 0.05f);

        // 震動
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(50);
        }

        // 停止扫描
        stopScan();
        //stopPreview();

        forScanCount = 0;
        // 清除barcodeFrequencyMap，以便下一次掃描
        barcodeFrequencyMap.clear();


        // 停止 run
        //stopHandler();

    }


    private void stopHandler() {
        scanHandler.removeCallbacks(runnable);
        scanHandler.removeCallbacks(run);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (cameraSource != null) {
            cameraSource.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 釋放 SoundPool 資源
        soundPool.release();

        // 停止震動
        vibrator.cancel();

        if (cameraSource != null) {
            cameraSource.release();
            cameraSource = null;
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        requestCameraPermission();


        startScan();
        //openCamera();

    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST);
        }
    }


}