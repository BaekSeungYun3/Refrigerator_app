package com.example.refrigerator_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.zip.DataFormatException;

public class Cart3_Activity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    DBHelper db;
    Button add_button;
    EditText cart_editText;
    ListView cartlist;
    ArrayList<String> listItem;
    CustomAdapter adapter;
    RelativeLayout cartRelative;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart3);

        db = new DBHelper(this);
        listItem = new ArrayList<>();
        add_button = findViewById(R.id.add_button);
        cart_editText = findViewById(R.id.cart_editText);
        cartlist = findViewById(R.id.cart_list);
        cartRelative = findViewById(R.id.cart_Relative);
        Button re_add_button = findViewById(R.id.re_add_button);

        adapter = new CustomAdapter(this, listItem);
        cartlist.setAdapter(adapter);
        adapter.updateData();
        //네비 바 안보이게
        bottomNavigationView = findViewById(R.id.bottom_navi);
        EditText editText = findViewById(R.id.cart_editText);
        cartlist = findViewById(R.id.cart_list);
//        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                if (hasFocus) {
//                    bottomNavigationView.setVisibility(View.GONE);
//                    re_add_button.setVisibility(View.GONE);
//                } else {
//                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//                    if (imm != null && imm.isAcceptingText()) {
//                        bottomNavigationView.setVisibility(View.VISIBLE);
//                        re_add_button.setVisibility(View.VISIBLE);
//                    }
//                }
//            }
//        });

        View rootView = findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getRootView().getHeight();
                int keyboardHeight = screenHeight - r.bottom;

                if (keyboardHeight > screenHeight * 0.15) {
                    // 키보드가 올라왔을 때
                    bottomNavigationView.setVisibility(View.GONE);
                    re_add_button.setVisibility(View.GONE);
                } else {
                    // 키보드가 내려갔을 때
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    re_add_button.setVisibility(View.VISIBLE);
                }
            }
        });
        //여기까지

        cartlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String text = cartlist.getItemAtPosition(i).toString();
            }
        });

        //장바구니 추가
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = cart_editText.getText().toString();
                if (!name.equals("") && db.insertData(name)) {
                    cart_editText.setText("");
                    adapter.updateData(); // 데이터베이스 변경 후에 updateData() 호출
                } else {
                    Toast.makeText(Cart3_Activity.this, "장볼 재료를 입력하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //냉장고로 이동

        re_add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Cart3_Activity.this, Cart2_Activity.class);
                startActivity(intent);
            }
        });

        // 리스트뷰 항상 보이도록 설정
        cartlist.setVisibility(View.VISIBLE);

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

                    return true;
                }else if (itemId == R.id.Community) {
                    startActivity(new Intent(getApplicationContext(),Community_Activity.class));
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
}