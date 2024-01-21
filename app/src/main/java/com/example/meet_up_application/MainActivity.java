package com.example.meet_up_application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.meet_up_application.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseDatabase database,profileurl;
    BottomNavigationView bottomNavigationView;
    FirebaseDatabase filter;
    FirebaseAuth auth;
    ArrayList<usergetset> users;
    UsersAdapter usersAdapter;
    String url;
    String getin1,getin2,getin3,email,username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        filter = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        profileurl = FirebaseDatabase.getInstance();
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.chats);
        users = new ArrayList<>();
        usersAdapter=new UsersAdapter(this,users);
        binding.usercycle.setAdapter(usersAdapter);
        filter.getReference().child("users").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String intrest1=snapshot.child("intrest1").getValue().toString();
                    String intrest2=snapshot.child("intrest2").getValue().toString();
                    String intrest3=snapshot.child("intrest3").getValue().toString();
                    database.getReference().child("users").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            users.clear();
                            if(snapshot.exists()) {
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    if (!snapshot1.child("uid").getValue().toString().equals(auth.getUid())) {
                                        if (snapshot1.child("intrest1").getValue().toString().equals(intrest1) || snapshot1.child("intrest2").getValue().toString().equals(intrest2) || snapshot1.child("intrest3").getValue().toString().equals(intrest3) || snapshot1.child("intrest1").getValue().toString().equals(intrest2) || snapshot1.child("intrest1").getValue().toString().equals(intrest3) || snapshot1.child("intrest2").getValue().toString().equals(intrest1) || snapshot1.child("intrest2").getValue().toString().equals(intrest3) || snapshot1.child("intrest3").getValue().toString().equals(intrest1) || snapshot1.child("intrest3").getValue().toString().equals(intrest2)) {
                                            usergetset user = snapshot1.getValue(usergetset.class);
                                            users.add(user);
                                        }
                                    }
                                }
                                usersAdapter.notifyDataSetChanged();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        profileurl.getReference().child("users").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                        if(snapshot.child("uid").getValue().toString().equals(auth.getUid())) {
                            usergetset usergetset= snapshot.getValue(usergetset.class);
                            url = usergetset.getProfile();
                            getin1 = usergetset.getIntrest1();
                            getin2 = usergetset.getIntrest2();
                            getin3 = usergetset.getIntrest3();
                            email = usergetset.getEmail();
                            username  = usergetset.getUsername();
                        }
                    }
                }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.logout:
                        Intent i =new Intent(MainActivity.this,LogoutScreen.class);
                        i.putExtra("username",username);
                        i.putExtra("imgurl",url);
                        startActivity(i);
                        finish();
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.chats:
                        return true;
                    case R.id.updateprofile:
                        Intent in=new Intent(MainActivity.this,updateProfileScreen.class);
                        in.putExtra("username",username);
                        in.putExtra("imgurl",url);
                        in.putExtra("int1",getin1);
                        in.putExtra("int2",getin2);
                        in.putExtra("int3",getin3);
                        in.putExtra("email",email);
                        startActivity(in);
                        finish();
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

    }
}