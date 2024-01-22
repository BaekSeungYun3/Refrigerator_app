package com.example.refrigerator_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Community_Activity extends AppCompatActivity {
    FloatingActionButton fab, fab_follow, fab_main;
    Animation fabOpen, fabClose, rotateForward, rotateBackward;
    boolean isOpen = false;
    RecyclerView recyclerView;
    PostAdapter postAdapter;
    List<Post> postLists;
    List<String> followingList;
    LinearLayoutManager linearLayoutManager;

    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        fab_main = findViewById(R.id.fab_main);
        fab = findViewById(R.id.fab);
        fab_follow = findViewById(R.id.fab_follow);

        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);

        rotateForward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);

        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Community_Activity.this, PostActivity.class);
                startActivity(intent);
                finish();
            }
        });
        fab_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Community_Activity.this, SearchActivity.class);
                startActivity(intent);
                finish();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postLists = new ArrayList<>();
        postAdapter = new PostAdapter(this, postLists);
        recyclerView.setAdapter(postAdapter);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    postLists.add(post);
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navi);
        bottomNavigationView.setSelectedItemId(R.id.Community);

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
                    startActivity(new Intent(getApplicationContext(), Recipe_Activity.class)); //자기 페이지 아닌 if문에 넣어주기
                    finish();
                    return true;
                } else if (itemId == R.id.Cart) {
                    startActivity(new Intent(getApplicationContext(), Cart_Activity.class));
                    finish();
                    return true;
                }else if (itemId == R.id.Community) {
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
    private void animateFab(){
        if (isOpen){
            fab_main.startAnimation(rotateForward);
            fab.startAnimation(fabClose);
            fab_follow.startAnimation(fabClose);
            fab.setClickable(false);
            fab_follow.setClickable(false);
            isOpen=false;
        } else {
            fab_main.startAnimation(rotateBackward);
            fab.startAnimation(fabOpen);
            fab_follow.startAnimation(fabOpen);
            fab.setClickable(true);
            fab_follow.setClickable(true);
            isOpen=true;
        }
    }
}
