package com.example.meet_up_application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends AppCompatActivity {
    VideoView videoView;
    FirebaseAuth mAuth;
    DatabaseReference da;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash_screen);
        mAuth = FirebaseAuth.getInstance();
        da = FirebaseDatabase.getInstance().getReference();
        videoView = (VideoView) findViewById(R.id.videoView);
        String videopath= new StringBuilder("android.resource://").append(getPackageName()).append("/raw/splashscreen").toString();
        videoView.setVideoPath(videopath);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mAuth.getCurrentUser()!=null){
                            String uid = mAuth.getUid();
                            da.child("users").child(uid).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        String username= snapshot.child("username").getValue().toString();
                                        Intent i = new Intent(SplashScreen.this,MainActivity.class);
                                        i.putExtra("username",username);
                                        startActivity(i);
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        else {
                            Intent i = new Intent(SplashScreen.this, Login_SignUp.class);
                            startActivity(i);
                            finish();
                        }
                    }
                },300);
            }
        });

        videoView.start();
    }
}
