package idv.logistic.logisticnote;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class TestScanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_scan);


        CameraManager manager = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                String cameraId = manager.getCameraIdList()[0];
                manager.setTorchMode(cameraId, true); // 打開燈光
            } catch (CameraAccessException e) {
                // 錯誤處理
                e.printStackTrace();
            }
        }

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan a QR Code");
        integrator.setCameraId(0);  // 使用後置攝像頭
        integrator.setBeepEnabled(true); // 關閉掃描音效
        integrator.setBarcodeImageEnabled(true); // 顯示掃描的圖像
        integrator.initiateScan();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            String content = result.getContents(); // 獲取二維碼的內容
            // 對掃描結果進行處理
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}