package com.example.refrigerator_app;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {
    DBHelper db;
    private ImageButton floatingActionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        db= new DBHelper(this);
        // 인텐트로부터 전달받은 데이터 가져오기
        String recipeName = getIntent().getStringExtra("recipeName");
        String recipeIngredients = getIntent().getStringExtra("recipeIngredients");
        String recipeMatchingIngredients = getIntent().getStringExtra("recipeMatchingIngredients");
        // 뷰 요소 찾기
        TextView textViewRecipeName = findViewById(R.id.textViewRecipeName);
        // 데이터 설정
        textViewRecipeName.setText("\"" + recipeName + "\"");
        String[] allIngredients = recipeIngredients.split(", ");
        LinearLayout container = findViewById(R.id.containerCheckBoxes);

        CheckBox[] ingredientCheckBoxes = new CheckBox[allIngredients.length];

        for (int i = 0; i < allIngredients.length; i++) {
            String ingredient = allIngredients[i].replaceAll("\"", "");
            ingredientCheckBoxes[i] = new CheckBox(this);
            ingredientCheckBoxes[i].setText(ingredient);
            ingredientCheckBoxes[i].setTextSize(16);

            // 가지고 있는 재료인 경우 체크박스 텍스트를 빨간색으로 표시
            if (recipeMatchingIngredients != null && recipeMatchingIngredients.contains(ingredient)) {
                ingredientCheckBoxes[i].setTextColor(Color.RED);
            }

            container.addView(ingredientCheckBoxes[i]);
        }


// Floating Action Button 추가
        FloatingActionButton fab = new FloatingActionButton(this);
        fab.setImageResource(R.drawable.baseline_add_shopping_cart_24);
        fab.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));

// FloatingActionButton의 크기 및 위치 설정
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.gravity = Gravity.END | Gravity.BOTTOM; // 우측 하단에 위치하도록 설정
        int margin = getResources().getDimensionPixelSize(R.dimen.fab_margin);
        layoutParams.setMargins(margin, margin, margin, margin);
        fab.setLayoutParams(layoutParams);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 클릭 이벤트 처리 로직을 여기에 작성하세요
                StringBuilder selectedIngredients = new StringBuilder();
                for (CheckBox checkBox : ingredientCheckBoxes) {
                    if (checkBox.isChecked()) {
                        String ingredientName = checkBox.getText().toString();
                        insertDataToCart(ingredientName);
                        selectedIngredients.append(ingredientName).append(", ");

                        // 체크박스 초기화
                        checkBox.setChecked(false);
                    }
                }

                if (selectedIngredients.length() > 0) {
                    // 선택한 재료를 장바구니에 추가한 경우
                    String message = selectedIngredients.toString().trim();
                    message = message.substring(0, message.length() - 1);  // 마지막 쉼표 제거
                    message += "이(가) 장바구니에 추가되었습니다.";

                    // 사용자에게 이동 여부를 묻는 대화상자(Dialog) 표시
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipeDetailActivity.this);
                    builder.setMessage(message);
                    builder.setPositiveButton("장바구니로 이동", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 장바구니 화면으로 이동
                            startActivity(new Intent(RecipeDetailActivity.this, Cart3_Activity.class));
                        }
                    });
                    builder.setNegativeButton("취소", null);
                    builder.show();
                }
            }

            private void insertDataToCart(String ingredientName) {
                // 장바구니에 데이터 추가
                boolean result = db.insertData(ingredientName);

                if (result) {
                    // 장바구니에 추가 성공
                    Log.d("RecipeDetailActivity", "Ingredient added to cart: " + ingredientName);
                } else {
                    // 장바구니에 추가 실패
                    Log.d("RecipeDetailActivity", "Failed to add ingredient to cart: " + ingredientName);
                }
            }
        });


// FloatingActionButton을 담을 LinearLayout 찾기
        LinearLayout fabContainer = findViewById(R.id.containerCheckBoxes);

// FloatingActionButton을 LinearLayout에 추가
        fabContainer.addView(fab);

        //여기부터 복붙
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navi);
        bottomNavigationView.setSelectedItemId(R.id.Recipe);


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
                    startActivity(new Intent(getApplicationContext(), Cart3_Activity.class));  //자기 페이지 아닌 if문에 넣어주기.
//                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
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

        });}


}