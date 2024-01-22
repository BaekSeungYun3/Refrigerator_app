package com.example.refrigerator_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

public class Cart2_Activity extends AppCompatActivity{

    private TableLayout tableLayout;
    private DbOpenHelper dbOpenHelper;
    private  DBHelper dbHelper;
    private TextView nameTextView;
    private TextView quantityTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart2);
        dbHelper= new DBHelper(this);
        tableLayout = findViewById(R.id.tableLayout);
        nameTextView = findViewById(R.id.nametx);
        quantityTextView = findViewById(R.id.qutx);
        dbOpenHelper = new DbOpenHelper(this);
        dbOpenHelper.open();

        populateData();

        // DBadd_btn 클릭 이벤트 처리
        ImageButton addButton = findViewById(R.id.floatingActionButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> selectedNames = new ArrayList<>();
                for (int i = 0; i < tableLayout.getChildCount(); i++) {
                    View view = tableLayout.getChildAt(i);
                    if (view instanceof TableRow) {
                        TableRow row = (TableRow) view;
                        CheckBox checkBox = (CheckBox) row.getChildAt(0);
                        TextView rowNameTextView = (TextView) row.getChildAt(1);
                        if (checkBox != null && checkBox.isChecked()) {
                            String name = rowNameTextView.getText().toString();
                            selectedNames.add(name);
                        }}}
                for (String name : selectedNames) {
                    boolean result = dbHelper.insertData(name);
                }
                showConfirmationDialog(selectedNames);
            }

            private void showConfirmationDialog(ArrayList<String> selectedNames) {
                String message = TextUtils.join(", ", selectedNames) + " 이(가) 장바구니에 추가되었습니다.";

                AlertDialog.Builder builder = new AlertDialog.Builder(Cart2_Activity.this);
                builder.setMessage(message);
                builder.setPositiveButton("장바구니로 이동", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Cart2_Activity.this, Cart_Activity.class));
                    }
                });
                builder.setNegativeButton("닫기", null);
                builder.show();
            }



        });
        //여기부터 복붙
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navi);
        bottomNavigationView.setSelectedItemId(R.id.Cart);


        //초기 화면을 home으로 즉, FrameLayout(main_frame)에 fragement_home.xml 띄우기
        //     getSupportFragmentManager().beginTransaction().add(R.id.main_frame, new home()).commit();

        //네비게이션 뷰 아이템 누르면 이동하는거
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();

                if (itemId == R.id.home) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                    return true; //자기 페이지에 해당하는 if문에 리턴 트루만 남기기.
                } else if (itemId == R.id.Recipe) {
                    startActivity(new Intent(getApplicationContext(), Recipe_Activity.class));  //자기 페이지 아닌 if문에 넣어주기.
//                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
                } else if (itemId == R.id.Cart) {
                    startActivity(new Intent(getApplicationContext(), Cart2_Activity.class));  //자기 페이지 아닌 if문에 넣어주기.
//                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
                }else if (itemId == R.id.Community) {
                    startActivity(new Intent(getApplicationContext(), Community_Activity.class));
                    finish();
                    return true;
                }else if (itemId == R.id.Profile) {
                    startActivity(new Intent(getApplicationContext(), Profile_Activity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });


    }

    // 데이터베이스에서 데이터 가져와서 테이블에 추가
    private void populateData() {
        addTableData(dbOpenHelper.selectColumns(), "_TABLENAME0");
        addTableData(dbOpenHelper.selectColumnsfrz(), "_TABLENAME1");
        addTableData(dbOpenHelper.selectColumnsroom(), "_TABLENAME2");
    }

    // 테이블에 데이터 추가
    private void addTableData(Cursor cursor, String tableName) {
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String quantity = cursor.getString(cursor.getColumnIndex("quantity"));

                TableRow row = new TableRow(this);
                TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                //rowParams.setMargins(5, 5, 5, 5); // 좌우 상하에 5dp의 마진을 추가
                row.setLayoutParams(rowParams);

                CheckBox checkBox = new CheckBox(this);
                TableRow.LayoutParams checkBoxParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                checkBoxParams.gravity = Gravity.CENTER;
                checkBox.setLayoutParams(checkBoxParams);
                row.addView(checkBox);

                TextView nameTextView = new TextView(this);
                nameTextView.setText(name);
                nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                TableRow.LayoutParams nameParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                nameParams.gravity = Gravity.CENTER;
                nameTextView.setLayoutParams(nameParams);
                row.addView(nameTextView);

                TextView quantityTextView = new TextView(this);
                quantityTextView.setText(quantity);
                quantityTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                TableRow.LayoutParams quantityParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                quantityParams.gravity = Gravity.CENTER;
                quantityTextView.setLayoutParams(quantityParams);
                row.addView(quantityTextView);

                tableLayout.addView(row);

            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    // 액티비티 생명주기 메서드 등 다른 코드

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbOpenHelper.close();
    }
}


