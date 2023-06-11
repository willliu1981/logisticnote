package idv.logistic.logisticnote.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import idv.kw.utils.switchable.ViewUpdater;
import idv.kw.utils.switchable.barcode.BarcodeSwitcher;
import idv.kw.utils.switchable.barcode.FlashLightSwitcher;
import idv.logistic.logisticnote.R;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ScannerActivityOld extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    //private static final String HAS_LAST4 = "+4碼";
    //private static final String HAS_NO_LAST4 = "-4碼";
    private static final String LIGHT_ON = "開燈";
    private static final String LIGHT_OFF = "關燈";

    private static final int CAMERA_PERMISSION_REQUEST = 1;

    ZXingScannerView scannerView;
    private Button btn_confirm, btn_tail4code, btn_scan, btn_light, btn_backup;
    private TextView txtv_result, txtv_bkp1, txtv_bkp2, txtv_bkp3, txtv_bkp4;
    private EditText edtv_input;
    private SoundPool soundPool;
    private int soundId;
    private Vibrator vibrator;
    private TextView selectedBkpItem;

    private CameraManager cameraManager;

    BarcodeSwitcher barcodeSwitcher;
    FlashLightSwitcher flashLightSwitcher;

    private void openFlashlight(boolean isOpen) throws CameraAccessException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                String cameraId = cameraManager.getCameraIdList()[0];
                //cameraManager.setTorchMode(cameraId, isOpen); // 打開燈光
                scannerView.setFlash(isOpen);
            } catch (CameraAccessException e) {
                // 錯誤處理
                e.printStackTrace();
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);


        initComps();

        //恢復備分檔
        restoreBkpTextValue();

        flashLightSwitcher = new FlashLightSwitcher(this, new ViewUpdater() {
            @Override
            public void updateText(Object o, boolean isTrue) {
                try {
                    openFlashlight(isTrue);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                    Toast.makeText(ScannerActivityOld.this, "light err: " + e.getMessage(), Toast.LENGTH_LONG).show();
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


        startScan();

        //按下掃描按紐
        btn_scan.setOnClickListener(view -> {
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
                Toast.makeText(ScannerActivityOld.this, "code 格式錯誤", Toast.LENGTH_LONG).show();

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
    }

    private void initComps() {
        btn_scan = findViewById(R.id.scn_btn_scan);
        btn_confirm = findViewById(R.id.scn_btn_confirm);
        scannerView = findViewById(R.id.scn_sv_scanner);
        btn_tail4code = findViewById(R.id.scn_btn_tail4code);
        txtv_result = findViewById(R.id.scn_txtv_result);
        edtv_input = findViewById(R.id.scn_edtv_input);
        btn_light = findViewById(R.id.scn_btn_light);
        btn_backup = findViewById(R.id.scn_btn_backup);
        txtv_bkp1 = findViewById(R.id.scn_txtv_bkp_1);
        txtv_bkp2 = findViewById(R.id.scn_txtv_bkp_2);
        txtv_bkp3 = findViewById(R.id.scn_txtv_bkp_3);
        txtv_bkp4 = findViewById(R.id.scn_txtv_bkp_4);
    }


    @Override
    public void handleResult(Result result) {
        scannerView.stopCamera();

        txtv_result.setText(result.getText());
        barcodeSwitcher.refresh();


        // 播放音效
        soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);

        // 震動
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(50);
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 釋放 SoundPool 資源
        soundPool.release();

        // 停止震動
        vibrator.cancel();


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        }


    }
}