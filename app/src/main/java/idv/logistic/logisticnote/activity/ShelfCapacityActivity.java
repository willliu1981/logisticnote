package idv.logistic.logisticnote.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.google.android.material.internal.TextWatcherAdapter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


import idv.logistic.logisticnote.R;
import idv.logistic.logisticnote.dao.product.ShelfDao;
import idv.logistic.logisticnote.model.Shelf;
import idv.logistic.logisticnote.model.Type;
import idv.kw.tool.DateTimeUtil;

public class ShelfCapacityActivity extends AppCompatActivity {

    Boolean PermissionGranted = false;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    public void verifyStoragePermissions(Activity activity, String pm) {
        int permission = ActivityCompat.checkSelfPermission(activity, pm);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        } else {
            PermissionGranted = true;
        }
    }


    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String permissions[],
            int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            PermissionGranted = true;
            System.out.println("P granted");
        } else {
            PermissionGranted = false;
            System.out.println("P not granted");
        }
    }


    @SuppressLint("RestrictedApi")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelf_capacity);


        //verifyStoragePermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //verifyStoragePermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (!PermissionGranted) {
            System.out.println("pm not OK!");
        } else {
            System.out.println("pm OK!");
        }


        //test



        //DBFactory.config().setDBName("log.db").setPackageName(getPackageName()).setContext(this);

        final EditText edtv_no = (EditText) findViewById(R.id.sc_edtv_sno);
        final EditText edtv_cap = (EditText) findViewById(R.id.sc_edtv_cap);
        final TextView txtv_updateDate = (TextView) findViewById(R.id.sc_txtv_updateDate);
        Button btn_update = (Button) findViewById(R.id.sc_btn_update);

        final ShelfDao dao = new ShelfDao();


        edtv_no.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(@NonNull Editable s) {
                super.afterTextChanged(s);
                // verifyStoragePermissions(ShelfCapacityActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                // verifyStoragePermissions(ShelfCapacityActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);

                Shelf shelf = dao.getByNo(edtv_no.getText().toString().toUpperCase(Locale.ROOT));
                if (shelf != null) {
                    edtv_cap.setText(shelf.getCapacity().toString());
                    txtv_updateDate.setText(DateTimeUtil.formatGMTtoLocal(new Date(shelf.getUpdate_date().getTime())));
                } else {
                    edtv_cap.setText("");
                    txtv_updateDate.setText("");
                }
            }
        });
        edtv_no.setFilters(new InputFilter[]{new InputFilter.AllCaps()});


        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Shelf shelf = dao.getByNo(edtv_no.getText().toString().toUpperCase(Locale.ROOT));
                boolean isDataExist = shelf != null ? true : false;
                Integer id = isDataExist ? shelf.getId() : null;

                shelf = new Shelf();
                shelf.setNo(edtv_no.getText().toString().toUpperCase(Locale.ROOT));
                shelf.setCapacity(Integer.parseInt(edtv_cap.getText().toString()));
                shelf.setType(Type.NONE);
                shelf.setUpdate_date(Timestamp.valueOf(DateTimeUtil.formatLocalToGMT(new Date())));

                if (isDataExist) {
                    dao.update(shelf, id);
                } else {
                    dao.add(shelf);
                }
            }
        });


        //test...
        Button btn_defaultNo = (Button) findViewById(R.id.sc_btn_defaultNo);
        btn_defaultNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtv_no.setText("1A01A11");
            }
        });



        /*
        Button btnTest1 = (Button) findViewById(R.id.btnTest1);
        btnTest1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                test1();
            }
        });

        Button btnTest2 = (Button) findViewById(R.id.btnTest2);
        btnTest2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                test2();
            }
        });
        //*/

    }

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            String stringExtra = data.getStringExtra(Intent.EXTRA_TEXT);

        }

    }
    //*/


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void test1() {
        ShelfDao dao = new ShelfDao();
        Shelf shelf = new Shelf();
        shelf.setNo("1A16A11");
        shelf.setType(Type.NEW);
        shelf.setCapacity(50);
        shelf.setUpdate_date(new Timestamp(new java.util.Date().getTime()));

        dao.add(shelf);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void test2() {

        ShelfDao dao = new ShelfDao();
        Shelf shelf = dao.get(16);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));
        //formatter.setTimeZone(TimeZone.getTimeZone(TimeZone.getDefault().getID()));
        String update_date = formatter.format(shelf.getUpdate_date().getTime());

        //shelf.setUpdate_date(Timestamp.valueOf(update_date));

        StringBuilder sb = new StringBuilder();
        sb.append("no=" + shelf.getNo()).append("\n")
                .append("date=" + update_date);

        //TextView txtvMsg = (TextView) findViewById(R.id.txtvMsg);
        //txtvMsg.setText(sb.toString());
    }
}