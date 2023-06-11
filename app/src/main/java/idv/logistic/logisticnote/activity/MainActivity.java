package idv.logistic.logisticnote.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import idv.logistic.logisticnote.R;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private Bitmap bitmap;
    private ImageView iv_qrcode;
    private Button btn_scan, btn_encode;
    private EditText et_encode;
    private TextView QRResult;
    ZXingScannerView scannerView;
    BarcodeEncoder barcodeEncoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_scan = findViewById(R.id.btn_scan);
        btn_encode = findViewById(R.id.btn_encode);
        iv_qrcode = findViewById(R.id.iv_qrcode);
        et_encode = findViewById(R.id.et_encode);
        QRResult = findViewById(R.id.txv_result);
        scannerView = findViewById(R.id.scannerView);

        QRResult.setTextColor(Color.RED);

        barcodeEncoder = new BarcodeEncoder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
        }
        //按下掃描按紐
        btn_scan.setOnClickListener(view -> {
            //判斷有沒有給CAMERA權限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    //跳是否允許相機權限視窗
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
                } else {
                    scannerView.startCamera();
                    scannerView.setResultHandler(this);
                }
            }
        });
        btn_encode.setOnClickListener(view -> {
            try {
                bitmap = barcodeEncoder.encodeBitmap(et_encode.getText().toString(), BarcodeFormat.QR_CODE, 200, 200);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            iv_qrcode.setImageBitmap(bitmap);
        });
    }

    @Override
    public void handleResult(Result result) {
        Log.d("Result", "" + result.getText());
        Toast.makeText(this, "code=" + result.getText(), Toast.LENGTH_LONG).show();
        QRResult.setText(result.getText());
        scannerView.stopCamera();
    }
}