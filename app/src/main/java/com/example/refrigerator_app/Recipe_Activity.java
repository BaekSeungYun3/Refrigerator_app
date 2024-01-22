package com.example.refrigerator_app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Recipe_Activity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private DbOpenHelper mDbOpenHelper;
    private Button buttonFindRecipes;
    private ListView listViewResult;
    private TextView textViewResult;
    private Button gotodb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open(); // 데이터베이스 열기

        buttonFindRecipes = findViewById(R.id.buttonFindRecipes);
        listViewResult = findViewById(R.id.listViewResult);


        //각 테이블에서 재료 가져옴
        List<String> coldDataList = getDataFromTable("cold_table");
        List<String> frzDataList = getDataFromTable("frz_table");
        List<String> roomDataList = getDataFromTable("room_table");
        //가져온 재료 하나의 리스트로 만들고 배열로 저장
        List<String> combinedList = new ArrayList<>();
        combinedList.addAll(coldDataList);
        combinedList.addAll(frzDataList);
        combinedList.addAll(roomDataList);
        String[] combinedArray = combinedList.toArray(new String[combinedList.size()]);
        // 이제 combinedList에는 "cold_table", "frz_table", "room_table" 테이블의 "name" 값들이 모두 포함된 하나의 리스트로 저장

        //메뉴찾기 버튼 클릭
        buttonFindRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String[]> menuData = readMenuDataFromCSV();
                List<String[]> matchingRecipes = getMatchingRecipes(combinedArray, menuData);    //filteredMatchingRecipes(메뉴,재료)반환

                if (matchingRecipes.isEmpty()) {
                    textViewResult.setText("일치하는 메뉴가 없습니다.");
                } else {
                    ArrayAdapter<String[]> menuAdapter = new ArrayAdapter<String[]>(Recipe_Activity.this, android.R.layout.simple_list_item_2, android.R.id.text1, matchingRecipes) {
                        @NonNull
                        @Override
                        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                            View itemView = super.getView(position, convertView, parent);
                            String[] menuData = getItem(position); // matchingRecipes에서 해당 position의 데이터 가져오기
                            TextView textViewMenuName = itemView.findViewById(android.R.id.text1);
                            textViewMenuName.setText(menuData[0]);
                            textViewMenuName.setTextSize(20);
                            TextView textViewIngredients = itemView.findViewById(android.R.id.text2);
                            textViewIngredients.setText(menuData[3]);
                            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) textViewIngredients.getLayoutParams();
                            params.setMargins(0, 8, 0, 0); // Adjust the top margin as per your requirement
                            textViewIngredients.setLayoutParams(params);
                            Log.d("Recipe_Activity", "Find Recipes button clicked");
                            return itemView;
                        }

                    };
                    listViewResult.setAdapter(menuAdapter);
                }
            }
        });



        // 리스트 아이템 클릭 이벤트 핸들러
        listViewResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 클릭된 아이템의 데이터를 가져옵니다.
                String[] clickedRecipe = (String[]) parent.getItemAtPosition(position);

                // RecipeDetailActivity로 전달할 데이터를 포함한 Intent를 생성합니다.
                Intent intent = new Intent(Recipe_Activity.this, RecipeDetailActivity.class);
                intent.putExtra("recipeName", clickedRecipe[0]);
                intent.putExtra("recipeIngredients", clickedRecipe[1]);
                intent.putExtra("recipeMatchingIngredients", clickedRecipe[2]);

                // RecipeDetailActivity를 시작합니다.
                startActivity(intent);
            }
        });
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

                    return true;
                } else if (itemId == R.id.Cart) {
                    startActivity(new Intent(getApplicationContext(), Cart3_Activity.class));  //자기 페이지 아닌 if문에 넣어주기.
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

    //데베ㅔ
    private List<String> getDataFromTable(String tableName) {
        Cursor cursor = mDbOpenHelper.sortColumn(tableName, "name ASC");
        List<String> dataList = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String data = cursor.getString(cursor.getColumnIndex("name"));
                dataList.add(data);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return dataList;
    }


    private List<String[]> readMenuDataFromCSV() {

        List<String[]> menuData = new ArrayList<>();
        InputStream inputStream = getResources().openRawResource(R.raw.c6);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                csvLine = csvLine.replaceAll("^\"|\"$", ""); // 쌍따옴표 제거
                String[] row = csvLine.split(",");
                menuData.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return menuData;

    }


    //매칭
// 매칭
    private List<String[]> getMatchingRecipes(String[] userIngredients, List<String[]> menuData) {
        double requiredMatchingPercentage = 0.4; // 일치하는 재료 비율 설정 (입력 재료가 많아지면 출력되는 메뉴가 적어져서 비율을 좀 줄임)
        List<String[]> matchingRecipes = new ArrayList<>();
        Map<String, Integer> menuCountMap = new HashMap<>(); // menuName과 matchingCount를 저장하는 맵
        // 일치하는 재료로 메뉴 찾기
        for (String[] menuRow : menuData) {
            String nickname = menuRow[0];
            String menuName = menuRow[1];
            String[] menuIngredients = Arrays.copyOfRange(menuRow, 2, menuRow.length);
            int matchingCount = 0;
            List<String> matchingIngredientsList = new ArrayList<>();  // 입력된 재료 중에서 일치하는 재료
            for (String userIngredient : userIngredients) {
                for (String menuIngredient : menuIngredients) {
                    if (userIngredient.trim().equalsIgnoreCase(menuIngredient.trim())) {
                        matchingCount++;
                        matchingIngredientsList.add(menuIngredient);  // 입력된 재료 중에서 일치하는 재료
                        break;
                    }}}
            if (matchingCount >= userIngredients.length * requiredMatchingPercentage) {
                String[] matchingRecipe = new String[4];
                matchingRecipe[0] = menuName;  // 메뉴 이름
                matchingRecipe[1] = TextUtils.join(", ", menuIngredients);  // 메뉴의 필요한 재료
                matchingRecipe[2] = TextUtils.join(", ", matchingIngredientsList);  // 입력 재료 중 일치하는 재료
                matchingRecipe[3] = nickname;
                matchingRecipes.add(matchingRecipe);
                // menuCountMap 업데이트 중복된 메뉴 처리 할 때 사용
                if (menuCountMap.containsKey(menuName)) {
                    int currentCount = menuCountMap.get(menuName);
                    if (matchingCount > currentCount) {
                        menuCountMap.put(menuName, matchingCount);
                    }
                } else {
                    menuCountMap.put(menuName, matchingCount);
                }
            }
        }
        // 중복된 menuName을 가진 항목 중 가장 높은 matchingCount를 가진 항목만 남기기
        List<String[]> filteredMatchingRecipes = new ArrayList<>();
        Set<String> visitedMenuNames = new HashSet<>();  // 방문한 메뉴 이름 저장

        for (String[] matchingRecipe : matchingRecipes) {
            String menuName = matchingRecipe[0];
            int matchingCount = menuCountMap.get(menuName); // 해당 메뉴의 matchingCount를 가져옴

            if (!visitedMenuNames.contains(menuName)) {
                // 첫 번째 등장한 메뉴 이름이라면 무조건 추가
                filteredMatchingRecipes.add(matchingRecipe);
                visitedMenuNames.add(menuName);
            } else {
                // 이미 등장한 메뉴 이름인 경우에는 현재 항목과 기존 항목의 matchingCount를 비교하여 더 큰 값이면 기존 항목 대신에 추가
                int existingMatchingCount = menuCountMap.get(menuName);

                if (matchingCount > existingMatchingCount) {
                    // 현재 항목의 matchingCount가 더 큰 경우에만 교체
                    filteredMatchingRecipes.removeIf(recipe -> recipe[0].equals(menuName));
                    filteredMatchingRecipes.add(matchingRecipe);
                    visitedMenuNames.add(menuName);
                }
            }
        }

        return filteredMatchingRecipes;




    }}