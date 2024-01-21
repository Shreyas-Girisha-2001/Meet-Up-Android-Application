package com.example.meet_up_application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class LogoutScreen extends AppCompatActivity {
    Button yes,no;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_logout_screen);
        yes = findViewById(R.id.logoutyes);
        no = findViewById(R.id.logoutno);
        mAuth= FirebaseAuth.getInstance();

        String username = getIntent().getStringExtra("username");
        String url=getIntent().getStringExtra("imgurl");
        BottomNavigationView bottomNavigationView;
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.logout);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.logout:
                        return true;
                    case R.id.chats:
                       Intent i= new Intent(LogoutScreen.this,MainActivity.class);
                        i.putExtra("username",username);
                        i.putExtra("imgurl",url);
                        startActivity(i);
                        finish();
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.updateprofile:
                        Intent in= new Intent(LogoutScreen.this,updateProfileScreen.class);
                        in.putExtra("username",username);
                        in.putExtra("imgurl",url);
                        startActivity(in);
                        finish();
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               mAuth.signOut();
               startActivity(new Intent(LogoutScreen.this,Login_SignUp.class));
                finish();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(LogoutScreen.this,MainActivity.class);
                i.putExtra("username",username);
                i.putExtra("imgurl",url);
                startActivity(i);
                finish();
            }
        });
    }
}