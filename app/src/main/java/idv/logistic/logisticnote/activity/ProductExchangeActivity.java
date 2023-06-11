package idv.logistic.logisticnote.activity;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import idv.kw.tool.DateTimeUtil;
import idv.logistic.logisticnote.R;
import idv.logistic.logisticnote.dao.product.ProductExchangeDao;
import idv.logistic.logisticnote.model.ProductExchange;


public class ProductExchangeActivity extends AppCompatActivity {
    //quick search: oc,list clk,wage


    static ProductExchangeActivity _this;


    Button btn_createOne;
    Button btn_edit;
    Button btn_previous;
    Button btn_next;
    Button btn_searchPNO;
    Button btn_goTo1;
    Button btn_createWage;
    TextView txtv_searchPNO;
    ListView txtv_list;
    ArrayList<ProductExchange> productList = new ArrayList<>();
    ArrayAdapter<ProductExchange> listAdapter;
    SharedPreferences prefs_listItem;
    private Vibrator vibrator;

    HashMap<Integer, Integer> searchPNOMap = new HashMap<>();
    HashMap<Integer, Integer> searchSNOMap = new HashMap<>();
    HashSet<Integer> checkSet = new HashSet<>();

    int currentSearchPNOIdx;


    public static String bkp1;
    public static String bkp2;
    public static String bkp3;
    public static String bkp4;
    public static ArrayList<String> shelfNoRecordList = new ArrayList<>();

    private static final long DOUBLE_CLICK_TIME_DELTA = 200;
    private int clickCount = 0;
    boolean isDoubleClicked;
    long lastClickTime = 0;//上一次點擊的時間
    private Handler listViewDoubleClickHandler = new Handler();

    ProductExchange selectedPE;

    ProductExchange deletedPE;
    int selectedIdx;

    private void initComps() {
        txtv_searchPNO = findViewById(R.id.pe_txtv_search_pno);
        btn_previous = findViewById(R.id.pe_btn_previous);
        //btn_next = findViewById(R.id.pe_btn_next);
        btn_createWage = findViewById(R.id.pe_btn_wage);
        btn_createOne = (Button) findViewById(R.id.pe_btn_create);
        btn_edit = (Button) findViewById(R.id.pe_btn_edit);
        txtv_list = (ListView) findViewById(R.id.pe_lstv_list);
        btn_searchPNO = findViewById(R.id.pe_btn_search_PNO);
        btn_goTo1 = findViewById(R.id.pe_btn_go_to_1);


        btn_goTo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollto(0);
            }
        });

        btn_searchPNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer listIdx = searchPNOMap.get(currentSearchPNOIdx);
                if (listIdx != null) {
                    scrollto(listIdx);
                    currentSearchPNOIdx++;
                } else {
                    currentSearchPNOIdx = 0;
                }
            }
        });

        //xxx list clk
        txtv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (isDoubleClicked) {
                    // 雙擊事件
                    selectedPE = (ProductExchange) adapterView.getItemAtPosition(i);
                    selectedIdx = i;


                    isDoubleClicked = false;
                } else {
                    // 單擊事件
                    ProductExchange beforeSelectedPE = selectedPE;
                    int beforeSelectedIdx = selectedIdx;
                    ProductExchange afterSelectedPE = selectedPE = (ProductExchange) adapterView.getItemAtPosition(i);
                    int afterSelectedIdx = selectedIdx = i;

                    if (beforeSelectedPE != null && afterSelectedPE != null) {
                        if (beforeSelectedPE.getShelfNoFrom() != null && afterSelectedPE.getShelfNoFrom() != null) {
                            if (beforeSelectedPE.getShelfNoFrom().equalsIgnoreCase(afterSelectedPE.getShelfNoFrom())) {
                                selectedPE = afterSelectedPE;
                                selectedIdx = afterSelectedIdx;
                            } else {
                                selectedPE = beforeSelectedPE;
                                selectedIdx = beforeSelectedIdx;
                            }
                        }
                    } else if (beforeSelectedPE == null) {
                        selectedPE = afterSelectedPE;
                        selectedIdx = afterSelectedIdx;
                    }


                    isDoubleClicked = true;
                    listViewDoubleClickHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isDoubleClicked = false;
                        }
                    }, DOUBLE_CLICK_TIME_DELTA); // 200 毫秒的延遲時間
                }

                /*
                long clickTime = System.currentTimeMillis();
                if (clickCount == 0 || clickTime - lastClickTime > DOUBLE_CLICK_TIME_DELTA) {
                    //單擊事件
                    //to get selectedPE
                    ProductExchange beforeSelectedPE = selectedPE;
                    int beforeSelectedIdx = selectedIdx;
                    ProductExchange afterSelectedPE = selectedPE = (ProductExchange) adapterView.getItemAtPosition(i);
                    int afterSelectedIdx = selectedIdx = i;

                    if (beforeSelectedPE != null && afterSelectedPE != null) {
                        if (beforeSelectedPE.getShelfNoFrom() != null && afterSelectedPE.getShelfNoFrom() != null) {
                            if (beforeSelectedPE.getShelfNoFrom().equalsIgnoreCase(afterSelectedPE.getShelfNoFrom())) {
                                selectedPE = afterSelectedPE;
                                selectedIdx = afterSelectedIdx;
                            } else {
                                selectedPE = beforeSelectedPE;
                                selectedIdx = beforeSelectedIdx;
                            }
                        }
                    } else if (beforeSelectedPE == null) {
                        selectedPE = afterSelectedPE;
                        selectedIdx = afterSelectedIdx;
                    }

                    clickCount = 1;
                } else {
                    //視為雙擊事件
                    selectedPE = (ProductExchange) adapterView.getItemAtPosition(i);
                    selectedIdx = i;




                    clickCount = 0;
                }
                lastClickTime = clickTime;//更新上一次點擊的時間

                 */


                //*

                currentSearchPNOIdx = 0;
                searchSNOMap.clear();
                searchPNOMap.clear();
                int listIdx = 0;
                int currentSearchPNOMapIdxForSetup = 0;
                int currentSearchSNOMapIdxForSetup = 0;
                for (ProductExchange item : productList) {
                    boolean isPNOCanBeSearched = false;
                    boolean isSNOCanBeSearched = false;

                    if (selectedPE != null && item.getShelfNoFrom() != null && selectedPE.getShelfNoFrom() != null
                            && !selectedPE.getShelfNoFrom().equalsIgnoreCase("")) {
                        if (item.getShelfNoFrom().equalsIgnoreCase(selectedPE.getShelfNoFrom())) {
                            isSNOCanBeSearched = true;
                        }
                    }

                    if (selectedPE != null && item.getNo() != null && selectedPE.getNo() != null) {
                        if (item.getNo().length() >= 9 && selectedPE.getNo().length() >= 9) {
                            if (item.getNo().substring(0, 9).equalsIgnoreCase(selectedPE.getNo().substring(0, 9))) {
                                isPNOCanBeSearched = true;
                            }
                        }
                    }

                    if (isSNOCanBeSearched) {
                        searchSNOMap.put(currentSearchSNOMapIdxForSetup++, listIdx);
                    }
                    if (isPNOCanBeSearched) {
                        searchPNOMap.put(currentSearchPNOMapIdxForSetup++, listIdx);
                    }

                    listIdx++;
                }

                scrollto(txtv_list.getFirstVisiblePosition());


                txtv_list.setSelector(R.color.skyblue);
            }
        });

        txtv_searchPNO.setVisibility(View.GONE);
        btn_previous.setVisibility(View.GONE);
        //btn_next.setVisibility(View.GONE);

        txtv_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                final ProductExchange pe = (ProductExchange) adapterView.getItemAtPosition(i);

                AlertDialog.Builder builder = new AlertDialog.Builder(ProductExchangeActivity.this);
                builder.setTitle("Mark/Delete : " + pe.getNo());

                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_marked_item, null);
                builder.setView(dialogView);


                /*
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: add your code here for positive button click
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: add your code here for negative button click
                    }
                });

                // */

                AlertDialog dialog_del_mark = builder.create();

                TextView txtv_info = dialogView.findViewById(R.id.pe_dl_txtv_info);
                txtv_info.setText("Are you sure you want to delete this item? (" +
                        pe.getShelfNoFrom() + " > " + pe.getShelfNoTo() + " : " + pe.getNo() + ")");


                Button btn_dl_delete = dialogView.findViewById(R.id.pe_dl_btn_delete);
                btn_dl_delete.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        // 震動
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
                            vibrator.vibrate(50);
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(ProductExchangeActivity.this);
                        builder.setTitle("提示");
                        builder.setMessage("是否確認刪除？");
                        builder.setPositiveButton("確認", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 点击确认按钮后执行删除操作
                                ProductExchangeDao dao = new ProductExchangeDao();
                                dao.delete(pe.getId());

                                // 震動
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                                } else {
                                    vibrator.vibrate(50);
                                }

                                String delMsg = "(" + pe.getShelfNoFrom() + " > " + pe.getShelfNoTo() + " : " + pe.getNo() + ")";
                                Toast.makeText(ProductExchangeActivity.this, delMsg + " 已刪除", Toast.LENGTH_LONG).show();
                                callShowList();
                                dialog_del_mark.dismiss();
                            }
                        });
                        builder.setNegativeButton("取消", null);

                        builder.show();
                        return true;
                    }
                });

                Button btn_dl_clip = dialogView.findViewById(R.id.pe_dl_btn_clip);
                btn_dl_clip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 取得要複製的文字內容
                        StringBuilder sb = new StringBuilder();
                        sb.append(pe.getNo() + " : " + pe.getShelfNoFrom() + " → " + pe.getShelfNoTo());

                        //String textToCopy = pe.getNo() + " : " + pe.getShelfNoFrom() + " → " + pe.getShelfNoTo();

                        String tag = prefs_listItem.getString("item_tag_" + pe.getId(), "");
                        sb.append(" , " + tag);

                        String textToCopy = sb.toString();


                        ClipboardManager clipboardManager = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                            // 取得剪貼簿管理員
                            clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

                            // 建立 ClipData 物件，將要複製的文字內容設定到 ClipData 中
                            ClipData clipData = ClipData.newPlainText("label", textToCopy);

                            // 將 ClipData 物件放到剪貼簿中
                            clipboardManager.setPrimaryClip(clipData);

                            Toast.makeText(ProductExchangeActivity.this, "已複製內容", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


                Button btn_dl_quickMark = dialogView.findViewById(R.id.pe_dl_btn_quickMark);
                btn_dl_quickMark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        long pattern[] = new long[]{50, 100, 50, 100, 50};

                        if (checkSet.contains(pe.getId())) {
                            checkSet.remove(pe.getId());

                            // 震動
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                            } else {
                                vibrator.vibrate(pattern, -1);
                            }
                        } else {
                            checkSet.add(pe.getId());

                            // 震動
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                            } else {
                                vibrator.vibrate(50);
                            }
                        }

                        callShowList();

                        dialog_del_mark.dismiss();
                    }
                });


                Button btn_dl_mark = dialogView.findViewById(R.id.pe_dl_btn_mark);
                boolean isMarked = prefs_listItem.getBoolean("item_mark_" + pe.getId(), false);


                btn_dl_mark.setOnClickListener(new View.OnClickListener() {
                    private long lastClickTime = 0;
                    private int clickCount = 0;

                    @Override
                    public void onClick(View view) {


                        long currentTime = System.currentTimeMillis();
                        if (currentTime - lastClickTime < 500) {
                            clickCount++;
                            if (clickCount == 2) {
                                long[] pattern = {0, 100, 100, 100};
                                // 双击按钮的操作
                                SharedPreferences.Editor editor = prefs_listItem.edit();
                                if (isMarked) {
                                    editor.putBoolean("item_mark_" + pe.getId(), false);// 震動
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                                    } else {
                                        vibrator.vibrate(50);
                                    }
                                } else {
                                    editor.putBoolean("item_mark_" + pe.getId(), true);
                                    // 震動
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                                    } else {
                                        vibrator.vibrate(pattern, -1);
                                    }
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                                    editor.apply();
                                }

                                callShowList();

                                clickCount = 0;

                                dialog_del_mark.dismiss();
                            }
                        } else {
                            clickCount = 1;
                        }
                        lastClickTime = currentTime;


                    }
                });
                btn_dl_mark.setText(isMarked ? "DEMARK" : "MARK");

                Button btn_dl_cancel = dialogView.findViewById(R.id.pe_dl_btn_cancel);
                btn_dl_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog_del_mark.dismiss();
                    }
                });


                dialog_del_mark.show();

                return true;

                /*
                AlertDialog.Builder builder = new AlertDialog.Builder(ProductExchangeActivity.this);
                builder.setTitle("提示");
                builder.setMessage("是否確認刪除？");
                builder.setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击确认按钮后执行删除操作
                        final ProductExchange pe = (ProductExchange) adapterView.getItemAtPosition(i);
                        ProductExchangeDao dao = new ProductExchangeDao();
                        dao.delete(pe.getId());
                        Toast.makeText(ProductExchangeActivity.this, pe.getNo() + " 已刪除", Toast.LENGTH_LONG).show();
                        callShowList();
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
                return true;

                // */
            }

        });


    }


    private void scrollto(int index) {
        View v = txtv_list.getChildAt(0);
        int top = (v == null) ? 0 : (v.getTop() - txtv_list.getPaddingTop());

        // 重刷 ListView
        showList();

        // 將 ListView 滾動至先前的位置
        txtv_list.setSelectionFromTop(index, top);
    }

    //xxx oc
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_exchange);

        _this = this;

        initComps();

        // 初始化 Vibrator
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        //改在callShowList中實現
        //prefs_listItem = getSharedPreferences("list_prefs", MODE_PRIVATE);


        btn_createOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(ProductExchangeActivity.this, ProductExchangeCreateActivity.class);
                startActivity(intent);
            }
        });

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedPE != null) {
                    Intent intent = new Intent();
                    intent.setClass(ProductExchangeActivity.this, ProductExchangeCreateActivity.class);
                    intent.putExtra("seletctedId", selectedPE.getId());
                    intent.putExtra("seletctedIdx", selectedIdx);
                    startActivity(intent);
                }

            }
        });

        btn_createWage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedPE!=null){
                    Timestamp setup_date = selectedPE.getSetup_date();
                    Timestamp timestamp = DateTimeUtil.formatToTimestampGMTtoLocal(setup_date);
                    String searchDate = "2023y" + timestamp.getMonth() + "m";
                    countWage(searchDate);
                }

            }
        });

        listAdapter = new ArrayAdapter<ProductExchange>(this, android.R.layout.simple_list_item_1,
                productList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


                TextView txtv_text1 = (TextView) super.getView(position, convertView, parent);
                ProductExchange item = getItem(position);
                //ProductExchange item = productList.get(position);
                StringBuilder sb = new StringBuilder();


                //int selectStateCode = 0;
                boolean selectStatePNO = false;
                boolean selectStateSNO = false;
                boolean selectStateCurrent = false;


                boolean isUpload = prefs_listItem.getBoolean("item_upload_" + item.getId(), false);
                String SNOMoveMark = isUpload ? " ▶ " : " ▷ ";

                //equal sno
                if (selectedPE != null && item.getShelfNoFrom() != null && selectedPE.getShelfNoFrom() != null &&
                        !item.getShelfNoFrom().equalsIgnoreCase("")) {
                    if (item.getShelfNoFrom().equalsIgnoreCase(selectedPE.getShelfNoFrom())) {
                        //SNOMoveMark = " ▶ ";
                        //selectStateCode += 1;
                        selectStateSNO = true;
                    }
                }


                //equal pno
                String tagPNO = "";
                if (selectedPE != null && item.getNo() != null && selectedPE.getNo() != null) {
                    if (item.getNo().length() >= 9 && selectedPE.getNo().length() >= 9) {
                        if (item.getNo().substring(0, 9).equalsIgnoreCase(selectedPE.getNo().substring(0, 9))) {
                            //tagPNO = " ※ ";
                            //selectStateCode += 2;
                            selectStatePNO = true;
                        }
                    }
                }


                String currentItem = "";
                if (selectedPE != null && selectedPE.getId() == item.getId()) {
                    //currentItem = "◈  ";
                    //selectStateCode = 9;
                    selectStateCurrent = true;
                }


                if (selectStateCurrent) {
                    txtv_text1.setBackgroundColor(Color.YELLOW);//亮黃
                } else if (selectStatePNO && selectStateSNO) {
                    txtv_text1.setBackgroundColor(Color.parseColor("#FF8000"));//橙

                } else if (selectStatePNO) {
                    txtv_text1.setBackgroundColor(Color.parseColor("#EAC100"));//楬黃

                } else if (selectStateSNO) {
                    txtv_text1.setBackgroundColor(Color.parseColor("#00E600"));//綠
                } else {
                    txtv_text1.setBackgroundColor(Color.WHITE);//白
                }


                /*
                txtv_text1.setBackgroundColor(Color.WHITE);//白
                if (selectStateCurrent) {
                    txtv_text1.setBackgroundColor(Color.YELLOW);//亮黃
                } else {
                    if (selectStatePNO && selectStateSNO) {
                        txtv_text1.setBackgroundColor(Color.parseColor("#FF8000"));//橙


                    } else {
                        if (selectStatePNO) {
                            txtv_text1.setBackgroundColor(Color.parseColor("#EAC100"));//楬黃

                        } else if (selectStateSNO) {
                            txtv_text1.setBackgroundColor(Color.parseColor("#00E600"));//綠
                        }
                    }
                }


                // */


                /*
                switch (selectStateCode) {
                    case 1://sno
                        txtv_text1.setBackgroundColor(Color.parseColor("#00E600"));//綠
                        break;
                    case 2://pno
                        txtv_text1.setBackgroundColor(Color.parseColor("#EAC100"));//黃
                        break;
                    case 3://sno+pno
                        txtv_text1.setBackgroundColor(Color.parseColor("#FF8000"));//橙
                        break;
                    case 4://selected
                        break;
                    case 5://selected+sno
                        break;
                    case 6://selected+pno
                        break;
                    case 7://selected+sno+pno
                        break;
                    case 9://current
                        txtv_text1.setBackgroundColor(Color.parseColor("#FFFF0F"));//亮黃
                        break;
                    default:
                        txtv_text1.setBackgroundColor(Color.parseColor("#FFFFFF"));//白

                }

                 */

                String pf_tag = prefs_listItem.getString("item_tag_" + item.getId(), "");
                String tag = null;
                if (pf_tag.equalsIgnoreCase("")) {
                    tag = "";
                } else {
                    tag = "            ⓘ " + pf_tag + " \r\n";
                }

                String pno = item.getNo();
                if (pno.length() == 13) {
                    pno = pno.substring(0, 9) + "-" + pno.substring(9);
                } else if (pno.length() == 9 && selectStatePNO) {
                    pno = " ⓟ " + pno;
                }

                String shelfNoto = item.getShelfNoTo();
                if (selectStateCurrent) {

                } else {
                    if ((selectStatePNO && selectStateSNO) && selectedPE.getShelfNoTo().equalsIgnoreCase(item.getShelfNoTo())) {
                        shelfNoto = "〔" + shelfNoto + "〕";
                    }

                }

                String isOtherMark = "";
                boolean isOther = false;
                if (position == selectedIdx) {
                    for (Integer id : searchPNOMap.values()) {
                        if (id < position) {
                            isOther = true;
                            break;
                        }
                    }

                    if (isOther) {
                        isOtherMark = " ▲";
                    }
                }


                //create item content string
                sb.append(item.getId()).append(".  ").append(currentItem).append(item.getShelfNoFrom()).append(SNOMoveMark).
                        append(shelfNoto).append(isOtherMark).append(" \r\n").
                        append("            ").append(tagPNO).append("no. ").append(pno).append(" \r\n  ").
                        append(tag).
                        append("            (").append(DateTimeUtil.
                                formatGMTtoLocal(new Date(item.getSetup_date().getTime()))).append(")");


                SpannableString spannableString = new SpannableString(sb.toString());
                int startIndex = -1;
                boolean is4010or4020 = false;
                if ((startIndex = sb.toString().indexOf("4000")) != -1 ||
                        (startIndex = sb.toString().indexOf("7000")) != -1) {
                    is4010or4020 = false;
                } else if ((startIndex = sb.toString().indexOf("4010")) != -1 ||
                        (startIndex = sb.toString().indexOf("4020")) != -1 ||
                        (startIndex = sb.toString().indexOf("4030")) != -1 ||
                        (startIndex = sb.toString().indexOf("7010")) != -1 ||
                        (startIndex = sb.toString().indexOf("7020")) != -1 ||
                        (startIndex = sb.toString().indexOf("7030")) != -1) {
                    is4010or4020 = true;
                }


                if (startIndex != -1) {
                    if (startIndex >= 0) {
                        int endIndex = startIndex + 4;
                        if (is4010or4020) {
                            spannableString.setSpan(new ForegroundColorSpan(Color.RED), startIndex, endIndex,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        }
                        spannableString.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);



                        /*
                        spannableString.setSpan(new TextAppearanceSpan(this.getContext(),
                                        android.R.style.TextAppearance_DeviceDefault_Large), startIndex, endIndex,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


//                         */
                    }


                }

                startIndex = sb.toString().indexOf("no.");
                if (startIndex >= 0) {
                    int endIndex = startIndex + 3;
                    boolean pf_mark = prefs_listItem.getBoolean("item_mark_" + item.getId(), false);
                    if (!pf_mark) {
                        spannableString.setSpan(new StyleSpan(Typeface.NORMAL), startIndex, endIndex,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    } else {
                        spannableString.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), startIndex, endIndex,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }

                //反白符號ⓘ
                startIndex = sb.toString().indexOf("ⓘ");
                if (startIndex >= 0) {
                    int endIndex = startIndex + 1;
                    if (!selectStateCurrent && selectStatePNO && selectStateSNO) {
                        spannableString.setSpan(new ForegroundColorSpan(Color.YELLOW), startIndex, endIndex,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else {
                        spannableString.setSpan(new ForegroundColorSpan(Color.RED), startIndex, endIndex,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    }

                    spannableString.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                if ((startIndex = sb.toString().indexOf("ⓟ")) >= 0) {
                    int endIndex = startIndex + 1;

                    if (selectStateCurrent) {
                        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FF9900")), startIndex, endIndex,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else {

                        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#00FFFF")), startIndex, endIndex,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                }


                if ((startIndex = sb.toString().indexOf("〔")) >= 0) {
                    int endIndex = startIndex + 1;
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#00FFFF")), startIndex, endIndex,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableString.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                if ((startIndex = sb.toString().indexOf("〕")) >= 0) {
                    int endIndex = startIndex + 1;
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#00FFFF")), startIndex, endIndex,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableString.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                if ((startIndex = sb.toString().indexOf("SEARCH")) >= 0 || (startIndex = sb.toString().indexOf("OK")) >= 0 ||
                        (startIndex = sb.toString().indexOf("LABELED")) >= 0 || (startIndex = sb.toString().indexOf("CHECK")) >= 0) {
                    int endIndex = startIndex + 1;
                    spannableString.setSpan(new ForegroundColorSpan(Color.RED), startIndex, endIndex,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableString.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }


                String input = sb.toString();
                String regex = "[0-9][A-Z][0-9]{2}[A-Z][0-9]{2}";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(input);
                while (matcher.find()) {
                    int start = matcher.start();
                    int end = matcher.end();

                    spannableString.setSpan(new StyleSpan(Typeface.BOLD), start, end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    if (!input.startsWith("11", end - 2)) {
                        if (!selectStateCurrent && selectStatePNO && selectStateSNO) {
                            spannableString.setSpan(new ForegroundColorSpan(Color.YELLOW), start, end,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        } else {
                            spannableString.setSpan(new ForegroundColorSpan(Color.RED), start, end,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }


                    }
                }

                if (checkSet.contains(item.getId())) {
                    if ((startIndex = sb.toString().indexOf("▶")) >= 0 || (startIndex = sb.toString().indexOf("▷")) >= 0) {
                        int endIndex = startIndex + 1;
                        spannableString.setSpan(new ForegroundColorSpan(Color.RED), startIndex, endIndex,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannableString.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }


                txtv_text1.setText(spannableString);
                return txtv_text1;
            }
        };
        callShowList();

    }

    public void callShowList() {
        callShowList(-1);
    }

    public void callShowList(int idx) {
        prefs_listItem = getSharedPreferences("list_prefs", MODE_PRIVATE);
        ProductExchangeDao dao = new ProductExchangeDao();
        List<ProductExchange> list = dao.getAll();


        // xxx test

        // countWage();

        //end test


        productList.clear();
        productList.addAll(list);
        Collections.reverse(productList);
        showList();

        if (idx >= 0) {
            this.scrollto(idx);

        } else {
            this.scrollto(-1);
        }
    }

    //xxx wage
    private void countWage(String searchDate) {
        ProductExchangeDao dao = new ProductExchangeDao();
        List<ProductExchange> list = dao.getAll();

        HashMap<String, HashMap<String, Double>> ymMap = new HashMap<>();//year moth map
        String currentDateStr = null;
        String currentYMStr = null;
        double lastHours = 0;
        int lastMinutes = 0;
        int lastDate1 = 0;

        for (ProductExchange pe : list) {
            Timestamp date0 = pe.getSetup_date();

            Timestamp date = DateTimeUtil.formatToTimestampGMTtoLocal(date0);


            // System.out.println("xxx date=" + date);
            int year = date.getYear() + 1900;
            int month = date.getMonth();
            int date1 = date.getDate();
            double hours = date.getHours();
            int minutes = date.getMinutes();


            String ymStr = year + "y" + month + "m";
            //System.out.println("xxx ymStr=" + ymStr);
            String dateStr = ymStr + date1 + "d";

            if (currentYMStr == null || !currentYMStr.equals(ymStr)) {
                ymMap.put(ymStr, new HashMap<>());

            }

            //System.out.println("xxx date=" + date);
            if (currentDateStr != null && !currentDateStr.equals(dateStr)) {
                if (ymMap.containsKey(currentYMStr)) {
                    HashMap dhMap = ymMap.get(currentYMStr);//day hours map


                    lastHours -= 8;

                    if (lastMinutes >= 10) {
                        lastHours += 1d;
                    }

                    dhMap.put(lastDate1, lastHours);
                    // System.out.println("xxx put lastHours=" + lastHours);


                }
            }

            currentDateStr = dateStr;
            currentYMStr = ymStr;
            lastHours = hours;
            lastMinutes = minutes;
            lastDate1 = date1;


            //System.out.println("xxx date1="+date1);

            // System.out.println("");


        }

        // System.out.println("xxx start1 test");
        HashMap<String, Double> myYMMap = ymMap.get(searchDate);

        HashMap<String, Double> totalMap = new HashMap<>();
        totalMap.put("x1.0", 0.0);
        totalMap.put("x1.33", 0.0);
        totalMap.put("x1.66", 0.0);

        for (Map.Entry<String, Double> entry : myYMMap.entrySet()) {
            System.out.println(entry);

            double hours = entry.getValue()-1;//中午休息一小時
            if (hours >= 10) {
                double remainder = hours - 10;
                totalMap.put("x1.66", totalMap.get("x1.66") + remainder);
                hours -= remainder;
            }

            if (hours >= 8) {
                double remainder = hours - 8;
                totalMap.put("x1.33", totalMap.get("x1.33") + remainder);
                hours -= remainder;
            }

            if (hours > 0) {
                double remainder = hours;
                totalMap.put("x1.0", totalMap.get("x1.0") + remainder);
            }

        }

        //System.out.println("xxx entries :" + myYMMap.entrySet());
        // System.out.println("xxx total :" + totalMap);

        //System.out.println("xxx start1 end");

        /*
        System.out.println("xxx start2 test" );
        System.out.println("xxx start2 ymMap: " +ymMap);
        System.out.println("xxx start2 end" );


         */

        String textToCopy = "entries= " + myYMMap.entrySet() + " ; total wage= " + totalMap;

        ClipboardManager clipboardManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            // 取得剪貼簿管理員
            clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

            // 建立 ClipData 物件，將要複製的文字內容設定到 ClipData 中
            ClipData clipData = ClipData.newPlainText("label", textToCopy);

            // 將 ClipData 物件放到剪貼簿中
            clipboardManager.setPrimaryClip(clipData);

            Toast.makeText(ProductExchangeActivity.this, "已複製內容", Toast.LENGTH_SHORT).show();
        }
    }


    private void showList() {
        /*
        ProductExchange elems[] = new ProductExchange[list.size()];
        list.toArray(elems);

        descElems = new ProductExchange[list.size()];
        int i = elems.length - 1;
        for (ProductExchange pe : elems) {
            descElems[i] = pe;
            i--;
        }

        // */

        txtv_list.setAdapter(listAdapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            txtv_list.deferNotifyDataSetChanged();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*
        System.out.println("xxx PEA start get idx...");

        if (resultCode == RESULT_OK) {
            String seletctedIdx = data.getStringExtra("seletctedIdx");

            if (seletctedIdx != null) {
                System.out.println("xxx PEA get idx:" + seletctedIdx);
            }

        }

         */
    }
}
