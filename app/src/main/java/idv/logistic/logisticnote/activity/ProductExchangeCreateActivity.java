package idv.logistic.logisticnote.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.InputFilter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Locale;

import idv.logistic.logisticnote.R;
import idv.logistic.logisticnote.database.ResultTank;
import idv.kw.tool.DateTimeUtil;
import idv.logistic.logisticnote.dao.product.ProductExchangeDao;
import idv.logistic.logisticnote.model.ProductExchange;

public class ProductExchangeCreateActivity extends AppCompatActivity {
    final static String QUERY_INPUTPNO = "PNO";
    final static String QUERY_SELECTEDPID = "PID";
    final static String QUERY_NONE = "NONE";
    public final static int SCANNER_ACTIVITY_CODE_FOR_PNO = 101;
    public final static int SCANNER_ACTIVITY_CODE_FOR_FROM_SNO = 102;
    public final static int SCANNER_ACTIVITY_CODE_FOR_TO_SNO = 103;

    TextView txtv_title;
    TextView txtv_setupDate;
    TextView txtv_pno;
    TextView txtv_shelfFromForSetup;
    TextView txtv_shelfToForSetup;
    TextView txtv_shelfFromForUpdate;
    TextView txtv_shelfToForUpdate;
    TextView txtv_saveStatus;
    EditText edtv_tag;
    Button btn_update;
    Button btn_setup;
    Button btn_copySNOTo;
    Button btn_tag_labeled, btn_tag_check, btn_tag_ok, btn_tag_search;
    CheckBox ckb_upload;
    ProductExchangeDao dao;
    SharedPreferences prefs_listItem;
    View view_separator;
    int seletctedListIdx;

    private Vibrator vibrator;

    private int seletctedId;
    private String queryType = QUERY_NONE;

    final ActivityResultLauncher<Intent> PNOScannerlauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                if (result.getResultCode() == RESULT_OK && data != null) {
                    txtv_pno.setText(data.getStringExtra("no"));
                    queryType = QUERY_INPUTPNO;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                        fillDataFromDaoToTextByNO(txtv_pno.getText().toString().toUpperCase(Locale.ROOT));
                    }
                    setSaveStatus("", false);
                }
            }
    );

    final ActivityResultLauncher<Intent> SNOForFromForSetupScannerlauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                if (result.getResultCode() == RESULT_OK && data != null) {
                    txtv_shelfFromForSetup.setText(data.getStringExtra("no"));
                    setSaveStatus("", false);
                }
            }
    );

    final ActivityResultLauncher<Intent> SNOForToForSetupScannerlauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                if (result.getResultCode() == RESULT_OK && data != null) {
                    txtv_shelfToForSetup.setText(data.getStringExtra("no"));
                    setSaveStatus("", false);
                }
            }
    );


    final ActivityResultLauncher<Intent> SNOForFromForUpdateScannerlauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                if (result.getResultCode() == RESULT_OK && data != null) {
                    txtv_shelfFromForUpdate.setText(data.getStringExtra("no"));
                }
            }
    );

    final ActivityResultLauncher<Intent> SNOForToForUpdatScannerlauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                if (result.getResultCode() == RESULT_OK && data != null) {
                    txtv_shelfToForUpdate.setText(data.getStringExtra("no"));
                }
            }
    );

    class ScannerNoOnListener implements View.OnClickListener {
        private ActivityResultLauncher<Intent> launcher;

        public ScannerNoOnListener(ActivityResultLauncher<Intent> launcher) {
            this.launcher = launcher;
        }

        @Override
        public void onClick(View view) {
            Intent intent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                intent = new Intent(ProductExchangeCreateActivity.this, ScannerActivity2.class);
            }
            TextView txtView = (TextView) view;
            intent.putExtra("no", txtView.getText().toString());
            launcher.launch(intent);
        }
    }

    private void setSaveStatus(String value, boolean isSaved) {
        if (isSaved) {
            txtv_saveStatus.setText(value + " 已儲存");

        } else {
            txtv_saveStatus.setText("");
        }
    }


    //xxx init
    private void initComps() {
        txtv_title = findViewById(R.id.pe_txtv_title);
        txtv_shelfFromForUpdate = findViewById(R.id.pe_txtv_shelf_from_for_update);
        txtv_shelfToForUpdate = findViewById(R.id.pe_txtv_shelf_to_for_update);
        txtv_shelfFromForSetup = findViewById(R.id.pe_txtv_shelf_from_for_setup);
        txtv_shelfToForSetup = findViewById(R.id.pe_txtv_shelf_to_for_setup);
        txtv_setupDate = findViewById(R.id.pe_txtv_setupDate);
        txtv_pno = findViewById(R.id.pe_txtv_pno);
        btn_update = findViewById(R.id.pe_btn_update);
        btn_setup = findViewById(R.id.pe_btn_exchange);
        btn_copySNOTo = findViewById(R.id.pe_btn_copy_to);
        txtv_saveStatus = findViewById(R.id.pe_txtv_save_stutus);
        btn_tag_labeled = findViewById(R.id.pe_btn_tag_labeled);
        btn_tag_check = findViewById(R.id.pe_btn_tag_check);
        btn_tag_ok = findViewById(R.id.pe_btn_tag_ok);
        btn_tag_search = findViewById(R.id.pe_btn_tag_search);
        edtv_tag = findViewById(R.id.pe_edtv_tag);
        ckb_upload = findViewById(R.id.pe_ckb_upload);
        view_separator = findViewById(R.id.pe_view_separator);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = this.getIntent();


        ProductExchangeActivity._this.callShowList(seletctedListIdx);

        /*
        Intent intent = this.getIntent();
        String seletctedIdx = intent.getStringExtra("seletctedIdx");

        if (seletctedIdx != null) {
            Intent newIntent = new Intent();
            intent.putExtra("seletctedIdx", seletctedIdx);
            setResult(Activity.RESULT_OK, newIntent);
            finish();
        }

         */


        return;
    }


    //xxx oc
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_exchange_create);

        initComps();
        setAllCap();

        //other init
        prefs_listItem = getSharedPreferences("list_prefs", MODE_PRIVATE);

        dao = new ProductExchangeDao();

        // 初始化 Vibrator
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        Intent intent = this.getIntent();
        seletctedId = intent.getIntExtra("seletctedId", -1);
        seletctedListIdx = intent.getIntExtra("seletctedIdx", -1);
        if (seletctedId != -1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                queryType = QUERY_SELECTEDPID;
                fillDataFromDaoToTextById(seletctedId);//選擇list pid

            }
        } else {
            disableEidtView();
        }


        txtv_pno.setOnClickListener(new ScannerNoOnListener(PNOScannerlauncher));
        txtv_shelfFromForSetup.setOnClickListener(new ScannerNoOnListener(SNOForFromForSetupScannerlauncher));
        txtv_shelfToForSetup.setOnClickListener(new ScannerNoOnListener(SNOForToForSetupScannerlauncher));
        txtv_shelfFromForUpdate.setOnClickListener(new ScannerNoOnListener(SNOForFromForUpdateScannerlauncher));
        txtv_shelfToForUpdate.setOnClickListener(new ScannerNoOnListener(SNOForToForUpdatScannerlauncher));


        txtv_pno.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                clip(((TextView) view).getText().toString());

                return true;
            }
        });

        txtv_shelfFromForSetup.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                clip(((TextView) view).getText().toString());
                return true;
            }
        });

        txtv_shelfToForSetup.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                clip(((TextView) view).getText().toString());
                return true;
            }
        });

        txtv_shelfFromForUpdate.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                clip(((TextView) view).getText().toString());
                return true;
            }
        });

        txtv_shelfToForUpdate.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                clip(((TextView) view).getText().toString());
                return true;
            }
        });

//*
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ProductExchangeCreateActivity.this.seletctedId != -1) {
                    ProductExchange p = dao.get(ProductExchangeCreateActivity.this.seletctedId);
                    p.setNo(txtv_pno.getText().toString());
                    p.setShelfNoFrom(txtv_shelfFromForUpdate.getText().toString());
                    p.setShelfNoTo(txtv_shelfToForUpdate.getText().toString());
                    p.setUpdate_date(Timestamp.valueOf(DateTimeUtil.formatLocalToGMT(new Date())));
                    dao.update(p, ProductExchangeCreateActivity.this.seletctedId);

                    SharedPreferences.Editor editor = prefs_listItem.edit();
                    editor.putString("item_tag_" + p.getId(), edtv_tag.getText().toString());
                    editor.putBoolean("item_upload_" + p.getId(), ckb_upload.isChecked());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                        editor.apply();
                    }

                    Toast.makeText(ProductExchangeCreateActivity.this, "已更新資料", Toast.LENGTH_SHORT).show();
                }
            }

        });

        //*/

        btn_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProductExchange p = new ProductExchange();
                p.setNo(txtv_pno.getText().toString());
                p.setShelfNoFrom(txtv_shelfFromForSetup.getText().toString());
                p.setShelfNoTo(txtv_shelfToForSetup.getText().toString());
                p.setSetup_date(Timestamp.valueOf(DateTimeUtil.formatLocalToGMT(new Date())));
                p.setUpdate_date(Timestamp.valueOf(DateTimeUtil.formatLocalToGMT(new Date())));
                dao.add(p);
                Toast.makeText(ProductExchangeCreateActivity.this, "已新增資料", Toast.LENGTH_SHORT).show();
                setSaveStatus(p.getNo(), true);

                queryType = QUERY_INPUTPNO;
                seletctedListIdx = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    fillDataFromDaoToTextByNO(txtv_pno.getText().toString().toUpperCase(Locale.ROOT));
                }
            }
        });


        btn_copySNOTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtv_shelfFromForSetup.setText(txtv_shelfFromForUpdate.getText().toString());
                txtv_shelfToForSetup.setText(txtv_shelfToForUpdate.getText().toString());
                setSaveStatus("", false);
            }
        });

        btn_tag_labeled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                /*
                txtv_shelfToForSetup.setText(b.getText().toString());
                txtv_shelfFromForSetup.setText("");
                txtv_pno.setText("");
                ckb_upload.setChecked(false);
                setSaveStatus("", false);

                 */

                exitForSetSign(b.getText().toString());
            }
        });

        btn_tag_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                /*
                txtv_shelfToForSetup.setText(b.getText().toString());
                txtv_shelfFromForSetup.setText("");
                txtv_pno.setText("");
                ckb_upload.setChecked(false);
                setSaveStatus("", false);

                 */
                exitForSetSign(b.getText().toString());
            }
        });

        btn_tag_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                /*
                txtv_shelfToForSetup.setText(b.getText().toString());
                txtv_shelfFromForSetup.setText("");
                txtv_pno.setText("");
                ckb_upload.setChecked(false);
                setSaveStatus("", false);

                // */
                exitForSetSign(b.getText().toString());
            }
        });

        btn_tag_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Button b = (Button) view;
                txtv_shelfToForSetup.setText(b.getText().toString());
                ckb_upload.setChecked(false);
                setSaveStatus("", false);

                 */
                exitForSearch();
            }
        });


    }

    private void exitForSetSign(String sign) {
        ProductExchange p = new ProductExchange();
        p.setNo("");
        p.setShelfNoFrom("");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            p.setShelfNoTo(sign.toUpperCase(Locale.ROOT));
        }
        p.setSetup_date(Timestamp.valueOf(DateTimeUtil.formatLocalToGMT(new Date())));
        p.setUpdate_date(Timestamp.valueOf(DateTimeUtil.formatLocalToGMT(new Date())));
        dao.add(p);
        Toast.makeText(ProductExchangeCreateActivity.this, "已新增符號", Toast.LENGTH_SHORT).show();
        ProductExchangeActivity._this.callShowList();
        //finish();
        onBackPressed();
    }

    private void exitForSearch() {
        ProductExchange p = new ProductExchange();
        p.setNo(txtv_pno.getText().toString());
        p.setShelfNoFrom(txtv_shelfFromForSetup.getText().toString());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            p.setShelfNoTo(btn_tag_search.getText().toString().toUpperCase(Locale.ROOT));
        }
        p.setSetup_date(Timestamp.valueOf(DateTimeUtil.formatLocalToGMT(new Date())));
        p.setUpdate_date(Timestamp.valueOf(DateTimeUtil.formatLocalToGMT(new Date())));
        dao.add(p);
        Toast.makeText(ProductExchangeCreateActivity.this, "已新增搜索", Toast.LENGTH_SHORT).show();
        ProductExchangeActivity._this.callShowList();
        //finish();
        onBackPressed();
    }

    private void clip(String content) {
        // 取得要複製的文字內容
        String textToCopy = content;

        ClipboardManager clipboardManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            // 取得剪貼簿管理員
            clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

            // 建立 ClipData 物件，將要複製的文字內容設定到 ClipData 中
            ClipData clipData = ClipData.newPlainText("label", textToCopy);

            // 將 ClipData 物件放到剪貼簿中
            clipboardManager.setPrimaryClip(clipData);

            Toast.makeText(ProductExchangeCreateActivity.this, "已複製內容", Toast.LENGTH_SHORT).show();

            // 震動
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(50);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*
        if (requestCode == FOR_SCANNER_ACTIVITY_CODE && resultCode == RESULT_OK) {
            String result = data.getStringExtra("pno");
            Toast.makeText(ProductExchangeCreateActivity.this, "code= " + result, Toast.LENGTH_LONG).show();
            txtv_pno.setText(result);
        }

         //*/
    }


    private void fillDataFromDaoToTextById(int pid) {
        fillDataFromDaoToText(pid, QUERY_SELECTEDPID);
    }

    private void fillDataFromDaoToTextByNO(String pno) {
        fillDataFromDaoToText(pno, QUERY_INPUTPNO);
    }

    private void fillDataFromDaoToText(Object query, String queryType) {
        ResultTank<ProductExchange> tank = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
            switch (queryType) {
                case QUERY_SELECTEDPID://from edit
                    tank = dao.getTankByID((Integer) query);
                    txtv_title.setText("庫移紀錄(EDIT)");
                    enableEditView();
                    break;
                case QUERY_INPUTPNO://this input
                    tank = dao.getTankByNo((String) query);
                    seletctedListIdx = 0;
                    if (!tank.isEmpty()) {//but can be edit
                        txtv_title.setText("庫移紀錄(EDIT)");
                        enableEditView();
                    } else {// can't be edit
                        txtv_title.setText("庫移紀錄");
                        disableEidtView();

                    }
                    break;
                default:
            }
        } else {
            Toast.makeText(this, "無法取得資料,SDK版本需要9以上", Toast.LENGTH_LONG);
        }


        if (!tank.isEmpty()) {

            ProductExchange pe = tank.getLatest();


            if (this.queryType != null && this.queryType.equalsIgnoreCase(QUERY_SELECTEDPID)) {
                txtv_pno.setText(pe.getNo());
                txtv_shelfFromForSetup.setText(pe.getShelfNoFrom());
                txtv_shelfToForSetup.setText(pe.getShelfNoTo());
            }
            txtv_setupDate.setText(DateTimeUtil.formatGMTtoLocal(new Date(pe.getSetup_date().getTime())));
            txtv_shelfFromForUpdate.setText(pe.getShelfNoFrom());
            txtv_shelfToForUpdate.setText(pe.getShelfNoTo());

            String tag = prefs_listItem.getString("item_tag_" + pe.getId(), "");
            edtv_tag.setText(tag);
            boolean isUpload = prefs_listItem.getBoolean("item_upload_" + pe.getId(), false);
            ckb_upload.setChecked(isUpload);

            ProductExchangeCreateActivity.this.seletctedId = pe.getId();
        } else {
            txtv_setupDate.setText("");
            txtv_shelfFromForUpdate.setText("");
            txtv_shelfToForUpdate.setText("");
            edtv_tag.setText("");
            ckb_upload.setChecked(false);
            //edtv_ShelfFromForSetup.setText("");
            //edtv_ShelfToForSetup.setText("");
            ProductExchangeCreateActivity.this.seletctedId = -1;
        }
        this.queryType = QUERY_INPUTPNO;
    }

    private void disableEidtView() {
        txtv_shelfFromForUpdate.setVisibility(View.GONE);
        txtv_shelfToForUpdate.setVisibility(View.GONE);
        edtv_tag.setVisibility(View.GONE);
        ckb_upload.setVisibility(View.GONE);
        btn_update.setVisibility(View.GONE);
        txtv_title.setTextColor(Color.GRAY);
        view_separator.setBackgroundColor(Color.GRAY);
    }

    private void enableEditView() {
        txtv_shelfFromForUpdate.setVisibility(View.VISIBLE);
        txtv_shelfToForUpdate.setVisibility(View.VISIBLE);
        edtv_tag.setVisibility(View.VISIBLE);
        ckb_upload.setVisibility(View.VISIBLE);
        btn_update.setVisibility(View.VISIBLE);
        txtv_title.setTextColor(Color.parseColor("#EA0000"));
        view_separator.setBackgroundColor(Color.parseColor("#EA0000"));
    }


    private void setAllCap() {
        txtv_pno.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        txtv_shelfFromForUpdate.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        txtv_shelfToForUpdate.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        txtv_shelfFromForSetup.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        txtv_shelfToForSetup.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
    }
}