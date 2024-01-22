package com.example.refrigerator_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;


public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private FragmentAdapter adapter;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navi);
        bottomNavigationView.setSelectedItemId(R.id.home);

        //초기 화면을 home으로 즉, FrameLayout(main_frame)에 fragement_home.xml 띄우기
        //     getSupportFragmentManager().beginTransaction().add(R.id.main_frame, new home()).commit();


        tabLayout = findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.viewPager2);

        tabLayout.addTab((tabLayout.newTab().setText("냉장")));
        tabLayout.addTab((tabLayout.newTab().setText("냉동")));
        tabLayout.addTab((tabLayout.newTab().setText("실온")));

        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new FragmentAdapter(fragmentManager, getLifecycle());
        viewPager2.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //여기부터 복붙
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navi);
        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();

                if (itemId == R.id.home) {
                    return true; //자기 페이지에 해당하는 if문에 리턴 트루만 남기기.
                } else if (itemId == R.id.Recipe) {
                    startActivity(new Intent(getApplicationContext(), Recipe_Activity.class));  //자기 페이지 아닌 if문에 넣어주기.
//                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
                } else if (itemId == R.id.Cart) {
                    startActivity(new Intent(getApplicationContext(), Cart2_Activity.class));
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
}