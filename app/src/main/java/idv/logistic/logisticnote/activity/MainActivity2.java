package idv.logistic.logisticnote.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

import idv.logistic.logisticnote.R;

public class MainActivity2 extends AppCompatActivity {
    /* oc

     */

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final String TAG = "BarcodeScannerActivity";

    private CameraSource cameraSource;
    BarcodeDetector barcodeDetector;
    SurfaceView surfaceView;

    Button btn_startScan;

    //xxx oc
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //test start
        System.out.println("xxx test");

        int n = 0;
        ArrayList<Integer> list = new ArrayList<>();
        Random random = new Random();
        TreeMap<Integer, Integer> groupMap = new TreeMap<>();
        for (int i = 1; i <= 200; ++i) {
            n = random.nextInt(40) + 1;
            list.add(n);
        }

        /* insert code here */
        for (Integer i:list){
            Integer count=groupMap.get(i);
            if(count==null){
                count=0;
            }
            groupMap.put(i,count+1);
        }


        System.out.println(groupMap);


        //test end


        surfaceView = findViewById(R.id.ts_surfaceView);
        btn_startScan = findViewById(R.id.ts_btn_sacn);


        btn_startScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                if (cameraSource != null) {
                    cameraSource.stop();
                    if (ActivityCompat.checkSelfPermission(MainActivity2.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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
                        cameraSource.start(surfaceView.getHolder());


                        // 準備開啟相機
                        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext());
                        if (code != ConnectionResult.SUCCESS) {
                            GoogleApiAvailability.getInstance().getErrorDialog(MainActivity2.this, code, 1).show();
                        } else {
                            try {
                                cameraSource.start();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // 檢查相機權限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
            return;
        }

        // 創建條碼探測器
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        // 創建相機源
        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setAutoFocusEnabled(true)
                .build();

        // 設置探測器監聽器
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() > 0) {
                    // 找到條碼，處理結果
                    final String barcodeValue = barcodes.valueAt(0).displayValue;
                    Log.d(TAG, "Barcode value: " + barcodeValue);

                    // 將結果顯示在 Toast 中
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity2.this,
                                    "Barcode value: " + barcodeValue, Toast.LENGTH_SHORT).show();
                            cameraSource.stop();
                        }
                    });
                }
            }
        });




    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraSource.release();
        barcodeDetector.release();
    }


}
