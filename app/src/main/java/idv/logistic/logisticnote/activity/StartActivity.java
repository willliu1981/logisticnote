package idv.logistic.logisticnote.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import idv.logistic.logisticnote.R;
import idv.logistic.logisticnote.TestScanActivity;
import idv.logistic.logisticnote.database.connection.DBFactory;

public class StartActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        DBFactory.config().setDBName("log.db").setPackageName(getPackageName()).setContext(this);

        Button btn_exchange = (Button) findViewById(R.id.st_btn_exchange);
        btn_exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(StartActivity.this, ProductExchangeActivity.class);
                startActivity(intent);
            }
        });

        Button btn_shelfCapacity = (Button) findViewById(R.id.st_btn_shelfCapacity);
        btn_shelfCapacity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(StartActivity.this, ShelfCapacityActivity.class);
                startActivity(intent);
            }
        });

        btn_shelfCapacity.setVisibility(View.GONE);

        Button btn_wage = (Button) findViewById(R.id.st_btn_wage);
        btn_wage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(StartActivity.this, WageActivity.class);
                startActivity(intent);
            }
        });
        btn_wage.setVisibility(View.GONE);

        Button btn_testRc = (Button) findViewById(R.id.st_btn_test_rc);
        btn_testRc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    intent.setClass(StartActivity.this, MainActivity2.class);
                }
                startActivity(intent);
            }
        });
        //btn_testRc.setVisibility(View.GONE);

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("確定要退出應用嗎？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("否", null)
                .show();
    }
}